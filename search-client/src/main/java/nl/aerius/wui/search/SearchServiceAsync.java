package nl.aerius.wui.search;

import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;

import nl.aerius.search.domain.SearchCapability;

@ImplementedBy(SearchServiceAsyncImpl.class)
public interface SearchServiceAsync {
  void retrieveSearchResults(String query, Set<SearchCapability> capabilities, String locale, AsyncCallback<SearchSuggestion[]> callback);
  
  void startSearchQuery(String query, Set<SearchCapability> capabilities, String locale, String cancel, AsyncCallback<SearchResult> callback);

  void retrieveSearchResults(String uuid, AsyncCallback<SearchResult> callback);
}
