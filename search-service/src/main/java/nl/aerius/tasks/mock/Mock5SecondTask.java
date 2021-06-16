package nl.aerius.tasks.mock;

import org.springframework.stereotype.Component;

@Component
public class Mock5SecondTask extends MockSearchTask {
  public Mock5SecondTask() {
    super(5000);
  }
}
