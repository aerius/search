package nl.aerius.search.tasks;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchSuggestion;

/**
 * Boiler-plate for task-related code
 */
public final class TaskUtils {
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

  /**
   * Create a Flowable from the given search services, retrieve search results for the given query in parallel,
   * and combine the result into a single list. Return a Future for this result.
   */
  public static Future<List<SearchSuggestion>> futureFromTasks(final String query, final Iterable<SearchTaskService> values,
      final Logger logger) {
    return Flowable.fromIterable(values)
        .parallel()
        .runOn(Schedulers.computation())
        .map(v -> v.retrieveSearchResults(query))
        .doOnError(e -> logger.error("Error while performing search task:", e))
        .sequential()
        .flatMap(v -> Flowable.fromIterable(v.getSuggestions()))
        // TODO Don't sort on description but on weight or something (see SearchSuggestion)
        .sorted((o1, o2) -> o1.getDescription().compareTo(o2.getDescription()))
        .toList()
        .toFuture();
  }
}
