package nl.aerius.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import nl.aerius.domain.SearchSuggestion;
import nl.aerius.rest.SearchRestService;
import nl.aerius.tasks.SearchTaskDelegator;

@Controller
public class SearchViewController {
  private static final Logger LOG = LoggerFactory.getLogger(SearchRestService.class);

  @Autowired SearchTaskDelegator delegator;

  @GetMapping(value = { "/" })
  public String searchForm(final Model model) {
    return "form";
  }

  @GetMapping(value = { "/results" })
  public String search(final String query, final long capabilities, final Model model) {
    final List<SearchSuggestion> results = delegator.retrieveSearchResults(query, capabilities);

    model.addAttribute("query", query);
    model.addAttribute("capabilities", capabilities);

    model.addAttribute("results", results);
    return "results";
  }
}
