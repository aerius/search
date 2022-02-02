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

import java.time.Duration;
import java.util.Set;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchRegion;
import nl.aerius.search.tasks.CapabilityKey;

@SpringBootTest
public class AsyncSearchTaskDelegatorTest {
  @Autowired AsyncSearchTaskDelegator delegator;

  @Test
  public void testResponseDelays() {
    final Set<CapabilityKey> caps = Set.of(CapabilityKey.of(SearchCapability.MOCK_0, SearchRegion.NL),
        CapabilityKey.of(SearchCapability.MOCK_01, SearchRegion.NL),
        CapabilityKey.of(SearchCapability.MOCK_05, SearchRegion.NL));
    final SearchResult result1 = delegator.retrieveSearchResultsAsync("test", caps);

    assertFalse(result1.isComplete(), "Result should not be complete at this point.");
    assertNotNull(result1.getResults(), "Results should not be null at this point.");

    Awaitility.await().atMost(Duration.ofMillis(50));
    assertEquals(1, result1.getResults().size(), "Should have 1 results at this point.");


    Awaitility.await().atMost(Duration.ofMillis(100));
    assertEquals(2, result1.getResults().size(), "Should have 2 results at this point.");


    Awaitility.await().atMost(Duration.ofMillis(400));
    assertEquals(3, result1.getResults().size(), "Should have 3 results at this point.");
  }

  @Test
  public void testResponseCancellation() {
    final Set<CapabilityKey> caps = Set.of(CapabilityKey.of(SearchCapability.MOCK_01, SearchRegion.NL));
    final SearchResult result1 = delegator.retrieveSearchResultsAsync("test", caps);

    assertFalse(result1.isComplete(), "Result should not be complete at this point.");

    

    Awaitility.await().atMost(Duration.ofMillis(50));
    delegator.cancelSearchTask(result1.getUuid());

    final SearchResult result2 = delegator.retrieveSearchTask(result1.getUuid());

    assertNull(result2, "Result should be disposed after cancellation");


    Awaitility.await().atMost(Duration.ofMillis(60));
    assertFalse(result1.isComplete(), "Result should not be complete, because the query should be cancelled - the reactor was not cancelled");
    assertTrue(result1.getResults().isEmpty(),
        "Results should still be empty after the task completes (>100ms) - the individual task was not cancelled");
  }

  @Test
  public void testReceptorNonNull() {
    final Set<CapabilityKey> caps = Set.of(CapabilityKey.of(SearchCapability.RECEPTOR, SearchRegion.NL));
    final SearchResult result1 = delegator.retrieveSearchResultsAsync("123123", caps);


    Awaitility.await().atMost(Duration.ofMillis(50));
    assertTrue(result1.isComplete(), "Result should be complete at this point.");
    assertEquals(1, result1.getResults().size(), "Result number should be 1");
  }

  @Test
  public void testReceptorNull() {
    final Set<CapabilityKey> caps = Set.of(CapabilityKey.of(SearchCapability.RECEPTOR, SearchRegion.NL));

    final SearchResult res = delegator.retrieveSearchResultsAsync("nothing", caps);


    Awaitility.await().atMost(Duration.ofMillis(50));
    final SearchResult result = delegator.retrieveSearchTask(res.getUuid());

    assertTrue(result.isComplete(), "Result should be complete at this point.");
    assertEquals(0, result.getResults().size(), "Result number should be 0");
  }
}
