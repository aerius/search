package nl.aerius.search.tasks;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchSuggestion;

/**
 * Boiler-plate for task-related code
 */
public final class TaskUtils {
  /**
   * TODO Create another comparator, based on weight or some other value
   */
  private static final Comparator<SearchSuggestion> COMPARATOR = (o1, o2) -> o1.getDescription().compareTo(o2.getDescription());

  private TaskUtils() {}

  public static boolean hasCapability(final long capabilities, final SearchCapability capability) {
    return (capabilities & 1 << capability.bit()) > 0;
  }

  /**
   * Find all defined capabilities in the given capability num
   */
  public static Set<SearchCapability> findCapabilities(final long capabilities) {
    return Stream.of(SearchCapability.values())
        .filter(v -> hasCapability(capabilities, v))
        .collect(Collectors.toSet());
  }

  /**
   * Find all task services that satisfy the given set of capabilities
   */
  public static Map<SearchCapability, SearchTaskService> findTaskServices(final TaskFactory taskFactory, final long capabilities, final Logger log) {
    return findCapabilities(capabilities).stream()
        .filter(v -> {
          if (taskFactory.hasCapability(v)) {
            return true;
          } else {
            log.error("No task for known capability: " + v);
            return false;
          }
        })
        .collect(Collectors.toMap(v -> v, v -> taskFactory.getTask(v)));
  }

  public static Comparator<SearchSuggestion> getResultComparator() {
    return COMPARATOR;
  }
}
