package nl.aerius.tasks;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.aerius.domain.SearchCapability;
import nl.aerius.tasks.mock.ImplementsCapability;

/**
 * Scan all components implementing {@link SearchTaskService}, then index them
 * based on the {@link SearchCapability} they fulfill.
 * 
 * TODO If multiple components implement the same {@link SearchCapability}, then
 * the indexing mechanism will crash. Add a priority, filter duplicates?
 */
@Component
public class TaskFactory {
  private static final Logger LOG = LoggerFactory.getLogger(SearchTaskDelegatorImpl.class);

  @Autowired private Set<SearchTaskService> scannedTasks;

  private Map<SearchCapability, SearchTaskService> tasks;

  public SearchTaskService getTask(final SearchCapability capability) {
    return Optional.ofNullable(tasks.get(capability))
        .orElseThrow(() -> new RuntimeException("No task for capability: " + capability));
  }

  @PostConstruct
  public void onFactoryConstructed() {
    tasks = scannedTasks.stream()
        .filter(v -> {
          if (v.getClass().isAnnotationPresent(ImplementsCapability.class)) {
            return true;
          } else {
            LOG.warn("Cannot initialize task {} because its capability is unknown. Add an ImplementsCapability annotation.",
                v.getClass().getSimpleName());
            return false;
          }
        })
        .collect(Collectors.toMap(v -> {
          final var clazz = v.getClass();
          final ImplementsCapability ann = clazz.getAnnotation(ImplementsCapability.class);
          final SearchCapability value = ann.value();

          LOG.info("Initializing task {} for capability {}", clazz.getSimpleName(), value);

          return value;
        }, v -> v));
  }
}
