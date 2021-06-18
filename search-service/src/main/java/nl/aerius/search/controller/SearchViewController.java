package nl.aerius.search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.tasks.BlockingSearchTaskDelegator;

/**
 * A simple
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

    model.addAttribute("results", results);
    
    return "synchronous-results";
  }
}
