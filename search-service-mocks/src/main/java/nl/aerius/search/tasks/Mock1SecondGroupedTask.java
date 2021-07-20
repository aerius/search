package nl.aerius.search.tasks;

import org.springframework.stereotype.Component;

import nl.aerius.search.domain.SearchCapability;

/**
 * A simple mock service that returns a search result
 * after a 1 second delay
 */
@Component
@ImplementsCapability(SearchCapability.MOCK_GROUP_1)
public class Mock1SecondGroupedTask extends MockGroupSearchTask {
  public Mock1SecondGroupedTask() {
    super(1000);
  }
}
