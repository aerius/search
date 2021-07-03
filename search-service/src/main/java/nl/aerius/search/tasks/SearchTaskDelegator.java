package nl.aerius.search.tasks;

import java.util.List;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.tasks.async.SearchResult;

public interface SearchTaskDelegator {
  List<SearchSuggestion> retrieveSearchResults(final String query, final long capabilities);

  String retrieveSearchResultsAsync(final String query, final long capabilities);

  SearchResult retrieveSearchTask(String uuid);
}
