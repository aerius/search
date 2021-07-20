package nl.aerius.search.domain;

public final class SearchSuggestionBuilder {
  private SearchSuggestionBuilder() {}

  public static SearchSuggestion create(final String txt) {
    final SearchSuggestion suggestion = new SearchSuggestion();

    suggestion.setDescription(txt);
    suggestion.setId(String.valueOf(txt.hashCode()));

    return suggestion;
  }

  public static SearchSuggestion create(final String txt, final String group) {
    final SearchSuggestion sug = create(txt);
    sug.setGroup(group);
    return sug;
  }
}
