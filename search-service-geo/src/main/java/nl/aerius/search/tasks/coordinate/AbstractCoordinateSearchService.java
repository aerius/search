package nl.aerius.search.tasks.coordinate;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchSuggestionBuilder;
import nl.aerius.search.domain.SearchSuggestionType;
import nl.aerius.search.domain.SearchTaskResult;
import nl.aerius.search.tasks.ReceptorUtils;
import nl.aerius.search.tasks.SearchTaskService;
import nl.overheid.aerius.shared.domain.geo.HexagonZoomLevel;
import nl.overheid.aerius.shared.domain.v2.geojson.Point;
import nl.overheid.aerius.shared.geometry.ReceptorUtil;

public class AbstractCoordinateSearchService implements SearchTaskService {
  private static final String COORDINATE_FORMAT = "x:%d y:%d";
  private static final String WKT_POINT_FORMAT = "POINT(%d %d)";

  // Regex used to identify a x:{x} y:{y} string
  private static final Pattern SEARCH_TERM_COORDINATE_REGEX = Pattern
      .compile("^\\s*(x\\:)?\\s*([1-9]?\\d{5})\\s*[,;\\s]\\s*(y\\:)?\\s*([1-9]\\d{5})\\s*$", Pattern.CASE_INSENSITIVE);

  // The relevant groups in the above regular expression that identify the X and Y
  // coordinates respectively.
  private static final int SEARCH_TERM_COORDINATE_REGEX_GROUP_X = 2;
  private static final int SEARCH_TERM_COORDINATE_REGEX_GROUP_Y = 4;

  private final ReceptorUtil util;
  private final HexagonZoomLevel minZoomLevel;

  public AbstractCoordinateSearchService(final ReceptorUtil util, final HexagonZoomLevel minZoomLevel) {
    this.util = util;
    this.minZoomLevel = minZoomLevel;
  }

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    final MatchResult coordinateMatch = SEARCH_TERM_COORDINATE_REGEX.matcher(query);
    if (coordinateMatch != null) {
      final int x = Integer.parseInt(coordinateMatch.group(SEARCH_TERM_COORDINATE_REGEX_GROUP_X));
      final int y = Integer.parseInt(coordinateMatch.group(SEARCH_TERM_COORDINATE_REGEX_GROUP_Y));

      final Point point = new Point(x, y);
      final int recId = util.getReceptorIdFromPoint(point);
      final Point rec = util.getPointFromReceptorId(recId);
      return Single.just(SearchResultBuilder.of(
          SearchSuggestionBuilder.create(String.format(COORDINATE_FORMAT, x, y), 1.0, SearchSuggestionType.COORDINATE,
              String.format(WKT_POINT_FORMAT, x, y)),
          ReceptorUtils.getSearchSuggestion(recId, rec, minZoomLevel)));
    }

    return Single.just(SearchResultBuilder.empty());
  }
}
