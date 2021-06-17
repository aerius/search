package nl.aerius.tasks;

import java.util.List;

import nl.aerius.domain.SearchCapability;
import nl.aerius.domain.SearchSuggestion;

public class SearchResult {
  private SearchCapability capability;

  private List<SearchSuggestion> suggestions;

  public SearchCapability getCapability() {
    return capability;
  }

  public void setCapability(final SearchCapability capability) {
    this.capability = capability;
  }

  public List<SearchSuggestion> getSuggestions() {
    return suggestions;
  }

  public void setSuggestions(final List<SearchSuggestion> suggestions) {
    this.suggestions = suggestions;
  }
}
