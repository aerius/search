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

import nl.aerius.geo.domain.ReceptorPoint;
import nl.aerius.geo.util.ReceptorUtil;
import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.domain.SearchSuggestionBuilder;
import nl.aerius.search.domain.SearchSuggestionType;
import nl.aerius.search.domain.SearchTaskResult;

public class ReceptorUtils {
  // TODO i18n
  private static final String RECEPTOR_FORMAT = "Receptor %s - x:%s y:%s";

  private ReceptorUtils() {
  }

  public static SearchTaskResult tryParse(final ReceptorUtil receptorUtil, final String query) throws NumberFormatException {
    final int id = Integer.parseInt(query);

    final ReceptorPoint rec = receptorUtil.createReceptorPointFromId(id);
    return SearchResultBuilder.of(getSearchSuggestion(rec));
  }

  public static SearchSuggestion getSearchSuggestion(final ReceptorPoint rec) {
    return SearchSuggestionBuilder.create(String.format(RECEPTOR_FORMAT, rec.getId(), (int) rec.getX(), (int) rec.getY()),
        100, SearchSuggestionType.RECEPTOR);
  }

}
