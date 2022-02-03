package nl.aerius.search.tasks.receptor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.domain.SearchSuggestionType;
import nl.aerius.search.domain.SearchTaskResult;
import nl.aerius.search.tasks.coordinate.RDNewCoordinateSearchService;

class RDNewCoordinateSearchServiceTest {
  private RDNewCoordinateSearchService service;

  @BeforeEach
  void beforeEach() {
    service = new RDNewCoordinateSearchService();
  }

  @Test
  void testCoordinateNonNull() {
    final Single<SearchTaskResult> single = service.retrieveSearchResults("x:123123 y:456456");
    final SearchTaskResult result = single.blockingGet();

    assertEquals(2, result.getSuggestions().size(), "Result number should be 2");
    assertEquals(1, result.getSuggestions().stream()
        .map(SearchSuggestion::getType)
        .filter(v -> v == SearchSuggestionType.COORDINATE)
        .count(), "Expecting 1 receptor result.");
    assertEquals(1, result.getSuggestions().stream()
        .map(SearchSuggestion::getType)
        .filter(v -> v == SearchSuggestionType.RECEPTOR)
        .count(), "Expecting 1 receptor result.");
  }

  @Test
  void testCoordinateNull() {
    final Single<SearchTaskResult> single = service.retrieveSearchResults("nothing");
    final SearchTaskResult result = single.blockingGet();

    assertEquals(0, result.getSuggestions().size(), "Result number should be 0");
  }
}
