package nl.aerius.tasks;

import java.util.List;

import nl.aerius.domain.SearchSuggestion;

public interface SearchTaskService {
  List<SearchSuggestion> retrieveSearchResults(String query);
}
