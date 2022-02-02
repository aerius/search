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

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchRegion;
import nl.aerius.search.domain.SearchSuggestion;

/**
 * Boiler-plate for task-related code
 */
public final class TaskUtils {
  private static final Logger LOG = LoggerFactory.getLogger(TaskUtils.class);

  private static final Comparator<SearchSuggestion> COMPARATOR = Comparator
      .<SearchSuggestion>comparingDouble(SearchSuggestion::getScore)
      .reversed()
      .thenComparing(SearchSuggestion::getDescription);

  // (o1, o2) -> Double.compare(o1.getScore(), o2.getScore());

  private TaskUtils() {}

  /**
   * Find all task services that satisfy the given set of capabilities
   */
  public static Map<CapabilityKey, SearchTaskService> findTaskServices(final TaskFactory taskFactory, final Collection<CapabilityKey> capabilities,
      final Logger log) {
    return capabilities.stream()
        .filter(v -> {
          if (taskFactory.hasCapability(v)) {
            return true;
          } else {
            log.error("No task for known capability: " + v);
            return false;
          }
        })
        .collect(Collectors.toMap(v -> v, v -> taskFactory.getTask(v)))
        .entrySet().stream()
        .flatMap(e -> e.getValue().stream()
            .map(v -> new AbstractMap.SimpleImmutableEntry<CapabilityKey, SearchTaskService>(e.getKey(), v)))
        .collect(Collectors.toMap(SimpleImmutableEntry::getKey, SimpleImmutableEntry::getValue));

  }

  public static Comparator<SearchSuggestion> getResultComparator() {
    return COMPARATOR;
  }

  public static Set<CapabilityKey> parseCapabilities(final Collection<String> capabilities, final String region) {
    final SearchRegion reg = SearchRegion.safeValueOf(region);
    if (reg == null) {
      LOG.warn("Requested region that does not exist: {}", region.replaceAll("[\n\r\t]", "_"));
      return Set.of();
    }

    return capabilities.stream()
        .map(v -> {
          final SearchCapability cap = SearchCapability.safeValueOf(v);
          if (cap == null) {
            LOG.warn("Requested capability that does not exist: {}", cap);
          }
          return CapabilityKey.of(cap, reg);
        })
        .filter(v -> v != null)
        .collect(Collectors.toSet());
  }
}
