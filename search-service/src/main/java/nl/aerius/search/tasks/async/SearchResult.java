package nl.aerius.search.tasks.async;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.domain.SearchTaskResult;

public class SearchResult {
  private static final Logger LOG = LoggerFactory.getLogger(SearchResult.class);

  private final String uuid;

  private final TreeSet<SearchSuggestion> results;

  private boolean complete;

  public SearchResult(final String uuid, final Comparator<SearchSuggestion> comparator) {
    this.uuid = uuid;
    results = new TreeSet<>(comparator);
  }

  public boolean isComplete() {
    return complete;
  }

  public void complete() {
    if (LOG.isTraceEnabled()) {
      LOG.info("Search task {} has fully completed.", uuid);
    }

    complete = true;
  }

  public List<SearchSuggestion> getResults() {
    // Defensively return a copy of the results in a list
    return new ArrayList<>(results);
  }

  public void complete(final SearchTaskResult result) {
    if (result.getSuggestions() == null) {
      return;
    }

    if (LOG.isTraceEnabled()) {
      LOG.trace("Completing delegated subtask for search query {} with {} suggestions.", uuid, result.getSuggestions().size());
    }

    results.addAll(result.getSuggestions());
  }

  public String getUuid() {
    return uuid;
  }
}
