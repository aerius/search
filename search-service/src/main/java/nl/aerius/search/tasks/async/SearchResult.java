package nl.aerius.search.tasks.async;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.aerius.search.domain.SearchSuggestion;

public class SearchResult {
  private static final Logger LOG = LoggerFactory.getLogger(SearchResult.class);

  private final String uuid;

  private Future<List<SearchSuggestion>> future;

  public SearchResult(final String uuid) {
    this.uuid = uuid;
  }

  public boolean isComplete() {
    return future.isDone();
  }

  public List<SearchSuggestion> getResults() {
    try {
      return future.get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public void initialize(final Future<List<SearchSuggestion>> future) {
    if (future != null) {
      throw new IllegalStateException("Initializing a task twice is not allowed.");
    }

    this.future = future;
  }

  public String getUuid() {
    return uuid;
  }
}
