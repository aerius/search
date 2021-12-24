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

import java.util.ArrayList;

import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.domain.SearchSuggestionBuilder;
import nl.aerius.search.domain.SearchSuggestionType;
import nl.aerius.search.domain.SearchTaskResult;
import nl.overheid.aerius.geo.shared.EPSG;
import nl.overheid.aerius.geo.shared.EPSGProxy;
import nl.overheid.aerius.shared.domain.geo.HexagonUtil;
import nl.overheid.aerius.shared.domain.geo.HexagonZoomLevel;
import nl.overheid.aerius.shared.domain.geo.ReceptorGridSettings;
import nl.overheid.aerius.shared.domain.v2.geojson.Point;
import nl.overheid.aerius.shared.domain.v2.geojson.Polygon;
import nl.overheid.aerius.shared.geometry.ReceptorUtil;

public class ReceptorUtils {
  // TODO i18n
  private static final String RECEPTOR_FORMAT = "Receptor %s - x:%s y:%s";

  private ReceptorUtils() {}

  public static ReceptorUtil createReceptorUtil(final int srid, final int minSurfaceArea, final int hexHor) {
    final EPSG epsg = EPSGProxy.getEPSG(srid);
    final ArrayList<HexagonZoomLevel> zoomLevels = new ArrayList<HexagonZoomLevel>();
    for (int i = 1; i <= 5; i++) {
      zoomLevels.add(new HexagonZoomLevel(i, minSurfaceArea));
    }
    return new ReceptorUtil(new ReceptorGridSettings(epsg.getBounds(), epsg, hexHor, zoomLevels));
  }

  public static SearchTaskResult tryParse(final String query, final ReceptorUtil receptorUtil, final HexagonZoomLevel zoomLevel)
      throws NumberFormatException {
    final int id = Integer.parseInt(query);

    final Point rec = receptorUtil.getPointFromReceptorId(id);
    return SearchResultBuilder.of(getSearchSuggestion(id, rec, zoomLevel));
  }

  public static SearchSuggestion getSearchSuggestion(final int id, final Point rec, final HexagonZoomLevel zoomLevel) {
    final String label = String.format(RECEPTOR_FORMAT, id, (int) rec.getX(), (int) rec.getY());

    final String wktCentroid = "POINT(" + rec.getX() + " " + rec.getY() + ")";

    final Polygon createHexagon = HexagonUtil.createHexagon(rec, zoomLevel);

    // TODO Format as WKT
    final String wktGeometry = "";
    return SearchSuggestionBuilder.create(label, 100, SearchSuggestionType.RECEPTOR, wktCentroid, wktGeometry);
  }

}
