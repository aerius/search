package nl.aerius.search.tasks;

import org.springframework.stereotype.Component;

import nl.aerius.search.domain.SearchCapability;

@Component
@ImplementsCapability(SearchCapability.MOCK_GROUP_0)
public class Mock0SecondGroupedTask extends MockGroupSearchTask {
  public Mock0SecondGroupedTask() {
    super(0);
  }
}
