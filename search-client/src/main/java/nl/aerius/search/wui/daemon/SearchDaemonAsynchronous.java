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
package nl.aerius.search.wui.daemon;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

import nl.aerius.search.wui.command.SearchTextCommand;
import nl.aerius.search.wui.config.SearchConfiguration;
import nl.aerius.search.wui.context.SearchContext;
import nl.aerius.search.wui.domain.SearchResult;
import nl.aerius.search.wui.service.SearchServiceAsync;
import nl.aerius.wui.dev.GWTProd;
import nl.aerius.wui.event.BasicEventComponent;
import nl.aerius.wui.future.AppAsyncCallback;
import nl.aerius.wui.util.GWTAtomicInteger;
import nl.aerius.wui.util.SchedulerUtil;

/**
 * Asynchronous daemon that takes advantage of the search service's async
 * option.
 *
 * Design goals:
 *
 * <pre>
 * - Retrieve search results asynchronously
 * - Maintain 2 types of results:
 *   - The most actual results
 *   - 'old' results (i.e. results that come in when a new search query is already underway) 
 * - Discard 'old' results only when the most recent search query completes
 * </pre>
 *
 * Usually, it takes at least 2 service calls to complete a search query; the
 * initial request returns partial results, the second (ideally) the complete
 * set. Those two calls take a good part of a second (300ms or so) to complete,
 * so a user, when typing, will likely have struck another stroke within that
 * time. As such, 'old' results will constantly come in as the user is typing.
 * In order to provide a good user experience, these old results should not be
 * discarded, but displayed in the interim of the user starting and finishing
 * his query.
 */
public class SearchDaemonAsynchronous extends BasicEventComponent {
  private static final SearchDaemonAsynchronousEventBinder EVENT_BINDER = GWT.create(SearchDaemonAsynchronousEventBinder.class);

  interface SearchDaemonAsynchronousEventBinder extends EventBinder<SearchDaemonAsynchronous> {}

  private static final int DELAY = 250;

  @Inject SearchContext context;
  @Inject SearchConfiguration config;

  @Inject SearchServiceAsync service;

  private final GWTAtomicInteger counter = new GWTAtomicInteger();

  private String currentSearchId = null;

  private final Function<Integer, AsyncCallback<SearchResult>> callbackFactory = count -> AppAsyncCallback.create(result -> {
    if (!countMatches(count)) {
      processOldResults(result);

      // And try once more if there are no results
      if (result.results.length == 0) {
        fetchOldResults(result.uuid);
      }
      return;
    }

    processResults(result, count);
  }, e -> {
    if (!countMatches(count)) {
      return;
    }

    searchFailure(e);
  });

  @EventHandler
  public void onSearchTextCommand(final SearchTextCommand c) {
    final String query = c.getValue();

    if (query == null || query.trim().isEmpty()) {
      clear();
      return;
    }

    context.beginSearch();
    final int count = counter.incrementAndGet();

    service.startSearchQuery(query, config.getSearchCapabilities(), config.getSearchRegion(), null, callbackFactory.apply(count));
  }

  /**
   * Process old results, by actualizing the cache result set (but not the
   * 'actual' result set).
   */
  private void processOldResults(final SearchResult result) {
    if (result.complete) {
      context.clearCache();
    }

    context.setCacheResults(Stream.of(result.results)
        .collect(Collectors.toList()));
  }

  private void processResults(final SearchResult result, final Integer count) {
    currentSearchId = result.uuid;
    context.addResults(Stream.of(result.results)
        .collect(Collectors.toList()));

    if (result.complete) {
      context.completeSearch();
    } else {
      fetchSearchResults(result.uuid, count);
    }
  }

  private void fetchSearchResults(final String uuid, final Integer count) {
    SchedulerUtil.delay(() -> {
      if (!countMatches(count)) {
        fetchOldResults(uuid);
        return;
      }

      service.retrieveSearchResults(currentSearchId, callbackFactory.apply(count));
    }, DELAY);
  }

  private void fetchOldResults(final String uuid) {
    service.retrieveSearchResults(uuid, AppAsyncCallback.create(stales -> {
      processOldResults(stales);
    }));
  }

  private void clear() {
    currentSearchId = null;
    context.clear();
    counter.incrementAndGet();
  }

  private boolean countMatches(final int count) {
    return count == counter.get();
  }

  private void searchFailure(final Throwable e) {
    context.failSearch();
    GWTProd.error("Could not complete search query: " + e.getMessage(), e);
  }

  @Override
  public void setEventBus(final EventBus eventBus) {
    super.setEventBus(eventBus, this, EVENT_BINDER);
  }
}
