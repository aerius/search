package nl.aerius.tasks.mock;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.aerius.domain.SearchSuggestionBuilder;
import nl.aerius.tasks.SearchResult;
import nl.aerius.tasks.SearchTaskService;

public abstract class MockSearchTask implements SearchTaskService {
  private static final Logger LOG = LoggerFactory.getLogger(MockSearchTask.class);

  private final long delay;

  public MockSearchTask(final long delay) {
    this.delay = delay;
  }

  @Override
  public SearchResult retrieveSearchResults(final String query) {
    LOG.debug("Retrieving mock search result for query [{}] at delay of {}ms", query, delay);
    
    try {
      Thread.sleep(delay);
    } catch (final InterruptedException e) {
      // Eat
    }

    final SearchResult result = new SearchResult();
    result.setSuggestions(List.of(SearchSuggestionBuilder.create("Mock for query: [" + query + "] produced after " + delay + "ms")));

    return result;
  }
}
