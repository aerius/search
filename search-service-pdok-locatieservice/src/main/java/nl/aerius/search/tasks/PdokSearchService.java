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
@ImplementsCapability(value = SearchCapability.BASIC_INFO, region = SearchRegion.NL)
public class PdokSearchService implements SearchTaskService {
  private static final String PDOK_SUGGEST_MODS = "&fl=id,type,weergavenaam,centroide_rd,geometrie_rd,score";
  private static final String PDOK_SUGGEST_ENDPOINT = "https://geodata.nationaalgeoregister.nl/locatieserver/v3/suggest?q=%s" + PDOK_SUGGEST_MODS;

  private static final Logger LOG = LoggerFactory.getLogger(PdokSearchService.class);

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    final String url = String.format(PDOK_SUGGEST_ENDPOINT, query);
    final HttpResponse<JsonNode> json = Unirest.get(url).asJson();
    final JSONObject body = json.getBody().getObject();

    final List<SearchSuggestion> sugs = new ArrayList<>();
    final JSONArray arr = body.getJSONObject("response").getJSONArray("docs");
    for (int i = 0; i < arr.length(); i++) {
      final JSONObject jsonObject = arr.getJSONObject(i);

      final SearchSuggestion sug = createSuggestion(jsonObject);
      sugs.add(sug);
    }

    final SearchTaskResult result = new SearchTaskResult();
    result.setSuggestions(sugs);

    return Single.just(result);
  }

  private SearchSuggestion createSuggestion(final JSONObject jsonObject) {
    final String id = jsonObject.getString("id");
    final String displayText = jsonObject.getString("weergavenaam");
    final double score = jsonObject.getDouble("score");
    final SearchSuggestionType type = determineType(jsonObject.getString("type"));
    final String wktCentroid = jsonObject.getString("centroide_rd");
    final String wktGeometry = jsonObject.getString("geometrie_rd");

    final SearchSuggestion suggestion = SearchSuggestionBuilder.create(displayText, score, type, wktCentroid, wktGeometry);
    suggestion.setId(id);
    return suggestion;
  }

  private SearchSuggestionType determineType(final String type) {
    SearchSuggestionType suggestionType;

    switch (type) {
    case "adres":
      suggestionType = SearchSuggestionType.ADDRESS;
      break;
    case "woonplaats":
      suggestionType = SearchSuggestionType.CITY;
      break;
    case "gemeente":
      suggestionType = SearchSuggestionType.MUNICIPALITY;
      break;
    case "postcode":
      suggestionType = SearchSuggestionType.POSTAL_CODE;
      break;
    case "weg":
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
