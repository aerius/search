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
package nl.aerius.search.tasks.receptor;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchTaskResult;
import nl.aerius.search.tasks.ReceptorUtils;
import nl.aerius.search.tasks.SearchTaskService;
import nl.overheid.aerius.shared.domain.geo.HexagonZoomLevel;
import nl.overheid.aerius.shared.domain.geo.ReceptorGridSettings;
import nl.overheid.aerius.shared.geometry.ReceptorUtil;

public abstract class AbstractReceptorSearchService implements SearchTaskService {
  private final ReceptorUtil util;
  private final HexagonZoomLevel minimumZoomLevel;

  protected AbstractReceptorSearchService(final ReceptorGridSettings settings) {
    this.util = new ReceptorUtil(settings);
    this.minimumZoomLevel = settings.getZoomLevel1();
  }

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    return Single.just(query)
        .map(v -> ReceptorUtils.tryParse(v, util, minimumZoomLevel))
        .onErrorReturn(e -> SearchResultBuilder.empty());
  }
}
