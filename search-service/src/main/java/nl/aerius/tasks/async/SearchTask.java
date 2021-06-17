package nl.aerius.tasks.async;

import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.aerius.domain.SearchSuggestion;
import nl.aerius.tasks.SearchResult;

public class SearchTask {
  private static final Logger LOG = LoggerFactory.getLogger(SearchTask.class);

  private final Set<SearchSuggestion> results = new TreeSet<>((o1, o2) -> o1.getDescription().compareTo(o2.getDescription()));

  private boolean complete;

  private final String uuid;

  public SearchTask(final String uuid) {
    this.uuid = uuid;
  }

  public void complete(final SearchResult result) {
    LOG.debug("Completing {} results for search task {}", result.getSuggestions().size(), uuid);
    results.addAll(result.getSuggestions());
  }

  public void complete() {
    LOG.debug("Search task {} completed.", uuid);
    setComplete(true);
  }

  public boolean isComplete() {
    return complete;
  }

  public void setComplete(final boolean complete) {
    this.complete = complete;
  }

  public Set<SearchSuggestion> getResults() {
    return results;
  }
}
