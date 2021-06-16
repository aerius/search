package nl.aerius.tasks.mock;

import org.springframework.stereotype.Component;

@Component
public class Mock1SecondTask extends MockSearchTask {
  public Mock1SecondTask() {
    super(1000);
  }
}
