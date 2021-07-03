package nl.aerius.search.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    return "form";
  }

  @GetMapping(value = { "/results" })
  public String search(final String query, final long capabilities, final Model model) {
    final long timeStart = System.currentTimeMillis();

    final List<SearchSuggestion> results = delegator.retrieveSearchResults(query, capabilities);

    final long timeEnd = System.currentTimeMillis();

    model.addAttribute("duration", timeEnd - timeStart);

    model.addAttribute("query", query);
    model.addAttribute("capabilities", capabilities);
    model.addAttribute("capabilityNames", TaskUtils.findCapabilities(capabilities)
        .stream().map(v -> v.name())
        .collect(Collectors.joining(", ")));

    model.addAttribute("results", results);

    return "synchronous-results";
  }
}
