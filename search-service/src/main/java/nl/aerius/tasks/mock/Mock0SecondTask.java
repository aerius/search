package nl.aerius.tasks.mock;

import org.springframework.stereotype.Component;

import nl.aerius.domain.SearchCapability;

@Component
@ImplementsCapability(SearchCapability.MOCK0)
public class Mock0SecondTask extends MockSearchTask {
  public Mock0SecondTask() {
    super(0);
  }
}
