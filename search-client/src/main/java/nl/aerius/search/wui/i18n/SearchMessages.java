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
package nl.aerius.search.wui.i18n;

import com.google.gwt.i18n.client.Messages;

public interface SearchMessages extends Messages {
  String searchButtonAria();

  String searchInputFieldAria();

  String searchInputFieldPlaceholder();

  String searchTextLoading();

  String searchTextNoResults();

  String searchTextNoResultsExplainer();

  String searchSuggestionTypeName(@Select String type);
}
