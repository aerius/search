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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchTaskResult;

@Component
@ImplementsCapability(SearchCapability.MOCK_RUNTIME_EXCEPTION)
public class MockRuntimeExceptionTask implements SearchTaskService {
  private static final Logger LOG = LoggerFactory.getLogger(MockRuntimeExceptionTask.class);

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    LOG.debug("Mocking runtime exception for query [{}]", query);
    throw new NullPointerException("Some mocked nullpointer");
  }
}
