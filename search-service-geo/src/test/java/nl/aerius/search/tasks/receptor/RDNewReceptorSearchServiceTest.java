package nl.aerius.search.tasks.receptor;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchTaskResult;

class RDNewReceptorSearchServiceTest {
  private RDNewReceptorSearchService service;

  @BeforeEach
  void beforeEach() {
    service = new RDNewReceptorSearchService();
  }

  @Test
  void testReceptorNonNull() {
    service = new RDNewReceptorSearchService();
    final Single<SearchTaskResult> single = service.retrieveSearchResults("123123");
    final SearchTaskResult result = single.blockingGet();

    assertEquals(1, result.getSuggestions().size(), "Result number should be 1");
  }

  @Test
  void testReceptorNull() {
    service = new RDNewReceptorSearchService();
    final Single<SearchTaskResult> single = service.retrieveSearchResults("nothing");
    final SearchTaskResult result = single.blockingGet();

    assertEquals(0, result.getSuggestions().size(), "Result number should be 0");
  }
}
