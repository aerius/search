package nl.aerius.search.tasks;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchSuggestion;

/**
 * Boiler-plate for task-related code
 */
public final class TaskUtils {
  private static final Logger LOG = LoggerFactory.getLogger(TaskUtils.class);
  /**
   * TODO Create another comparator, based on weight or some other value
   */
  private static final Comparator<SearchSuggestion> COMPARATOR = (o1, o2) -> o1.getDescription().compareTo(o2.getDescription());

  private TaskUtils() {}

  /**
   * Find all task services that satisfy the given set of capabilities
   */
  public static Map<SearchCapability, SearchTaskService> findTaskServices(final TaskFactory taskFactory, final Set<SearchCapability> capabilities,
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
        .collect(Collectors.toMap(v -> v, v -> taskFactory.getTask(v)));
  }

  public static Comparator<SearchSuggestion> getResultComparator() {
    return COMPARATOR;
  }

  public static Set<SearchCapability> parseCapabilities(final List<String> capabilities) {
    return capabilities.stream()
        .map(v -> {
          final SearchCapability cap = SearchCapability.safeValueOf(v);
          if (cap == null) {
            LOG.warn("Requested capability that does not exist: {}", cap);
          }
          return cap;
        })
        .filter(v -> v != null)
        .collect(Collectors.toSet());
  }
}
