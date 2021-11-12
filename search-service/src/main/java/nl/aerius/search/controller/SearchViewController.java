package nl.aerius.search.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.tasks.TaskUtils;
import nl.aerius.search.tasks.sync.BlockingSearchTaskDelegator;

/**
 * A simple front-end
 */
@Controller
public class SearchViewController {
  @Autowired BlockingSearchTaskDelegator delegator;

  @GetMapping(value = { "/" })
  public String searchForm(final Model model) {
    return "synchronous-form";
  }

  @GetMapping(value = { "/async" })
  public String searchFormAsync(final Model model) {
    return "asynchronous-form";
  }

  @GetMapping(value = { "/results" })
  public String search(final String query, @RequestParam final List<String> capabilities, final String region, final Model model) {
    final long timeStart = System.currentTimeMillis();

    final List<SearchSuggestion> results = delegator.retrieveSearchResults(query, TaskUtils.parseCapabilities(capabilities, region));

    final long timeEnd = System.currentTimeMillis();

    model.addAttribute("duration", timeEnd - timeStart);

    model.addAttribute("query", query);
    model.addAttribute("region", region);
    model.addAttribute("capabilities", capabilities.stream()
        .collect(Collectors.joining(", ")));

    model.addAttribute("results", results);

    return "synchronous-results";
  }
}
