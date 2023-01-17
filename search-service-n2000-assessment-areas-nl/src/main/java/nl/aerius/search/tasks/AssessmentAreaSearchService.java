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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchRegion;
import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.domain.SearchSuggestionBuilder;
import nl.aerius.search.domain.SearchSuggestionType;
import nl.aerius.search.domain.SearchTaskResult;

@Component
@ImplementsCapability(value = SearchCapability.ASSESSMENT_AREA, region = SearchRegion.NL)
public class AssessmentAreaSearchService implements SearchTaskService {
  private static final Logger LOG = LoggerFactory.getLogger(AssessmentAreaSearchService.class);

  private static final long MAX_RESULTS = 20;

  private final Map<String, Nature2000Area> areas;
  private final Natura2000WfsInterpreter interpreter;

  @Autowired
  public AssessmentAreaSearchService(final Natura2000WfsInterpreter interpreter) {
    this.interpreter = interpreter;
    this.areas = new HashMap<>();
  }

  @PostConstruct
  public void init() {
    areas.clear();
    try {
      areas.putAll(interpreter.retrieveAreas());
      LOG.info("AssessmentAreaSearchService initialized");
    } catch (final Exception e) {
      LOG.error("Exception while initializing AssessmentAreaSearchService", e);
    }
  }

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    final SearchTaskResult result = new SearchTaskResult();
    if (areas.isEmpty()) {
      // Warn if areas is null - it is expected to happen shortly after startup (see
      // init()) but not during normal operation
      LOG.warn(
          "Queries made while assessment area search service was not yet initialized,"
              + " this is an issue if it happens well after startup has succeeded.");
      result.setSuggestions(new ArrayList<>());
      return Single.just(result);
    }

    final String normalizedQuery = interpreter.normalize(query);

    final String[] normalizedQueryParts = normalizedQuery.split(" ");

    final List<SearchSuggestion> sugs = areas.entrySet().stream()
        .filter(area -> Stream.of(normalizedQueryParts)
            .anyMatch(part -> area.getKey().contains(part)))
        .map(Entry::getValue)
        .map(v -> areaToSuggestion(normalizedQuery, v))
        .sorted(Comparator.comparingDouble(SearchSuggestion::getScore).reversed())
        .limit(MAX_RESULTS)
        .collect(Collectors.toList());

    result.setSuggestions(sugs);
    return Single.just(result);
  }

  public SearchSuggestion areaToSuggestion(final String normalizedQuery, final Nature2000Area area) {
    // Start with a score of 20, increment with 10 for each character in the query,
    // then reduce by size of match
    // It's not pretty but it'll do fine in most cases
    final int score = Math.max(20, Math.min(100, normalizedQuery.length() * 10) - Math.min(area.getNormalizedName().length(), 20));

    return SearchSuggestionBuilder.create(area.getName(), score, SearchSuggestionType.ASSESSMENT_AREA, area.getWktCentroid(), area.getWktGeometry());
  }
}
