package nl.aerius.search.tasks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.aerius.search.domain.SearchTaskResult;
import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.domain.SearchSuggestionBuilder;

public abstract class MockSearchTask implements SearchTaskService {
  private static final Logger LOG = LoggerFactory.getLogger(MockSearchTask.class);

  private final long delay;

  public MockSearchTask(final long delay) {
    this.delay = delay;
  }

  @Override
  public SearchTaskResult retrieveSearchResults(final String query) {
    LOG.debug("Retrieving mock search result for query [{}] at delay of {}ms", query, delay);

    try {
      Thread.sleep(delay);
    } catch (final InterruptedException e) {
      // Eat
    }
    
    final SearchSuggestion suggestion = SearchSuggestionBuilder.create("Mock for query [" + query + "] produced after " + delay + "ms");

    final SearchTaskResult result = new SearchTaskResult();
    result.setSuggestions(List.of(suggestion));

    return result;
  }
}
