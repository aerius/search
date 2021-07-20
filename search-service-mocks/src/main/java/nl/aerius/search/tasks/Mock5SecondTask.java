package nl.aerius.search.tasks;

import org.springframework.stereotype.Component;

import nl.aerius.search.domain.SearchCapability;

@Component
@ImplementsCapability(SearchCapability.MOCK_5)
public class Mock5SecondTask extends MockSearchTask {
  public Mock5SecondTask() {
    super(5000);
  }
}
