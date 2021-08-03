package nl.aerius.search.wui.daemon;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.axellience.vuegwt.core.annotations.component.Data;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.wui.command.SearchTextCommand;
import nl.aerius.search.wui.context.SearchContext;
import nl.aerius.search.wui.domain.SearchSuggestion;
import nl.aerius.search.wui.service.SearchServiceAsync;
import nl.aerius.search.wui.util.SearchUtils;
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

  private static final Set<SearchCapability> CAPS = SearchUtils.of(SearchCapability.MOCK_0, SearchCapability.MOCK_1,
      SearchCapability.RECEPTOR, SearchCapability.MOCK_GROUP_0, SearchCapability.MOCK_GROUP_1);

  @Inject @Data SearchContext context;

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

    service.retrieveSearchResults(query, CAPS, "nl", AppAsyncCallback.create(v -> {
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
