package nl.aerius.search.tasks;

import org.springframework.stereotype.Component;

import nl.aerius.search.domain.SearchCapability;

@Component
@ImplementsCapability(SearchCapability.MOCK1)
public class Mock1SecondTask extends MockSearchTask {
  public Mock1SecondTask() {
    super(1000);
  }
}
