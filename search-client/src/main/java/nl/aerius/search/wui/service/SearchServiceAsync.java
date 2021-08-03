package nl.aerius.search.wui.service;

import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.wui.domain.SearchResult;
import nl.aerius.search.wui.domain.SearchSuggestion;

@ImplementedBy(SearchServiceAsyncImpl.class)
public interface SearchServiceAsync {
  void retrieveSearchResults(String query, Set<SearchCapability> capabilities, String locale, AsyncCallback<SearchSuggestion[]> callback);
  
  void startSearchQuery(String query, Set<SearchCapability> capabilities, String locale, String cancel, AsyncCallback<SearchResult> callback);

  void retrieveSearchResults(String uuid, AsyncCallback<SearchResult> callback);
}
