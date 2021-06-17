package nl.aerius.tasks.mock;

import java.util.List;

import nl.aerius.domain.SearchSuggestion;
import nl.aerius.domain.SearchSuggestionBuilder;
import nl.aerius.tasks.SearchTaskService;

public abstract class MockSearchTask implements SearchTaskService {
  private final long delay;

  public MockSearchTask(final long delay) {
    this.delay = delay;
  }

  @Override
  public List<SearchSuggestion> retrieveSearchResults(final String query) {
    try {
      Thread.sleep(delay);
    } catch (final InterruptedException e) {
      // Eat
    }

    return List.of(SearchSuggestionBuilder.create("Mock for query: " + query + " -- this suggestion was produced after " + delay + "ms"));
  }
}
