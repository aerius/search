/*
 * Copyright the State of the Netherlands
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package nl.aerius.search.tasks;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchRegion;
import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.domain.SearchSuggestionBuilder;
import nl.aerius.search.domain.SearchSuggestionType;
import nl.aerius.search.domain.SearchTaskResult;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

@Component
@ImplementsCapability(value = SearchCapability.BASIC_INFO, region = SearchRegion.UK)
public class BingSearchService implements SearchTaskService {
  private static final String REGION = "GB";
  private static final String CULTURE = "en-GB";
  private static final String BNG_BOUNDS = "49.79,-8.82,60.94,1.92";
  private static final String PDOK_SUGGEST_ENDPOINT = "https://dev.virtualearth.net/REST/v1/Locations?query=%s"
      + "&key=%s&include=ciso2&userRegion=" + REGION + "&c=" + CULTURE + "&userMapView=" + BNG_BOUNDS;

  private static final Logger LOG = LoggerFactory.getLogger(BingSearchService.class);

  /**
   * TODO: Use session key s rather than the maps key
   * https://docs.microsoft.com/en-us/bingmaps/getting-started/bing-maps-api-best-practices#reducing-usage-transactions
   */
  @Value("${nl.aerius.bing.apiKey:#{null}}") private String apiKey;

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    final SearchTaskResult result = new SearchTaskResult();
    final List<SearchSuggestion> sugs = new ArrayList<>();
    if (apiKey != null) {
      retrieveSuggestions(query, sugs);
    }

    result.setSuggestions(sugs);

    return Single.just(result);
  }

  private void retrieveSuggestions(final String query, final List<SearchSuggestion> sugs) {
    final String url = String.format(PDOK_SUGGEST_ENDPOINT, query, apiKey);

    final HttpResponse<JsonNode> json = Unirest.get(url).asJson();
    final JSONObject body = json.getBody().getObject();

    final JSONArray arr = body.getJSONArray("resourceSets").getJSONObject(0).getJSONArray("resources");
    for (int i = 0; i < arr.length(); i++) {
      final JSONObject jsonObject = arr.getJSONObject(i);

      // Skip if the result is not in GB
      if (!jsonObject.getJSONObject("address").getString("countryRegionIso2").equals(REGION)) {
        continue;
      }

      final SearchSuggestion sug = createSuggestion(jsonObject);
      sugs.add(sug);
    }
  }

  private static SearchSuggestion createSuggestion(final JSONObject jsonObject) {
    final String id = jsonObject.getString("name").replace(" ", "-").toLowerCase();
    final String displayText = jsonObject.getString("name");
    final double score = getScoreFromConfidence(jsonObject.getString("confidence"));
    final SearchSuggestionType type = determineType(jsonObject.getString("entityType"));
    final String wktCentroid = "POLYGON((" + jsonObject.getJSONArray("bbox").join(" ") + "))";
    final String wktGeometry = "POINT((" + jsonObject.getJSONObject("point").getJSONArray("coordinates").join(" ") + "))";

    final SearchSuggestion suggestion = SearchSuggestionBuilder.create(displayText, score, type, wktCentroid, wktGeometry);
    suggestion.setId(id);
    return suggestion;
  }

  private static double getScoreFromConfidence(final String confidence) {
    switch (confidence) {
    case "High":
      return 90D;
    case "Medium":
      return 50D;
    case "Low":
      return 30D;
    default:
      return 15D;
    }
  }

  private static SearchSuggestionType determineType(final String type) {
    SearchSuggestionType suggestionType;

    switch (type) {
    case "AdminDivision1":
      suggestionType = SearchSuggestionType.TEXT;
      break;
    case "AdminDivision2":
      suggestionType = SearchSuggestionType.MUNICIPALITY;
      break;
    case "PopulatedPlace":
      suggestionType = SearchSuggestionType.CITY;
      break;
    case "Address":
      suggestionType = SearchSuggestionType.ADDRESS;
      break;
    case "Postcode2":
    case "Postcode1":
      suggestionType = SearchSuggestionType.POSTAL_CODE;
      break;
    case "RoadBlock":
      suggestionType = SearchSuggestionType.STREET;
      break;
    default:
      LOG.warn("No mapping for type: {}", type);
      suggestionType = SearchSuggestionType.TEXT;
      break;
    }

    return suggestionType;
  }
}
