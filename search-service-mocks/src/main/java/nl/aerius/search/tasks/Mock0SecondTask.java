package nl.aerius.search.tasks;

import org.springframework.stereotype.Component;

import nl.aerius.search.domain.SearchCapability;

@Component
@ImplementsCapability(SearchCapability.MOCK0)
public class Mock0SecondTask extends MockSearchTask {
  public Mock0SecondTask() {
    super(0);
  }
}
