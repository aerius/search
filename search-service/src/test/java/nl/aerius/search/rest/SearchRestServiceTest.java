package nl.aerius.search.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

class SearchRestServiceTest {
  private static final Logger LOG = LoggerFactory.getLogger(SearchRestServiceTest.class);

  @Autowired SearchRestService messagesService;

  @BeforeEach
  void beforeEach() {
    LOG.info("beforeEach");
  }

  @Test
  void testIfAnythingWorks() {
    Assertions.assertTrue(4 != 6, "4 does not equal 6");
  }
}
