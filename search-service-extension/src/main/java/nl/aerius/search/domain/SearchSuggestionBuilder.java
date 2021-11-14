package nl.aerius.search.domain;

public final class SearchSuggestionBuilder {
  private SearchSuggestionBuilder() {}

  public static SearchSuggestion create(final String txt) {
    final SearchSuggestion suggestion = new SearchSuggestion();

    suggestion.setDescription(txt);
    suggestion.setId(String.valueOf(txt.hashCode()));
    suggestion.setScore(1);
    suggestion.setType(SearchSuggestionType.TEXT);

    return suggestion;
  }

  public static SearchSuggestion create(final String txt, final double score) {
    final SearchSuggestion sug = create(txt);
    sug.setScore(score);
    return sug;
  }

  public static SearchSuggestion create(final String txt, final double score, final SearchSuggestionType type) {
    final SearchSuggestion sug = create(txt, score);
    sug.setType(type);
    return sug;
  }
}
