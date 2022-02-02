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
import nl.overheid.aerius.geo.shared.RDNew;
import nl.overheid.aerius.shared.domain.geo.ReceptorGridSettings;

public final class RDNewConstants {
  public static final int MIN_ZL_SURFACE_AREA = 10_000;
  public static final int HEX_HOR = 1529;
  public static final BBox BOUNDS = new BBox(3604, 296800, 287959, 629300);

  private RDNewConstants() {}

  public static ReceptorGridSettings create() {
    return ReceptorUtils.createReceptorUtil(RDNew.SRID, MIN_ZL_SURFACE_AREA, HEX_HOR, BOUNDS);
  }
}
