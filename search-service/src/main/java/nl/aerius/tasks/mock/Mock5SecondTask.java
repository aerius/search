package nl.aerius.tasks.mock;

import org.springframework.stereotype.Component;

import nl.aerius.domain.SearchCapability;

@Component
@ImplementsCapability(SearchCapability.MOCK5)
public class Mock5SecondTask extends MockSearchTask {
  public Mock5SecondTask() {
    super(5000);
  }
}
