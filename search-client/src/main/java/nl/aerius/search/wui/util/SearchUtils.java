package nl.aerius.search.wui.util;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nl.aerius.search.domain.SearchCapability;

public final class SearchUtils {
  private SearchUtils() {}

  /**
   * Return the given capabilities as a Set.
   * 
   * Set.of() is not emulated in GWT. 
   */
  public static Set<SearchCapability> of(final SearchCapability... capabilities) {
    return Stream.of(capabilities)
        .collect(Collectors.toSet());
  }
}
