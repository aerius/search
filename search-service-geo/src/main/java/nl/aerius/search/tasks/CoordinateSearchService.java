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
package nl.aerius.search.tasks;

import org.springframework.stereotype.Component;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.geo.domain.Point;
import nl.aerius.geo.domain.ReceptorPoint;
import nl.aerius.geo.util.ReceptorUtil;
import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchSuggestionBuilder;
import nl.aerius.search.domain.SearchSuggestionType;
import nl.aerius.search.domain.SearchTaskResult;

@Component
@ImplementsCapability(value = SearchCapability.COORDINATE, region = SearchRegion.NL)
public class CoordinateSearchService implements SearchTaskService {

  private static final String COORDINATE_FORMAT = "x:%d y:%d";
  private static final String WKT_POINT_FORMAT = "POINT(%d %d)";

  // Regex used to identify whether an Rijksdriehoekstelsel XY coordinate was entered as search term.
  private static final RegExp SEARCH_TERM_COORDINATE_REGEX = RegExp
      .compile("^\\s*(x\\:)?\\s*([1-2]?\\d{5})\\s*[,;\\s]\\s*(y\\:)?\\s*([3-6]\\d{5})\\s*$", "i");
  // The relevant groups in the above regular expression that identify the X and Y coordinates respectively.
  private static final int SEARCH_TERM_COORDINATE_REGEX_GROUP_X = 2;
  private static final int SEARCH_TERM_COORDINATE_REGEX_GROUP_Y = 4;

  private final ReceptorUtil util;

  public CoordinateSearchService(final ReceptorUtil util) {
    this.util = util;
  }

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    final MatchResult coordinateMatch = SEARCH_TERM_COORDINATE_REGEX.exec(query);
    if (coordinateMatch != null) {
      final int x = Integer.parseInt(coordinateMatch.getGroup(SEARCH_TERM_COORDINATE_REGEX_GROUP_X));
      final int y = Integer.parseInt(coordinateMatch.getGroup(SEARCH_TERM_COORDINATE_REGEX_GROUP_Y));

      final Point point = new Point(x, y);
      final ReceptorPoint rec = util.createReceptorIdFromPoint(point);
      return Single.just(SearchResultBuilder.of(
          SearchSuggestionBuilder.create(String.format(COORDINATE_FORMAT, x, y), 1.0, SearchSuggestionType.COORDINATE,
              String.format(WKT_POINT_FORMAT, x, y)),
          ReceptorUtils.getSearchSuggestion(rec)));
    }

    return Single.just(SearchResultBuilder.empty());
  }
}
