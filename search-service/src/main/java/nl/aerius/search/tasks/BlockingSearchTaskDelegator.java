package nl.aerius.search.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchSuggestion;

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

  public List<SearchSuggestion> retrieveSearchResults(final String query, final long capabilities) {
    final Map<SearchCapability, SearchTaskService> tasks = TaskUtils.findTaskServices(LOG, taskFactory, capabilities);

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
        .map(v -> v.getSuggestions())
        .collectInto(new ArrayList<SearchSuggestion>(), ArrayList::addAll)
        .blockingGet();
  }
}
