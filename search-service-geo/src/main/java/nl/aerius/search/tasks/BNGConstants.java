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

import nl.overheid.aerius.geo.shared.BBox;
import nl.overheid.aerius.geo.shared.BNGrid;
import nl.overheid.aerius.shared.domain.geo.ReceptorGridSettings;

public final class BNGConstants {
  // NOTE Both of the below values are not yet guaranteed to be as constant as the
  // RDNew counterparts
  public static final int MIN_ZL_SURFACE_AREA = 250_000;
  public static final int HEX_HOR = 721;
  public static final BBox BOUNDS = new BBox(1393.0196, 671196.3657, 13494.9764, 1234954.16);

  private BNGConstants() {}

  public static ReceptorGridSettings create() {
    return ReceptorUtils.createReceptorUtil(BNGrid.SRID, BNGConstants.MIN_ZL_SURFACE_AREA, BNGConstants.HEX_HOR, BNGConstants.BOUNDS);
  }
}
