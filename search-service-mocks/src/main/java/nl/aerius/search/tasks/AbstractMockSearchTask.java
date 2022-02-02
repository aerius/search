/*
 * Copyright the State of the Netherlands
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package nl.aerius.search.tasks;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchSuggestionBuilder;
import nl.aerius.search.domain.SearchTaskResult;

public abstract class AbstractMockSearchTask implements SearchTaskService {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractMockSearchTask.class);

  public static final int FIVE_SECOND = 5_000;
  public static final int SECOND = 1_000;
  public static final int HALF_SECOND = 500;
  public static final int TENTH_SECOND = 100;

  private static final String TEXT = "Mock for query [%s] produced after %sms";

  private final long delay;

  protected AbstractMockSearchTask(final long delay) {
    this.delay = delay;
  }

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    LOG.debug("Retrieving mock search result for query [{}] at delay of {}ms", query, delay);

    return Single.just(SearchResultBuilder
        .of(SearchSuggestionBuilder.create(String.format(TEXT, query, delay), 1)))
        .delay(delay, TimeUnit.MILLISECONDS)
        .doOnDispose(() -> LOG.info("Cancelled mock [{}] with delay {}", query, delay))
        .doAfterTerminate(() -> LOG.info("Terminated mock [{}] with delay {}", query, delay))
        .doOnError(e -> {
          // Handle error
          LOG.error("Thrown mock [{}] with delay {}", query, delay, e);
          throw e;
        });
  }
}
