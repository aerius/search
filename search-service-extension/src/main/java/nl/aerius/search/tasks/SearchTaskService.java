package nl.aerius.search.tasks;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchTaskResult;

public interface SearchTaskService {
  Single<SearchTaskResult> retrieveSearchResults(String query);
}
