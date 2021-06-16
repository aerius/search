package nl.aerius.tasks;

import java.util.List;

import nl.aerius.domain.SearchSuggestion;

public interface SearchTaskDelegator {
  List<SearchSuggestion> retrieveSearchResults(String query, long capabilities);
}
