package nl.aerius.search.tasks;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchSuggestionBuilder;
import nl.aerius.search.domain.SearchSuggestionType;
import nl.aerius.search.domain.SearchTaskResult;

public abstract class MockGroupSearchTask implements SearchTaskService {
  private static final Logger LOG = LoggerFactory.getLogger(MockGroupSearchTask.class);

  private static final String FOO = "foo";
  private static final String BAR = "bar";

  private static final String TEXT_FOO = "Mock for query [%s] (" + FOO + ") produced after %sms";
  private static final String TEXT_BAR = "Mock for query [%s] (" + BAR + ") produced after %sms";

  private final long delay;

  public MockGroupSearchTask(final long delay) {
    this.delay = delay;
  }

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    LOG.debug("Retrieving mock search result for query [{}] at delay of {}ms", query, delay);

    return Single.just(SearchResultBuilder
        .of(SearchSuggestionBuilder.create(String.format(TEXT_FOO, query, delay), 0.1, SearchSuggestionType.ADDRESS),
            SearchSuggestionBuilder.create(String.format(TEXT_BAR, query, delay), 0.1, SearchSuggestionType.MUNICIPALITY)))
        .delay(delay, TimeUnit.MILLISECONDS)
        .doOnDispose(() -> {
          // Handle cancellation
          LOG.info("Cancelled mock [{}] with delay {}", query, delay);
        })
        .doAfterTerminate(() -> {
          // Handle termination cleanup
          LOG.info("Terminated mock [{}] with delay {}", query, delay);
        })
        .doOnError(e -> {
          // Handle error
          LOG.error("Thrown mock [{}] with delay {}\", query, delay", e);
          throw e;
        });
  }
}
