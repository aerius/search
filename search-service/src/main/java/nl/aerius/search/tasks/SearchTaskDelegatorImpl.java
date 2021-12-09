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
package nl.aerius.search.tasks;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.tasks.async.AsyncSearchTaskDelegator;
import nl.aerius.search.tasks.async.SearchResult;
import nl.aerius.search.tasks.sync.BlockingSearchTaskDelegator;

/**
 * Simple task delegator offering both synchronous and asynchronous operations.
 */
@Component
@Primary
public class SearchTaskDelegatorImpl implements SearchTaskDelegator {
  @Autowired BlockingSearchTaskDelegator blockingDelegator;
  @Autowired AsyncSearchTaskDelegator asyncDelegator;

  @Override
  public List<SearchSuggestion> retrieveSearchResults(final String query, final Set<CapabilityKey> capabilities) {
    return blockingDelegator.retrieveSearchResults(query, capabilities);
  }

  @Override
  public SearchResult retrieveSearchResultsAsync(final String query, final Set<CapabilityKey> capabilities) {
    return asyncDelegator.retrieveSearchResultsAsync(query, capabilities);
  }

  @Override
  public SearchResult retrieveSearchTask(final String uuid) {
    return asyncDelegator.retrieveSearchTask(uuid);
  }

  @Override
  @Async
  public void cancelSearchTask(final String uuid) {
    asyncDelegator.cancelSearchTask(uuid);
  }
}
