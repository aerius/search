/*
 * Copyright the State of the Netherlands
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
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

  private boolean searchShowing = false;
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

  public boolean isSearchShowing() {
    return searchShowing;
  }

  public void setSearchShowing(final boolean searchShowing) {
    this.searchShowing = searchShowing;
  }

  public void toggleSearchShowing() {
    setSearchShowing(!isSearchShowing());
  }
}
