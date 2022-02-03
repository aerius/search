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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchRegion;
import nl.aerius.search.tasks.CapabilityKey;
import nl.aerius.search.tasks.Mock0SecondTask;
import nl.aerius.search.tasks.MockHalfSecondTask;
import nl.aerius.search.tasks.MockTenthSecondTask;
import nl.aerius.search.tasks.SearchTaskService;
import nl.aerius.search.tasks.TaskFactory;

class AsyncSearchTaskDelegatorTest {
  AsyncSearchTaskDelegator delegator;

  @BeforeEach
  void beforeEach() {
    final SearchTaskService mock0Task = new Mock0SecondTask();
    final SearchTaskService mock01Task = new MockTenthSecondTask();
    final SearchTaskService mock05Task = new MockHalfSecondTask();

    final Set<SearchTaskService> tasks = Set.of(mock0Task, mock01Task, mock05Task);

    final TaskFactory factory = new TaskFactory(tasks);
    factory.onFactoryConstructed();
    delegator = new AsyncSearchTaskDelegator(factory);
  }

  @Test
  void testResponseDelays() throws InterruptedException {
    final Set<CapabilityKey> caps = Set.of(CapabilityKey.of(SearchCapability.MOCK_0, SearchRegion.NL),
        CapabilityKey.of(SearchCapability.MOCK_01, SearchRegion.NL),
        CapabilityKey.of(SearchCapability.MOCK_05, SearchRegion.NL));
    final SearchResult result1 = delegator.retrieveSearchResultsAsync("test", caps);
    final String uuid = result1.getUuid();

    final SearchResult result2 = delegator.retrieveSearchTask(uuid);

    assertFalse(result2.isComplete(), "Result should not be complete at this point.");
    assertNotNull(result2.getResults(), "Results should not be null at this point.");

    TimeUnit.MILLISECONDS.sleep(50);
    final SearchResult result3 = delegator.retrieveSearchTask(uuid);
    assertEquals(1, result3.getResults().size(), "Should have 1 results at this point.");

    TimeUnit.MILLISECONDS.sleep(100);
    final SearchResult result4 = delegator.retrieveSearchTask(uuid);
    assertEquals(2, result4.getResults().size(), "Should have 2 results at this point.");

    TimeUnit.MILLISECONDS.sleep(400);
    final SearchResult result5 = delegator.retrieveSearchTask(uuid);
    assertEquals(3, result5.getResults().size(), "Should have 3 results at this point.");
  }

  @Test
  void testResponseCancellation() throws InterruptedException {
    final Set<CapabilityKey> caps = Set.of(CapabilityKey.of(SearchCapability.MOCK_01, SearchRegion.NL));
    final SearchResult result1 = delegator.retrieveSearchResultsAsync("test", caps);

    assertFalse(result1.isComplete(), "Result should not be complete at this point.");

    TimeUnit.MILLISECONDS.sleep(50);
    delegator.cancelSearchTask(result1.getUuid());

    final SearchResult result2 = delegator.retrieveSearchTask(result1.getUuid());

    assertNull(result2, "Result should be disposed after cancellation");

    TimeUnit.MILLISECONDS.sleep(60);
    assertFalse(result1.isComplete(), "Result should not be complete, because the query should be cancelled - the reactor was not cancelled");
    assertTrue(result1.getResults().isEmpty(),
        "Results should still be empty after the task completes (>100ms) - the individual task was not cancelled");
  }
}
