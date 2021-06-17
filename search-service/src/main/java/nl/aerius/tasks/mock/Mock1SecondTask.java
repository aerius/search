package nl.aerius.tasks.mock;

import org.springframework.stereotype.Component;

import nl.aerius.domain.SearchCapability;

@Component
@ImplementsCapability(SearchCapability.MOCK1)
public class Mock1SecondTask extends MockSearchTask {
  public Mock1SecondTask() {
    super(1000);
  }
}
