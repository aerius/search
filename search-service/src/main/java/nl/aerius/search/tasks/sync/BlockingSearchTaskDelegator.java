package nl.aerius.search.tasks.sync;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.tasks.SearchTaskService;
import nl.aerius.search.tasks.TaskFactory;
import nl.aerius.search.tasks.TaskUtils;

/**
 * Simple task delegator that blocks until all sub-tasks have completed.
 * 
 * Delegate a single search task to multiple specialized search services based
 * on the requested capabilities
 */
@Component
public class BlockingSearchTaskDelegator {
  private static final Logger LOG = LoggerFactory.getLogger(BlockingSearchTaskDelegator.class);

  @Autowired TaskFactory taskFactory;

  public List<SearchSuggestion> retrieveSearchResults(final String query, final Set<SearchCapability> capabilities) {
    final Map<SearchCapability, SearchTaskService> tasks = TaskUtils.findTaskServices(taskFactory, capabilities, LOG);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Delegating search query [{}] to {} tasks ({})", query, tasks.size(), tasks.keySet()
          .stream().map(v -> v.name())
          .collect(Collectors.joining(",")));
    }

    /**
     * Create a Flowable from the given search services, retrieve search results for
     * the given query in parallel, and combine the result into a single list.
     */
    return Flowable.fromIterable(tasks.values())
        .parallel()
        .runOn(Schedulers.computation())
        .map(v -> v.retrieveSearchResults(query))
        .doOnError(e -> LOG.error("Error while performing search task:", e))
        .sequential()
        .flatMap(v -> Flowable.fromIterable(v.getSuggestions()))
        .sorted(TaskUtils.getResultComparator())
        .toList()
        .blockingGet();
  }
}
