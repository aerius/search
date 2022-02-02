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
package nl.aerius.search.domain;

public final class SearchSuggestionBuilder {
  public static final double MAX_SCORE = 100;

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

  public static SearchSuggestion create(final String txt, final double score, final SearchSuggestionType type, final String wktCentroid) {
    final SearchSuggestion sug = create(txt, score, type);
    sug.setCentroid(wktCentroid);
    return sug;
  }

  public static SearchSuggestion create(final String txt, final double score, final SearchSuggestionType type, final String wktCentroid,
      final String wktGeometry) {
    final SearchSuggestion sug = create(txt, score, type, wktCentroid);
    sug.setGeometry(wktGeometry);
    return sug;
  }
}
