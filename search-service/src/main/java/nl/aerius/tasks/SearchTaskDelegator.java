package nl.aerius.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import nl.aerius.domain.SearchCapability;
import nl.aerius.domain.SearchSuggestion;

/**
 * Delegate a single search task to multiple specialized search services based
 * on the requested capabilities
 */
@Component
public class SearchTaskDelegator {
  private static final Logger LOG = LoggerFactory.getLogger(SearchTaskDelegator.class);

  @Autowired TaskFactory taskFactory;

  public List<SearchSuggestion> retrieveSearchResults(final String query, final long capabilities) {
    final Map<SearchCapability, SearchTaskService> tasks = findTasks(capabilities);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Delegating search query [{}] to {} tasks ({})", query, tasks.size(), tasks.keySet()
          .stream().map(v -> v.name())
          .collect(Collectors.joining(",")));
    }

    return Flowable.fromIterable(tasks.values())
        .parallel()
        .runOn(Schedulers.computation())
        .map(v -> v.retrieveSearchResults(query))
        .sequential()
        .collectInto(new ArrayList<SearchSuggestion>(), ArrayList::addAll)
        .blockingGet();
  }

  private Map<SearchCapability, SearchTaskService> findTasks(final long capabilities) {
    final Map<SearchCapability, SearchTaskService> tasks = new HashMap<>();

    for (final SearchCapability capability : SearchCapability.values()) {
      if (hasCapability(capabilities, capability)) {

        try {
          tasks.put(capability, taskFactory.getTask(capability));
        } catch (final Exception e) {
          // Log and eat
          LOG.error("No task for known capability: " + capability, e);
        }
      }
    }

    return tasks;
  }

  private static boolean hasCapability(final long capabilities, final SearchCapability capability) {
    return (capabilities & 1 << capability.getBit()) > 0;
  }
}
