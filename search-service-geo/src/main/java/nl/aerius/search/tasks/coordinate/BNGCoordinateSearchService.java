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
import nl.aerius.search.tasks.BNGConstants;
import nl.aerius.search.tasks.ImplementsCapability;
import nl.aerius.search.tasks.SearchTaskService;

@Component
@ImplementsCapability(value = SearchCapability.COORDINATE, region = SearchRegion.UK)
public class BNGCoordinateSearchService extends AbstractCoordinateSearchService implements SearchTaskService {
  public BNGCoordinateSearchService() {
    super(BNGConstants.create());
  }
}