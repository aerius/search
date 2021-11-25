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
  private static final String QUERY_FORMAT = "search?query=:query&capabilities=:capabilities&region=:region";
  
  private static final String QUERY_ASYNC_FORMAT = "search-async?query=:query&capabilities=:capabilities&region=:region";
  private static final String QUERY_ASYNC_CANCEL_FORMAT = "search-async?query=:query&capabilities=:capabilities&region=:region&cancel=:cancel";
  
  private static final String RESULT_FORMAT = "results/:uuid";

  @Inject SearchConfiguration cfg;

  @Override
  public void retrieveSearchResults(final String query, final Set<SearchCapability> capabilities, final String region,
      final AsyncCallback<SearchSuggestion[]> callback) {
    final String url = InteropRequestUtil.prepareUrl(cfg.getSearchEndpoint(), QUERY_FORMAT,
        ":query", query,
        ":capabilities", capabilities.stream().map(v -> v.name()).collect(Collectors.joining(",")),
        ":region", region);

    InteropRequestUtil.doGet(url, callback);
  }

  @Override
  public void startSearchQuery(final String query, final Set<SearchCapability> capabilities, final String region, final String cancel,
      final AsyncCallback<SearchResult> callback) {
    final String method = cancel == null ? QUERY_ASYNC_FORMAT : QUERY_ASYNC_CANCEL_FORMAT;

    final String url = InteropRequestUtil.prepareUrl(cfg.getSearchEndpoint(), method,
        ":query", query,
        ":capabilities", capabilities.stream().map(v -> v.name()).collect(Collectors.joining(",")),
        ":region", region,
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
