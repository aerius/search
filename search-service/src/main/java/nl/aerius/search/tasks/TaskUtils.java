package nl.aerius.search.tasks;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import nl.aerius.search.domain.SearchCapability;

/**
 * Boiler plate for task-related code
 */
public final class TaskUtils {
  private TaskUtils() {}

  public static boolean hasCapability(final long capabilities, final SearchCapability capability) {
    return (capabilities & 1 << capability.bit()) > 0;
  }

  public static Map<SearchCapability, SearchTaskService> findTaskServices(final Logger log, final TaskFactory taskFactory, final long capabilities) {
    final Map<SearchCapability, SearchTaskService> tasks = new HashMap<>();

    for (final SearchCapability capability : SearchCapability.values()) {
      if (hasCapability(capabilities, capability)) {

        try {
          tasks.put(capability, taskFactory.getTask(capability));
        } catch (final Exception e) {
          // Log and eat
          log.error("No task for known capability: " + capability, e);
        }
      }
    }

    return tasks;
  }
}
