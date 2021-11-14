package nl.aerius.search.wui.config;

import java.util.Set;

import nl.aerius.search.domain.SearchCapability;

public interface SearchConfiguration {
  String getSearchEndpoint();
  
  Set<SearchCapability> getSearchCapabilities();
  
  String getSearchRegion();
}
