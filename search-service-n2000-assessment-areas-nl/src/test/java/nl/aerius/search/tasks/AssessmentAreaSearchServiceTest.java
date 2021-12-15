package nl.aerius.search.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration
public class AssessmentAreaSearchServiceTest {
  @Autowired AssessmentAreaSearchService delegator;

  @Test
  public void testWorksAtALl() {
    Assertions.assertTrue(true);
  }
}
