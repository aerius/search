package nl.aerius.search.tasks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.tasks.async.AsyncSearchTaskDelegator;
import nl.aerius.search.tasks.async.SearchResult;
import nl.aerius.search.tasks.sync.BlockingSearchTaskDelegator;

/**
 * Simple task delegator offering both synchronous and asynchronous operations.
 */
@Component
@Primary
public class SearchTaskDelegatorImpl implements SearchTaskDelegator {
  @Autowired BlockingSearchTaskDelegator blockingDelegator;
  @Autowired AsyncSearchTaskDelegator asyncDelegator;

  @Override
  public List<SearchSuggestion> retrieveSearchResults(final String query, final long capabilities) {
    return blockingDelegator.retrieveSearchResults(query, capabilities);
  }

  @Override
  public String retrieveSearchResultsAsync(final String query, final long capabilities) {
    System.out.println("Starting async search");

    final String id = asyncDelegator.retrieveSearchResultsAsync(query, capabilities);

    System.out.println("Returning task id: " + id);

    return id;
  }

  @Override
  public SearchResult retrieveSearchTask(final String uuid) {
    return asyncDelegator.retrieveSearchTask(uuid);
  }
}
