package nl.aerius.search.domain;

public final class SearchSuggestionBuilder {
  private SearchSuggestionBuilder() {}

  public static SearchSuggestion create(final String txt) {
    final SearchSuggestion suggestion = new SearchSuggestion();
    
    suggestion.setDescription(txt);

    return suggestion;
  }
}
