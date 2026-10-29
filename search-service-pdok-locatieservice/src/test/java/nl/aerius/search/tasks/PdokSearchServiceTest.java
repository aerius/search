/*
 * Copyright (c) Contributors to the project
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.domain.SearchSuggestionType;
import nl.aerius.search.domain.SearchTaskResult;

import kong.unirest.core.GetRequest;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;

/**
 * Test class for {@link PdokSearchService}.
 */
@SpringBootTest
class PdokSearchServiceTest {
  @Autowired PdokSearchService delegator;

  private final HttpResponse<JsonNode> response = mock(HttpResponse.class);
  private final GetRequest request = mock(GetRequest.class);
  private final Logger logger = mock(Logger.class);

  private MockedStatic<Unirest> unirest;
  private MockedStatic<LoggerFactory> loggerFactory;
  private ArgumentCaptor<String> logMessageCaptor;

  @BeforeEach
  void beforeEach() {
    loggerFactory = mockStatic(LoggerFactory.class);
    loggerFactory.when(() -> LoggerFactory.getLogger(PdokSearchService.class)).thenReturn(logger);
    logMessageCaptor = ArgumentCaptor.forClass(String.class);

    doReturn(response).when(request).asJson();
    unirest = mockStatic(Unirest.class);
    unirest.when(() -> Unirest.get(any())).thenReturn(request);
    doReturn(request).when(request).requestTimeout(anyInt());
  }

  @AfterEach
  void afterEach() {
    unirest.close();
    loggerFactory.close();
  }

  @Test
  void testWorksAtAll() {
    final String json = """
        {
          "response": {
            "docs": [
              {
                "type": "gemeente",
                "weergavenaam": "Gemeente Utrecht",
                "geometrie_rd": "MULTIPOLYGON(((127157 456956,127739)))",
                "centroide_rd": "POINT(133587.182 455921.594)",
                "id": "gem-df0ca8ab37eccea5217e2a13f74d2833",
                "score": 9.48769
              }
            ]
          }
        }
        """;

    doReturn(true).when(response).isSuccess();
    doReturn(new JsonNode(json)).when(response).getBody();
    final Single<SearchTaskResult> result = delegator.retrieveSearchResults("utrecht");
    final List<SearchSuggestion> suggestions = result.blockingGet().getSuggestions();

    assertEquals(1, suggestions.size(), "Expected 1 result for 'utrecht'");
    final SearchSuggestion firstSuggestion = suggestions.get(0);
    assertEquals("gem-df0ca8ab37eccea5217e2a13f74d2833", firstSuggestion.getId(), "Didn't get correct id");
    assertEquals("Gemeente Utrecht", firstSuggestion.getDescription(), "Didn't get correct weergavenaam");
    assertEquals(SearchSuggestionType.MUNICIPALITY, firstSuggestion.getType(), "Didn't get correct type");
    assertEquals("POINT(133587.182 455921.594)", firstSuggestion.getCentroid(), "Didn't get correct Centroid");
    assertEquals("MULTIPOLYGON(((127157 456956,127739)))", firstSuggestion.getGeometry(), "Didn't get correct Geometry");
    assertEquals(9.48769, firstSuggestion.getScore(), 1E-10, "Didn't get correct score");
  }

  @Test
  void testNullBody() {
    assertFailureResult(true, "PDOK service returned", 3);
  }

  @Test
  void testNoSuccess() {
    assertFailureResult(false, "PDOK service returned", 3);
  }

  @Test
  void testUnirestException() {
    doThrow(new UnirestException("timeout")).when(request).asJson();
    assertFailureResult(true, "exception", 2);
  }

  private <T> void assertFailureResult(final boolean success, final String warningMessagePart, final int warnVarArguments) {
    doReturn(success).when(response).isSuccess();
    final PdokSearchService service = new PdokSearchService();
    final List<SearchSuggestion> suggestions = service.retrieveSearchResults("ab;d").blockingGet().getSuggestions();

    assertEquals(1, suggestions.size(), "Expected 1 result");
    assertEquals("Unable to get results from PDOK service.", suggestions.get(0).getDescription(), "Expected error result text");
    final Logger warnVerifier = verify(logger);
    if (warnVarArguments == 2) {
      warnVerifier.warn(logMessageCaptor.capture(), any(), any());
    } else {
      warnVerifier.warn(logMessageCaptor.capture(), any(Object[].class));
    }
    assertTrue(logMessageCaptor.getValue().contains(warningMessagePart),
        "Not logged expeced warning message but got: " + logMessageCaptor.getValue());
  }
}
