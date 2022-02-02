/*
 * Copyright the State of the Netherlands
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package nl.aerius.search.tasks.async;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import nl.aerius.search.domain.collections.CacheMap;
import nl.aerius.search.tasks.CapabilityKey;
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

  private static final int INTERVAL = 30; // 30 seconds
  private static final int TIME_TO_LIVE = 5 * 60; // 5 minutes

  @Autowired TaskFactory taskFactory;

  @Resource private final CacheMap<String, SearchResult> tasks = new CacheMap<>(TIME_TO_LIVE, INTERVAL, LOG);

  @Resource private final CacheMap<String, Disposable> disposables = new CacheMap<>(TIME_TO_LIVE, INTERVAL, LOG);

  public SearchResult retrieveSearchResultsAsync(final String query, final Set<CapabilityKey> capabilities) {
    final Map<CapabilityKey, SearchTaskService> services = TaskUtils.findTaskServices(taskFactory, capabilities, LOG);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Delegating search query [{}] to {} tasks ({})", query, services.size(), services.keySet()
          .stream().map(v -> v.toString())
          .collect(Collectors.joining(",")));
    }

    final SearchResult task = initializeTask();

    final Disposable disposable = Flowable.fromIterable(services.values())
        .parallel()
        .runOn(Schedulers.io())
        .map(v -> v.retrieveSearchResults(query))
        .doOnError(e -> {
          LOG.error("Error while executing search task:", e);
        })
        .flatMap(v -> v.toFlowable())
        .doAfterNext(r -> task.complete(r))
        .sequential()
        .doOnComplete(() -> task.complete())
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
    if (LOG.isTraceEnabled()) {
      LOG.trace("Cancelling search task: {}", uuid);
    }

    Optional.ofNullable(disposables.remove(uuid))
        .ifPresent(disposable -> {
          disposable.dispose();
          tasks.remove(uuid);
        });
  }
}
