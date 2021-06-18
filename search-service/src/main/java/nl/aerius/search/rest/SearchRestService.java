package nl.aerius.search.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.tasks.BlockingSearchTaskDelegator;
import nl.aerius.search.tasks.async.AsyncSearchTaskDelegator;
import nl.aerius.search.tasks.async.SearchTask;

@CrossOrigin
@RestController
public class SearchRestService {
  @Autowired BlockingSearchTaskDelegator blockingSearchRepository;
  @Autowired AsyncSearchTaskDelegator asyncSearchRepository;

  /**
   * Retrieve search results based on the given query and capabilities
   * 
   * Accept both GET and POST requests
   */
  @RequestMapping(value = "/api/query")
  public List<SearchSuggestion> retrieveSearchResults(final String query, final long capabilities) {
    return blockingSearchRepository.retrieveSearchResults(query, capabilities);
  }

  @PostMapping(value = "/api/query-async")
  public String retrieveSearchResultsAsync(final String query, final long capabilities) {
    return asyncSearchRepository.retrieveSearchResults(query, capabilities);
  }

  @GetMapping(value = "/api/search-task/{uuid}")
  public SearchTask retrieveSearchResultsAsync(final @PathVariable("uuid") String uuid) {
    return asyncSearchRepository.retrieveSearchTask(uuid);
  }
}
