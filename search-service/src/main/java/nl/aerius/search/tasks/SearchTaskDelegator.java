package nl.aerius.search.tasks;

import java.util.List;
import java.util.Set;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.tasks.async.SearchResult;

public interface SearchTaskDelegator {
  List<SearchSuggestion> retrieveSearchResults(final String query, final Set<SearchCapability> capabilities);

  String retrieveSearchResultsAsync(final String query, final Set<SearchCapability> capabilities);

  SearchResult retrieveSearchTask(String uuid);
}
