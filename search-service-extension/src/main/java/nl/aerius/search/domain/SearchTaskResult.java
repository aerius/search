package nl.aerius.search.domain;

import java.util.List;

public class SearchTaskResult {
  private List<SearchSuggestion> suggestions;

  public List<SearchSuggestion> getSuggestions() {
    return suggestions;
  }

  public void setSuggestions(final List<SearchSuggestion> suggestions) {
    this.suggestions = suggestions;
  }
}
