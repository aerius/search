package nl.aerius.search.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.tasks.SearchTaskDelegator;
import nl.aerius.search.tasks.async.SearchResult;

@CrossOrigin
@RestController
public class SearchRestService {
  @Autowired SearchTaskDelegator taskDelegator;

  /**
   * Retrieve search results based on the given query and capabilities
   * 
   * Accept both GET and POST requests
   */
  @RequestMapping(value = "/api/query")
  public List<SearchSuggestion> retrieveSearchResults(final String query, final long capabilities) {
    return taskDelegator.retrieveSearchResults(query, capabilities);
  }

  @RequestMapping(value = "/api/query-async")
  public String retrieveSearchResultsAsync(final String query, final long capabilities) {
    return taskDelegator.retrieveSearchResultsAsync(query, capabilities);
  }

  @GetMapping(value = "/api/search-task/{uuid}")
  public SearchResult retrieveSearchResultsAsync(final @PathVariable("uuid") String uuid) {
    return taskDelegator.retrieveSearchTask(uuid);
  }
}
