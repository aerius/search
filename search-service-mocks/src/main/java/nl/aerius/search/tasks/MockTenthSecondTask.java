package nl.aerius.search.tasks;

import org.springframework.stereotype.Component;

import nl.aerius.search.domain.SearchCapability;

/**
 * A simple mock service that returns a search result after a .1 second delay
 */
@Component
@ImplementsCapability(SearchCapability.MOCK_01)
public class MockTenthSecondTask extends MockSearchTask {
  public MockTenthSecondTask() {
    super(100);
  }
}
