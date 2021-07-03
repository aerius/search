package nl.aerius.search.tasks;

import nl.aerius.search.domain.SearchTaskResult;

public interface SearchTaskService {
  SearchTaskResult retrieveSearchResults(String query);
}
