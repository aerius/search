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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchTaskResult;

@SpringBootTest
class PdokSearchServiceTest {
  @Autowired PdokSearchService delegator;

  @Test
  void testWorksAtAll() {
    final Single<SearchTaskResult> result = delegator.retrieveSearchResults("utrecht");

    final SearchTaskResult suggestions = result.blockingGet();

    assertEquals(10, suggestions.getSuggestions().size(), "Expected 10 results for 'utrecht'");
  }
}
