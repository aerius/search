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

import nl.overheid.aerius.geo.shared.BBox;
import nl.overheid.aerius.shared.domain.geo.HexagonZoomLevel;
import nl.overheid.aerius.shared.domain.geo.ReceptorGridSettings;
import nl.overheid.aerius.shared.geo.EPSG;

public final class BNGConstants {
  private static final EPSG EPSG = nl.overheid.aerius.shared.geo.EPSG.BNG;

  private static final int HEX_HOR = 1785;

  private static final double MIN_X = -4_000.0;
  private static final double MAX_X = 660_000.0;
  private static final double MIN_Y = 4_000.0;
  private static final double MAX_Y = 1_222_000.0;

  private static final HexagonZoomLevel ZOOM_LEVEL_1 = new HexagonZoomLevel(1, 40_000);

  private BNGConstants() {}

  public static ReceptorGridSettings getReceptorUtil() {
    final ArrayList<HexagonZoomLevel> hexagonZoomLevels = new ArrayList<>();
    hexagonZoomLevels.add(ZOOM_LEVEL_1);
    final BBox boundingBox = new BBox(MIN_X, MIN_Y, MAX_X, MAX_Y);
    return new ReceptorGridSettings(boundingBox, EPSG, HEX_HOR, hexagonZoomLevels);
  }
}
