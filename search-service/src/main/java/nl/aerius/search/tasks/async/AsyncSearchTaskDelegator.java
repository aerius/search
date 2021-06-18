package nl.aerius.search.tasks.async;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.collections.CacheMap;
import nl.aerius.search.tasks.SearchTaskService;
import nl.aerius.search.tasks.TaskFactory;
import nl.aerius.search.tasks.TaskUtils;

/**
 * Simple task delegator that does not block.
 * 
 * Delegate a single search task to multiple specialized search services based
 * on the requested capabilities.
 * 
 * NOTE: While this delegator works, it has not been sufficiently stress tested
 * or its approach reviewed
 */
@Component
public class AsyncSearchTaskDelegator {
  private static final Logger LOG = LoggerFactory.getLogger(AsyncSearchTaskDelegator.class);

  @Autowired TaskFactory taskFactory;

  private static final int INTERVAL = 30; // 30 seconds
  private static final int TIME_TO_LIVE = 5 * 60; // 5 minutes

  private final CacheMap<String, SearchTask> tasks = new CacheMap<>(TIME_TO_LIVE, INTERVAL, LOG);

  public String retrieveSearchResults(final String query, final long capabilities) {
    final Map<SearchCapability, SearchTaskService> services = TaskUtils.findTaskServices(LOG, taskFactory, capabilities);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Delegating search query [{}] to {} tasks ({})", query, services.size(), services.keySet()
          .stream().map(v -> v.name())
          .collect(Collectors.joining(",")));
    }

    final String uuid = UUID.randomUUID().toString();
    final SearchTask task = new SearchTask(uuid);
    tasks.put(uuid, task);

    Flowable.fromIterable(services.values())
        .parallel()
        .runOn(Schedulers.computation())
        .map(v -> v.retrieveSearchResults(query))
        .doOnNext(v -> task.complete(v))
        .sequential()
        .doOnError(e -> LOG.error("Error while performing search task:", e))
        .doOnComplete(() -> task.complete())
        .subscribe();

    return uuid;
  }

  public SearchTask retrieveSearchTask(final String uuid) {
    final Optional<SearchTask> task = Optional.ofNullable(tasks.get(uuid));

    // Remove if the task is complete
    task.ifPresent(v -> {
      if (task.get().isComplete()) {
        tasks.remove(uuid);
      }
    });

    return task.orElse(null);
  }
}
