package nl.aerius.search.tasks;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import nl.aerius.search.domain.SearchCapability;
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
  public List<SearchSuggestion> retrieveSearchResults(final String query, final Set<SearchCapability> capabilities) {
    return blockingDelegator.retrieveSearchResults(query, capabilities);
  }

  @Override
  public SearchResult retrieveSearchResultsAsync(final String query, final Set<SearchCapability> capabilities) {
    return asyncDelegator.retrieveSearchResultsAsync(query, capabilities);
  }

  @Override
  public SearchResult retrieveSearchTask(final String uuid) {
    return asyncDelegator.retrieveSearchTask(uuid);
  }

  @Override
  @Async
  public void cancelSearchTask(final String uuid) {
    asyncDelegator.cancelSearchTask(uuid);
  }
}
