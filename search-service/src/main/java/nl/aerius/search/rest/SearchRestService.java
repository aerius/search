package nl.aerius.search.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.tasks.SearchTaskDelegator;
import nl.aerius.search.tasks.TaskUtils;
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
  public List<SearchSuggestion> retrieveSearchResults(final String query, @RequestParam final List<String> capabilities) {
    return taskDelegator.retrieveSearchResults(query, TaskUtils.parseCapabilities(capabilities));
  }

  @RequestMapping(value = "/api/query-async")
  public String retrieveSearchResultsAsync(final String query, @RequestParam final List<String> capabilities) {
    return taskDelegator.retrieveSearchResultsAsync(query, TaskUtils.parseCapabilities(capabilities));
  }

  @GetMapping(value = "/api/search-task/{uuid}")
  public SearchResult retrieveSearchResultsAsync(final @PathVariable("uuid") String uuid) {
    return taskDelegator.retrieveSearchTask(uuid);
  }
}
