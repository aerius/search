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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
  private static final String BING_SUGGEST_ENDPOINT = "https://dev.virtualearth.net/REST/v1/Autosuggest?query=%s"
      + "&key=%s"
      + "&userRegion=" + REGION
      + "&countryFilter=" + REGION
      + "&includeEntityTypes=Address,Place"
      + "&c=" + CULTURE
      + "&userMapView=" + BNG_BOUNDS
      + "&maxResults=5";
  private static final String BING_LOCATIONS_ENDPOINT = "https://dev.virtualearth.net/REST/v1/Locations?"
      + "key=%s"
      + "&userRegion=" + REGION
      + "&c=" + CULTURE
      + "&userMapView=" + BNG_BOUNDS
      + "&maxResults=1"
      + "%s";

  private static final Logger LOG = LoggerFactory.getLogger(BingSearchService.class);

  @Autowired SimpleBNGGeometryTransformer transformer;

  /**
   * TODO: Use session key s rather than the maps key
   * https://docs.microsoft.com/en-us/bingmaps/getting-started/bing-maps-api-best-practices#reducing-usage-transactions
   */
  @Value("${nl.aerius.bing.apiKey:#{null}}") private String apiKey;

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    final SearchTaskResult result = new SearchTaskResult();
    final Map<String, SearchSuggestion> sugs = new HashMap<>();
    if (apiKey != null) {
      retrieveSuggestions(query, sugs);
    } else {
      LOG.warn("BingSearchService will be no-op because there is no api key (env nl.aerius.bing.apiKey)");
    }

    result.setSuggestions(new ArrayList<>(sugs.values()));

    return Single.just(result);
  }

  private void retrieveSuggestions(final String query, final Map<String, SearchSuggestion> sugs) {
    final String url = String.format(BING_SUGGEST_ENDPOINT, query, apiKey);

    // First obtain suggestions, then translate them to actual locations.
    final JSONArray arr = obtainResources(url).getJSONObject(0)
        .getJSONArray("value");
    // As the list of suggestions can contain duplicates when we translate them to actual locations, filter those out by using a set
    // Use a LinkedHashSet to keep the order.
    final Set<SuggestedLocation> suggestedLocations = new LinkedHashSet<>();
    for (int i = 0; i < arr.length(); i++) {
      final JSONObject jsonObject = arr.getJSONObject(i);
      final SuggestedLocation suggestedLocation = createSuggestedLocation(jsonObject);
      suggestedLocations.add(suggestedLocation);
    }
    // Now convert the suggestions by Bing to actual locations, to obtain geo information.
    int i = 0;
    for (final SuggestedLocation suggestedLocation : suggestedLocations) {
      final JSONObject jsonObject = obtainLocation(suggestedLocation);
      if (jsonObject != null) {
        final SearchSuggestion sug = createSuggestion(query, i, jsonObject);
        sugs.merge(sug.getDescription(), sug, (a, b) -> a.getScore() > b.getScore() ? a : b);
        i++;
      }
    }
  }

  private JSONArray obtainResources(final String url) {
    final HttpResponse<JsonNode> json = Unirest.get(url).asJson();
    final JSONObject body = json.getBody().getObject();
    return body.getJSONArray("resourceSets").getJSONObject(0).getJSONArray("resources");
  }

  private SuggestedLocation createSuggestedLocation(final JSONObject jsonObject) {
    final JSONObject addressObject = jsonObject.getJSONObject("address");
    return new SuggestedLocation(jsonObject.optString("name"),
        addressObject.optString("locality"),
        addressObject.optString("adminDistrict2"),
        addressObject.optString("addressLine"),
        addressObject.optString("formattedAddress"));
  }

  private JSONObject obtainLocation(final SuggestedLocation location) {
    final String url;
    if (location.name != null && !location.name.isEmpty()) {
      url = String.format(BING_LOCATIONS_ENDPOINT, apiKey, "&query=" + location.name);
    } else {
      url = String.format(BING_LOCATIONS_ENDPOINT, apiKey, location.toAddressUrlParameters());
    }
    final JSONArray arr = obtainResources(url);
    return arr.length() == 0 ? null : arr.getJSONObject(0);
  }

  private SearchSuggestion createSuggestion(final String query, final int idx, final JSONObject jsonObject) {
    final String id = "id-" + query + "-" + idx;
    final String displayText = jsonObject.getString("name");
    // While there is a confidence bit, as we use a 2nd call we can't really use it
    /// (the confidence is always based on input for that second call, which isn't user input)
    // Instead, assume that Bing knows what it's doing with the autosuggest and score earlier records higher
    final double score = 90D - idx;
    final SearchSuggestionType type = determineType(jsonObject.getString("entityType"));
    final JSONArray bbox = jsonObject.getJSONArray("bbox");
    final String wktBbox = "POLYGON(("
        + bbox.getDouble(0) + " " + bbox.getDouble(1) + ", "
        + bbox.getDouble(2) + " " + bbox.getDouble(3) + ", "
        + bbox.getDouble(2) + " " + bbox.getDouble(1) + ", "
        + bbox.getDouble(0) + " " + bbox.getDouble(3) + ", "
        + bbox.getDouble(0) + " " + bbox.getDouble(1)
        + "))";
    final String wktCentroid = "POINT(" + jsonObject.getJSONObject("point").getJSONArray("coordinates").join(" ") + ")";

    final String wktBboxBng = transformer.toBNGWKT(wktBbox);
    final String wktCentroidBng = transformer.toBNGWKT(wktCentroid);

    final SearchSuggestion suggestion = SearchSuggestionBuilder.create(displayText, score, type, wktCentroidBng, null, wktBboxBng);
    suggestion.setId(id);
    return suggestion;
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

  private static class SuggestedLocation {

    private final String name;
    private final String locality;
    private final String adminDistrict;
    private final String addressLine;
    private final String formattedAddress;

    SuggestedLocation(final String name, final String locality, final String adminDistrict, final String addressLine, final String formattedAddress) {
      this.name = name;
      this.locality = locality;
      this.adminDistrict = adminDistrict;
      this.addressLine = addressLine;
      this.formattedAddress = formattedAddress;
    }

    @Override
    public int hashCode() {
      return Objects.hash(addressLine, adminDistrict, formattedAddress, locality, name);
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      final SuggestedLocation other = (SuggestedLocation) obj;
      return Objects.equals(addressLine, other.addressLine) && Objects.equals(adminDistrict, other.adminDistrict)
          && Objects.equals(formattedAddress, other.formattedAddress) && Objects.equals(locality, other.locality)
          && Objects.equals(name, other.name);
    }

    String toAddressUrlParameters() {
      final Map<String, String> parameters = new HashMap<>();
      parameters.put("countryRegion", REGION);
      if (locality != null) {
        parameters.put("locality", locality);
      }
      if (adminDistrict != null) {
        parameters.put("adminDistrict", adminDistrict);
      }
      if (addressLine != null) {
        parameters.put("addressLine", addressLine);
      }
      if (formattedAddress != null) {
        parameters.put("query", formattedAddress);
      }
      return parameters.isEmpty()
          ? ""
          : "&" + parameters.entrySet().stream()
              .map(e -> e.getKey() + "=" + e.getValue())
              .collect(Collectors.joining("&"));
    }
  }
}
