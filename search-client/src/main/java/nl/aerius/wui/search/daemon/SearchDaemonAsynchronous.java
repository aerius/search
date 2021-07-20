package nl.aerius.wui.search.daemon;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.axellience.vuegwt.core.annotations.component.Data;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.wui.dev.GWTProd;
import nl.aerius.wui.event.BasicEventComponent;
import nl.aerius.wui.future.AppAsyncCallback;
import nl.aerius.wui.search.command.SearchTextCommand;
import nl.aerius.wui.search.context.SearchContext;
import nl.aerius.wui.search.domain.SearchResult;
import nl.aerius.wui.search.domain.SearchSuggestion;
import nl.aerius.wui.search.service.SearchServiceAsync;
import nl.aerius.wui.search.util.SearchUtils;
import nl.aerius.wui.util.GWTAtomicInteger;
import nl.aerius.wui.util.SchedulerUtil;

/**
 * Asynchronous daemon that takes advantage of the search service's async option
 */
public class SearchDaemonAsynchronous extends BasicEventComponent {
  private static final SearchDaemonAsynchronousEventBinder EVENT_BINDER = GWT.create(SearchDaemonAsynchronousEventBinder.class);

  interface SearchDaemonAsynchronousEventBinder extends EventBinder<SearchDaemonAsynchronous> {}

  private static final Set<SearchCapability> CAPS = SearchUtils.of(SearchCapability.MOCK_0, SearchCapability.MOCK_1,
      SearchCapability.RECEPTOR, SearchCapability.MOCK_GROUP_0, SearchCapability.MOCK_GROUP_1);

  private static final int DELAY = 250;

  @Inject @Data SearchContext context;

  @Inject SearchServiceAsync service;

  private final GWTAtomicInteger counter = new GWTAtomicInteger();

  private String currentSearchId = null;

  private final Function<Integer, AsyncCallback<SearchResult>> callbackFactory = count -> AppAsyncCallback.create(v -> {
    if (!countMatches(count)) {
      return;
    }

    processResults(v, count);
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

    service.startSearchQuery(query, CAPS, "nl", currentSearchId, callbackFactory.apply(count));
  }

  private void processResults(final SearchResult result, final Integer count) {
    currentSearchId = result.uuid;
    context.addResults(Stream.of(result.results)
        .collect(Collectors.toList()));

    if (result.complete) {
      context.completeSearch();
    } else {
      fetchSearchResults(count);
    }
  }

  private void fetchSearchResults(final Integer count) {
    SchedulerUtil.delay(() -> {
      if (!countMatches(count)) {
        return;
      }

      service.retrieveSearchResults(currentSearchId, callbackFactory.apply(count));
    }, DELAY);
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
