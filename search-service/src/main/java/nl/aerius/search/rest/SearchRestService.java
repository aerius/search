package nl.aerius.search.rest;

import java.util.List;
import java.util.Optional;

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
  private static final String DEFAULT_CAPABILITIES = "MOCK_0,MOCK_01,MOCK_05,RECEPTOR";
  private static final String DEFAULT_REGION = "NL";

  @Autowired SearchTaskDelegator taskDelegator;

  /**
   * Retrieve search results based on the given query and capabilities
   * 
   * Accept both GET and POST requests
   */
  @RequestMapping(value = "/api/search")
  public List<SearchSuggestion> retrieveSearchResults(final String query,
      @RequestParam(defaultValue = DEFAULT_CAPABILITIES) final List<String> capabilities,
      @RequestParam(defaultValue = DEFAULT_REGION) final String region) {
    return taskDelegator.retrieveSearchResults(query, TaskUtils.parseCapabilities(capabilities, region));
  }

  @RequestMapping(value = "/api/search-async")
  public SearchResult retrieveSearchResultsAsync(final String query,
      @RequestParam(defaultValue = DEFAULT_CAPABILITIES) final List<String> capabilities,
      @RequestParam(defaultValue = DEFAULT_REGION) final String region, final Optional<String> cancel) {
    cancel.ifPresent(uuid -> taskDelegator.cancelSearchTask(uuid));

    return taskDelegator.retrieveSearchResultsAsync(query, TaskUtils.parseCapabilities(capabilities, region));
  }

  @GetMapping(value = "/api/results/{uuid}")
  public SearchResult retrieveSearchResultsAsync(final @PathVariable("uuid") String uuid) {
    return taskDelegator.retrieveSearchTask(uuid);
  }
}
