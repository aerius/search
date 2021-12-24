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
package nl.aerius.search.tasks.coordinate;

import org.springframework.stereotype.Component;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchRegion;
import nl.aerius.search.tasks.ImplementsCapability;
import nl.aerius.search.tasks.RDNewConstants;
import nl.aerius.search.tasks.ReceptorUtils;
import nl.aerius.search.tasks.SearchTaskService;
import nl.overheid.aerius.geo.shared.RDNew;
import nl.overheid.aerius.shared.domain.geo.HexagonZoomLevel;

@Component
@ImplementsCapability(value = SearchCapability.COORDINATE, region = SearchRegion.NL)
public class RDNewCoordinateSearchService extends AbstractCoordinateSearchService implements SearchTaskService {
  public RDNewCoordinateSearchService() {
    super(ReceptorUtils.createReceptorUtil(RDNew.SRID, RDNewConstants.MIN_ZL_SURFACE_AREA, RDNewConstants.HEX_HOR),
        new HexagonZoomLevel(1, RDNewConstants.MIN_ZL_SURFACE_AREA));
  }
}
