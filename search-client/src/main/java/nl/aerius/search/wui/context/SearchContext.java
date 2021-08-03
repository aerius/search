package nl.aerius.search.wui.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.inject.Singleton;

import com.axellience.vuegwt.core.annotations.component.Data;

import jsinterop.annotations.JsProperty;

import nl.aerius.search.wui.domain.SearchSuggestion;

@Singleton
public class SearchContext {
  @Data @JsProperty private final Map<String, Collection<SearchSuggestion>> results = new HashMap<>();

  @Data private boolean searching;

  private boolean cleanFirst;

  public Map<String, Collection<SearchSuggestion>> getResults() {
    return results;
  }

  public void addResults(final Collection<SearchSuggestion> results) {
    if (cleanFirst) {
      this.results.values().forEach(v -> v.clear());
    }
    
    results.forEach(result -> {
      this.results.computeIfAbsent(result.group, key -> new LinkedHashSet<>());
      this.results.compute(result.group, (v, lst) -> {
        if (!lst.stream()
            .map(e -> e.id)
            .anyMatch(id -> id.equals(result.id))) {
          lst.add(result);
        }
        return lst;
      });
    });
  }

  public boolean isSearching() {
    return searching;
  }

  public void setSearching(final boolean searching) {
    this.searching = searching;
  }

  public void beginSearch() {
    setSearching(true);
    cleanFirst = true;
  }

  public void completeSearch() {
    setSearching(false);
  }

  public void clear() {
    setSearching(false);
    results.clear();
    results.values().forEach(v -> v.clear());
  }

  public void failSearch() {
    clear();
  }
}
