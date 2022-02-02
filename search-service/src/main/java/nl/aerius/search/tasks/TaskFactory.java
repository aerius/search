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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchRegion;

/**
 * Scan all components implementing {@link SearchTaskService}, then index them
 * based on the {@link SearchCapability} they fulfill, as described by the
 * {@link ImplementsCapability} annotation.
 *
 * TODO If multiple components implement the same {@link SearchCapability}, then
 * the indexing mechanism will crash. Add a priority, filter out duplicates?
 *
 * TODO Perhaps index on flag bit # rather than enum? that way a capability need
 * not necessarily be defined yet and tasks can simply advertise they implement
 * a certain flag bit.
 */
@Component
public class TaskFactory {
  private static final Logger LOG = LoggerFactory.getLogger(TaskFactory.class);

  @Autowired private Set<SearchTaskService> scannedTasks;

  @Resource
  private Map<CapabilityKey, List<SearchTaskService>> tasks;

  public List<SearchTaskService> getTask(final CapabilityKey capability) {
    return Optional.ofNullable(tasks.get(capability))
        .orElseThrow(() -> new RuntimeException("No task for capability: " + capability));
  }

  public boolean hasCapability(final CapabilityKey capability) {
    return tasks.containsKey(capability);
  }

  @PostConstruct
  public void onFactoryConstructed() {
    // Map all scanned task services by each respective capability it fulfills.
    // TODO Make this map support multiple services for the same CapabilityKey
    final Map<List<CapabilityKey>, SearchTaskService> unflattened = scannedTasks.stream()
        .filter(v -> hasAnnotationOnClass(v.getClass()))
        .collect(Collectors.toMap(v -> extractCapabilityFromClass(v.getClass()), v -> v));

    tasks = unflattened.entrySet().stream()
        .flatMap(e -> e.getKey().stream()
            .map(k -> new AbstractMap.SimpleImmutableEntry<CapabilityKey, SearchTaskService>(k, e.getValue())))
        .collect(Collectors.toMap(v -> v.getKey(), v -> Arrays.asList(v.getValue())));
  }

  private static boolean hasAnnotationOnClass(final Class<? extends SearchTaskService> clazz) {
    if (clazz.isAnnotationPresent(ImplementsCapability.class)) {
      return true;
    }

    LOG.warn("Cannot initialize task {} because its capability is unknown. "
        + "Add an ImplementsCapability annotation.", clazz.getSimpleName());

    return false;
  }

  private static List<CapabilityKey> extractCapabilityFromClass(final Class<? extends SearchTaskService> clazz) {
    final ImplementsCapability ann = clazz.getAnnotation(ImplementsCapability.class);
    final SearchCapability value = ann.value();
    final SearchRegion[] regions = ann.region().length == 0 ? SearchRegion.values() : ann.region();

    LOG.info("Initializing task {} for capability {}", clazz.getSimpleName(), value);

    return Stream.of(regions)
        .map(region -> CapabilityKey.of(value, region))
        .collect(Collectors.toList());
  }
}
