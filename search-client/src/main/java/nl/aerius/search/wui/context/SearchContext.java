package nl.aerius.search.wui.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import jsinterop.annotations.JsProperty;

import nl.aerius.search.wui.domain.SearchSuggestion;

@Singleton
public class SearchContext {
  private final @JsProperty Map<String, SearchSuggestion> cache = new HashMap<>();
  private final @JsProperty Map<String, SearchSuggestion> results = new HashMap<>();

  private boolean searching = false;

  public Map<String, SearchSuggestion> getResults() {
    final Map<String, SearchSuggestion> ret = new HashMap<>();

    ret.putAll(cache);
    ret.putAll(results);

    return ret;
  }

  public void addResults(final Collection<SearchSuggestion> results) {
    this.results.putAll(results.stream()
        .collect(Collectors.toMap(v -> v.id, v -> v)));
  }

  public void setCacheResults(final Collection<SearchSuggestion> results) {
    this.cache.putAll(results.stream()
        .collect(Collectors.toMap(v -> v.id, v -> v)));
  }

  public boolean isSearching() {
    return searching;
  }

  public void setSearching(final boolean searching) {
    this.searching = searching;
  }

  public void beginSearch() {
    setSearching(true);
    cache.putAll(results);
    results.clear();
  }

  public void completeSearch() {
    setSearching(false);
    clearCache();
  }

  public void clear() {
    setSearching(false);
    results.clear();
  }

  public void failSearch() {
    clear();
  }

  public void clearCache() {
    cache.clear();
  }
}
