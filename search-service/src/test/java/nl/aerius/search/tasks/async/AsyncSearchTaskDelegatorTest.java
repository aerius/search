package nl.aerius.search.tasks.async;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nl.aerius.search.domain.SearchCapability;

@SpringBootTest
public class AsyncSearchTaskDelegatorTest {
  @Autowired AsyncSearchTaskDelegator delegator;

  @Test
  public void responseDelays() {
    final Set<SearchCapability> caps = Set.of(SearchCapability.MOCK_0, SearchCapability.MOCK_01, SearchCapability.MOCK_05);
    final SearchResult result1 = delegator.retrieveSearchResultsAsync("test", caps);

    assertFalse(result1.isComplete(), "Result should not be complete at this point.");
    assertTrue(result1.getResults() != null, "Results should not be null at this point.");

    try {
      Thread.sleep(50);
    } catch (final InterruptedException e) {}

    assertTrue(result1.getResults().size() == 1, "Should have 1 results at this point.");

    try {
      Thread.sleep(100);
    } catch (final InterruptedException e) {}

    assertTrue(result1.getResults().size() == 2, "Should have 2 results at this point.");

    try {
      Thread.sleep(400);
    } catch (final InterruptedException e) {}

    assertTrue(result1.getResults().size() == 3, "Should have 3 results at this point.");
  }

  @Test
  public void responseCancellation() {
    final Set<SearchCapability> caps = Set.of(SearchCapability.MOCK_01);
    final SearchResult result1 = delegator.retrieveSearchResultsAsync("test", caps);

    assertFalse(result1.isComplete(), "Result should not be complete at this point.");

    try {
      Thread.sleep(50);
    } catch (final InterruptedException e) {}

    delegator.cancelSearchTask(result1.getUuid());

    final SearchResult result2 = delegator.retrieveSearchTask(result1.getUuid());

    assertTrue(result2 == null, "Result should be disposed after cancellation");

    // Sneakishly keep checking results in result1
    try {
      Thread.sleep(60);
    } catch (final InterruptedException e) {}

    assertFalse(result1.isComplete(), "Result should not be complete, because the query should be cancelled - the reactor was not cancelled");
    assertTrue(result1.getResults().isEmpty(),
        "Results should still be empty after the task completes (>100ms) - the individual task was not cancelled");
  }

  @Test
  public void testReceptorNonNull() {
    final Set<SearchCapability> caps = Set.of(SearchCapability.RECEPTOR);
    final SearchResult result1 = delegator.retrieveSearchResultsAsync("123123", caps);

    try {
      Thread.sleep(50);
    } catch (final InterruptedException e) {}

    assertTrue(result1.isComplete(), "Result should be complete at this point.");
    assertEquals(1, result1.getResults().size(), "Result number should be 1");
  }

  @Test
  public void testReceptorNull() {
    final Set<SearchCapability> caps = Set.of(SearchCapability.RECEPTOR);

    final SearchResult res = delegator.retrieveSearchResultsAsync("nothing", caps);

    try {
      Thread.sleep(50);
    } catch (final InterruptedException e) {}

    final SearchResult result = delegator.retrieveSearchTask(res.getUuid());

    assertTrue(result.isComplete(), "Result should be complete at this point.");
    assertEquals(0, result.getResults().size(), "Result number should be 0");
  }
}
