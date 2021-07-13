package nl.aerius.search.tasks.async;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
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
 * Each search task is put in a cache that is wiped out after 5 minutes. When a
 * search task is retrieved, and it is a completed task, it is also removed from
 * the cache under the assumption that he user will not need the result again.
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

  private final CacheMap<String, SearchResult> tasks = new CacheMap<>(TIME_TO_LIVE, INTERVAL, LOG);

  private final CacheMap<String, Disposable> disposables = new CacheMap<>(TIME_TO_LIVE, INTERVAL, LOG);

  public SearchResult retrieveSearchResultsAsync(final String query, final Set<SearchCapability> capabilities) {
    final Map<SearchCapability, SearchTaskService> services = TaskUtils.findTaskServices(taskFactory, capabilities, LOG);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Delegating search query [{}] to {} tasks ({})", query, services.size(), services.keySet()
          .stream().map(v -> v.name())
          .collect(Collectors.joining(",")));
    }

    final SearchResult task = initializeTask();

    final Disposable disposable = Flowable.fromIterable(services.values())
        .parallel()
        .runOn(Schedulers.computation())
        .map(v -> v.retrieveSearchResults(query))
        .doOnNext(v -> task.complete(v))
        .doOnError(e -> LOG.error("Error while performing search task:", e))
//        .doOnCancel(() -> {
//          // Can we do something here?
//        })
        .sequential()
        .doOnComplete(() -> task.complete())
//        .doOnCancel(() -> {
//          // Can we do something useful here?
//        })
        .subscribe();

    disposables.put(task.getUuid(), disposable);

    return task;
  }

  /**
   * Retrieve the search result associated with the given reference, or null if it
   * is not complete TODO Return the future instead?
   */
  public SearchResult retrieveSearchTask(final String uuid) {
    final Optional<SearchResult> task = Optional.ofNullable(tasks.get(uuid));

    // Remove if the task is complete
    task.ifPresent(v -> {
      if (task.get().isComplete()) {
        tasks.remove(uuid);
      }
    });

    return task.orElse(null);
  }

  private SearchResult initializeTask() {
    final String uuid = UUID.randomUUID().toString();

    if (LOG.isTraceEnabled()) {
      LOG.trace("Initializing search task with uuid: {}", uuid);
    }

    final SearchResult task = new SearchResult(uuid, TaskUtils.getResultComparator());
    tasks.put(uuid, task);

    return task;
  }

  public void cancelSearchTask(final String uuid) {
    LOG.info("Cancelling: {}", uuid);
    Optional.ofNullable(disposables.remove(uuid))
        .ifPresent(disposable -> {
          LOG.info("Cancelled: {}", uuid);
          disposable.dispose();
          tasks.remove(uuid);
        });
  }
}
