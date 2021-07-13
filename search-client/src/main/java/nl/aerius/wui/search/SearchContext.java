package nl.aerius.wui.search;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import com.axellience.vuegwt.core.annotations.component.Data;

import jsinterop.annotations.JsProperty;

@Singleton
public class SearchContext {
  @Data @JsProperty private final List<SearchSuggestion> results = new ArrayList<>();

  @Data private boolean searching;

  public List<SearchSuggestion> getResults() {
    return results;
  }

  public void setResults(final List<SearchSuggestion> lst) {
    this.results.clear();
    this.results.addAll(lst);
  }

  public boolean isSearching() {
    return searching;
  }

  public void setSearching(final boolean searching) {
    this.searching = searching;
  }

  public void beginSearch() {
    setSearching(true);
  }

  public void completeSearch() {
    setSearching(false);
  }

  public void clear() {
    setSearching(false);
    results.clear();
  }
}
