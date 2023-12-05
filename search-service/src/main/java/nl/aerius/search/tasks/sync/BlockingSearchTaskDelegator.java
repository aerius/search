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
package nl.aerius.search.tasks.sync;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.domain.SearchSuggestionBuilder;
import nl.aerius.search.tasks.CapabilityKey;
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

  private final TaskFactory taskFactory;

  @Autowired
  public BlockingSearchTaskDelegator(final TaskFactory taskFactory) {
    this.taskFactory = taskFactory;
  }

  public List<SearchSuggestion> retrieveSearchResults(final String query, final Set<CapabilityKey> capabilities) {
    final Map<CapabilityKey, SearchTaskService> tasks = TaskUtils.findTaskServices(taskFactory, capabilities, LOG);

    /**
     * Create a Flowable from the given search services, retrieve search results for
     * the given query in parallel, and combine the result into a single list.
     */
    return Flowable.fromIterable(tasks.values())
        .parallel()
        .runOn(Schedulers.io())
        .map(v -> v.retrieveSearchResults(query))
        .map(Single::blockingGet)
        .doOnError(e -> LOG.error("Error while performing search task:", e))
        .sequential()
        .flatMap(v -> Flowable.fromIterable(v.getSuggestions()))
        .sorted(TaskUtils.getResultComparator())
        .toList()
        .onErrorReturn(e -> {
          LOG.error("General error while executing search task:", e);
          return List.of(SearchSuggestionBuilder.create("Failure during search, please contact the helpdesk"));
        })
        .blockingGet();
  }
}
