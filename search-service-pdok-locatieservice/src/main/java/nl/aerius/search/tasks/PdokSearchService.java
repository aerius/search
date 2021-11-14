package nl.aerius.search.tasks;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchCapability;
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
  private static final String PDOK_SUGGEST_MODS = "&fl=id,type,weergavenaam,centroide_rd,score";
  private static final String PDOK_SUGGEST_ENDPOINT = "https://geodata.nationaalgeoregister.nl/locatieserver/v3/suggest?q=%s" + PDOK_SUGGEST_MODS;

  private static final Logger LOG = LoggerFactory.getLogger(PdokSearchService.class);

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    final HttpResponse<JsonNode> json = Unirest.get(String.format(PDOK_SUGGEST_ENDPOINT, query)).asJson();

    final JSONObject body = json.getBody().getObject();

    final List<SearchSuggestion> sugs = new ArrayList<>();

    final JSONArray arr = body.getJSONObject("response").getJSONArray("docs");
    for (int i = 0; i < arr.length(); i++) {
      final JSONObject jsonObject = arr.getJSONObject(i);

      final SearchSuggestion sug = createSuggestion(jsonObject);

      LOG.info("Got suggestion: {}", sug);

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

    LOG.info("Centroid: {}", wktCentroid);

    final SearchSuggestion suggestion = SearchSuggestionBuilder.create(displayText, score, type, wktCentroid);
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
