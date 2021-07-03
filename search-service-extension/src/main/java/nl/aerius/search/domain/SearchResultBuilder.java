package nl.aerius.search.domain;

import java.util.Arrays;

public final class SearchResultBuilder {
  private SearchResultBuilder() {}

  public static SearchTaskResult of(final SearchSuggestion... lst) {
    final SearchTaskResult result = new SearchTaskResult();

    result.setSuggestions(Arrays.asList(lst));

    return result;
  }

  public static SearchTaskResult empty() {
    return of();
  }
}
