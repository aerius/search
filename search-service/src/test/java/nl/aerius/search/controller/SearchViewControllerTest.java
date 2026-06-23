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
package nl.aerius.search.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.domain.SearchSuggestionBuilder;
import nl.aerius.search.tasks.CapabilityKey;
import nl.aerius.search.tasks.sync.BlockingSearchTaskDelegator;

class SearchViewControllerTest {

  @Test
  void shouldServeSynchronousForm() {
    assertEquals("synchronous-form", new SearchViewController().searchForm(mock(Model.class)), "Root should serve the synchronous form");
  }

  @Test
  void shouldServeAsynchronousForm() {
    assertEquals("asynchronous-form", new SearchViewController().searchFormAsync(mock(Model.class)), "/async should serve the asynchronous form");
  }

  @Test
  void shouldPopulateModelAndServeResults() {
    final SearchViewController controller = new SearchViewController();
    final BlockingSearchTaskDelegator delegator = mock(BlockingSearchTaskDelegator.class);
    controller.delegator = delegator;

    final List<SearchSuggestion> results = List.of(SearchSuggestionBuilder.create("amsterdam", 90D));
    when(delegator.retrieveSearchResults(eq("ams"), any())).thenReturn(results);

    final Model model = mock(Model.class);
    final String view = controller.search("ams", List.of("RECEPTOR"), "NL", model);

    assertEquals("synchronous-results", view, "Search should serve the results view");
    verify(delegator).retrieveSearchResults(eq("ams"), any());
    verify(model).addAttribute(eq("query"), eq("ams"));
    verify(model).addAttribute(eq("region"), eq("NL"));
    verify(model).addAttribute(eq("results"), eq(results));
  }
}
