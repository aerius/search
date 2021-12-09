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
package nl.aerius.search.tasks.async;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.domain.SearchTaskResult;

public class SearchResult {
  private static final Logger LOG = LoggerFactory.getLogger(SearchResult.class);

  private final String uuid;

  private final TreeSet<SearchSuggestion> results;

  private boolean complete;

  public SearchResult(final String uuid, final Comparator<SearchSuggestion> comparator) {
    this.uuid = uuid;
    results = new TreeSet<>(comparator);
  }

  public boolean isComplete() {
    return complete;
  }

  public void complete() {
    if (LOG.isTraceEnabled()) {
      LOG.trace("Search task {} has fully completed.", uuid);
    }

    complete = true;
  }

  public List<SearchSuggestion> getResults() {
    // Defensively return a copy of the results in a list
    return new ArrayList<>(results);
  }

  public SearchResult complete(final SearchTaskResult result) {
    if (result.getSuggestions() == null) {
      return this;
    }

    if (LOG.isTraceEnabled()) {
      LOG.trace("Completing delegated subtask for search query {} with {} suggestions.", uuid, result.getSuggestions().size());
    }

    results.addAll(result.getSuggestions());
    return this;
  }

  public String getUuid() {
    return uuid;
  }
}
