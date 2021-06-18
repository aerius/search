package nl.aerius.search.tasks;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.aerius.search.domain.SearchCapability;

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
  private static final Logger LOG = LoggerFactory.getLogger(BlockingSearchTaskDelegator.class);

  @Autowired private Set<SearchTaskService> scannedTasks;

  private Map<SearchCapability, SearchTaskService> tasks;

  public SearchTaskService getTask(final SearchCapability capability) {
    return Optional.ofNullable(tasks.get(capability))
        .orElseThrow(() -> new RuntimeException("No task for capability: " + capability));
  }

  @PostConstruct
  public void onFactoryConstructed() {
    // Map all scanned task services by each respective capability it fulfills.
    tasks = scannedTasks.stream()
        .filter(v -> hasAnnotationOnClass(v.getClass()))
        .collect(Collectors.toMap(v -> extractCapabilityFromClass(v.getClass()), v -> v));
  }

  private static boolean hasAnnotationOnClass(final Class<? extends SearchTaskService> clazz) {
    if (clazz.isAnnotationPresent(ImplementsCapability.class)) {
      return true;
    }

    LOG.warn("Cannot initialize task {} because its capability is unknown. "
        + "Add an ImplementsCapability annotation.", clazz.getSimpleName());

    return false;
  }

  private static SearchCapability extractCapabilityFromClass(final Class<? extends SearchTaskService> clazz) {
    final ImplementsCapability ann = clazz.getAnnotation(ImplementsCapability.class);
    final SearchCapability value = ann.value();

    LOG.info("Initializing task {} for capability {}", clazz.getSimpleName(), value);

    return value;
  }
}
