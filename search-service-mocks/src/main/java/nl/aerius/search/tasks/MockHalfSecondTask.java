package nl.aerius.search.tasks;

import org.springframework.stereotype.Component;

import nl.aerius.search.domain.SearchCapability;

/**
 * A simple mock service that returns a search result after a .5 second delay
 */
@Component
@ImplementsCapability(SearchCapability.MOCK_05)
public class MockHalfSecondTask extends MockSearchTask {
  public MockHalfSecondTask() {
    super(500);
  }
}
