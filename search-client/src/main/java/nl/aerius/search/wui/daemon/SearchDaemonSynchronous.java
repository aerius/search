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

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

import nl.aerius.search.wui.command.SearchTextCommand;
import nl.aerius.search.wui.config.SearchConfiguration;
import nl.aerius.search.wui.context.SearchContext;
import nl.aerius.search.wui.domain.SearchSuggestion;
import nl.aerius.search.wui.service.SearchServiceAsync;
import nl.aerius.wui.dev.GWTProd;
import nl.aerius.wui.event.BasicEventComponent;
import nl.aerius.wui.future.AppAsyncCallback;
import nl.aerius.wui.util.GWTAtomicInteger;

/**
 * Synchronous daemon that takes advantage of the search service's synchronous
 * search call
 */
public class SearchDaemonSynchronous extends BasicEventComponent {
  private static final SearchDaemonSynchronousEventBinder EVENT_BINDER = GWT.create(SearchDaemonSynchronousEventBinder.class);

  interface SearchDaemonSynchronousEventBinder extends EventBinder<SearchDaemonSynchronous> {}

  @Inject SearchContext context;
  @Inject SearchConfiguration config;

  @Inject SearchServiceAsync service;

  private final GWTAtomicInteger counter = new GWTAtomicInteger();

  @EventHandler
  public void onSearchTextCommand(final SearchTextCommand c) {
    final String query = c.getValue();

    if (query == null || query.trim().isEmpty()) {
      clear();
      return;
    }

    context.beginSearch();
    final int count = counter.incrementAndGet();

    service.retrieveSearchResults(query, config.getSearchCapabilities(), config.getSearchRegion(), AppAsyncCallback.create(v -> {
      if (!countMatches(count)) {
        return;
      }

      completeSearch(v);
    }, e -> {
      if (!countMatches(count)) {
        return;
      }

      searchFailure(e);
    }));
  }

  private void clear() {
    context.clear();
    counter.incrementAndGet();
  }

  private boolean countMatches(final int count) {
    return count == counter.get();
  }

  private void searchFailure(final Throwable e) {
    context.failSearch();
    GWTProd.error("Could not complete search: " + e.getMessage(), e);
  }

  private void completeSearch(final SearchSuggestion[] results) {
    context.completeSearch();
    context.addResults(Stream.of(results)
        .collect(Collectors.toList()));
  }

  @Override
  public void setEventBus(final EventBus eventBus) {
    super.setEventBus(eventBus, this, EVENT_BINDER);
  }
}
