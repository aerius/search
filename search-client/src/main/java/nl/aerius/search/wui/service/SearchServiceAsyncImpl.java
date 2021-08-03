package nl.aerius.search.wui.service;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gwt.user.client.rpc.AsyncCallback;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.wui.config.SearchConfiguration;
import nl.aerius.search.wui.domain.SearchResult;
import nl.aerius.search.wui.domain.SearchSuggestion;
import nl.aerius.wui.util.InteropRequestUtil;

public class SearchServiceAsyncImpl implements SearchServiceAsync {
  private static final String QUERY_FORMAT = "search?query=:query&capabilities=:capabilities&locale=:locale";
  
  private static final String QUERY_ASYNC_FORMAT = "search-async?query=:query&capabilities=:capabilities&locale=:locale";
  private static final String QUERY_ASYNC_CANCEL_FORMAT = "search-async?query=:query&capabilities=:capabilities&locale=:locale&cancel=:cancel";
  
  private static final String RESULT_FORMAT = "results/:uuid";

  @Inject SearchConfiguration cfg;

  @Override
  public void retrieveSearchResults(final String query, final Set<SearchCapability> capabilities, final String locale,
      final AsyncCallback<SearchSuggestion[]> callback) {
    final String url = InteropRequestUtil.prepareUrl(cfg.getSearchEndpoint(), QUERY_FORMAT,
        ":query", query,
        ":capabilities", capabilities.stream().map(v -> v.name()).collect(Collectors.joining(",")),
        ":locale", locale);

    InteropRequestUtil.doGet(url, callback);
  }

  @Override
  public void startSearchQuery(final String query, final Set<SearchCapability> capabilities, final String locale, final String cancel, final AsyncCallback<SearchResult> callback) {
    final String method = cancel == null ? QUERY_ASYNC_FORMAT : QUERY_ASYNC_CANCEL_FORMAT;
    
    final String url = InteropRequestUtil.prepareUrl(cfg.getSearchEndpoint(), method,
        ":query", query,
        ":capabilities", capabilities.stream().map(v -> v.name()).collect(Collectors.joining(",")),
        ":locale", locale,
        ":cancel", cancel);

    InteropRequestUtil.doGet(url, callback);
  }

  @Override
  public void retrieveSearchResults(final String uuid, final AsyncCallback<SearchResult> callback) {
    final String url = InteropRequestUtil.prepareUrl(cfg.getSearchEndpoint(), RESULT_FORMAT,
        ":uuid", uuid);

    InteropRequestUtil.doGet(url, callback);
  }
}
