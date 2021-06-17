package nl.aerius.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.aerius.domain.SearchSuggestion;
import nl.aerius.tasks.SearchTaskDelegator;

@CrossOrigin
@RestController
public class SearchRestService {
  @Autowired SearchTaskDelegator searchRepository;

  /**
   * Retrieve search results based on the given query and capabilities
   * 
   * Accept both GET and POST requests
   */
  @RequestMapping(value = "/api/query")
  public List<SearchSuggestion> retrieveSearchResults(final String query, final long capabilities) {
    return searchRepository.retrieveSearchResults(query, capabilities);
  }

  @GetMapping(value = "/api/query-async")
  public String retrieveSearchResultsAsync(final String query, final long capabilities) {
    // TODO Support asynchronous queries
    return "unavailable";
  }
}
