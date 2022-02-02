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
package nl.aerius.search.tasks.async;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchRegion;
import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.tasks.CapabilityKey;
import nl.aerius.search.tasks.Mock0SecondTask;
import nl.aerius.search.tasks.MockHalfSecondTask;
import nl.aerius.search.tasks.MockTenthSecondTask;
import nl.aerius.search.tasks.SearchTaskService;
import nl.aerius.search.tasks.TaskFactory;
import nl.aerius.search.tasks.sync.BlockingSearchTaskDelegator;

public class BlockingSearchTaskDelegatorTest {
  BlockingSearchTaskDelegator delegator;

  @BeforeEach
  void beforeEach() {
    final SearchTaskService mock0Task = new Mock0SecondTask();
    final SearchTaskService mock01Task = new MockTenthSecondTask();
    final SearchTaskService mock05Task = new MockHalfSecondTask();

    final Set<SearchTaskService> tasks = Set.of(mock0Task, mock01Task, mock05Task);

    final TaskFactory factory = new TaskFactory(tasks);
    delegator = new BlockingSearchTaskDelegator(factory);
  }

  @Test
  public void testResponseDelays() {
    beforeEach();

    final Set<CapabilityKey> caps = Set.of(CapabilityKey.of(SearchCapability.MOCK_0, SearchRegion.NL),
        CapabilityKey.of(SearchCapability.MOCK_01, SearchRegion.NL),
        CapabilityKey.of(SearchCapability.MOCK_05, SearchRegion.NL));
    final List<SearchSuggestion> result = delegator.retrieveSearchResults("test", caps);
    assertEquals(3, result.size(), "Should have 3 results at this point.");
  }
}
