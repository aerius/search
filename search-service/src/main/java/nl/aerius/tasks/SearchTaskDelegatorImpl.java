package nl.aerius.tasks;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import nl.aerius.domain.SearchCapability;
import nl.aerius.domain.SearchSuggestion;

/**
 * Delegate a single search task to multiple specialized search services based on the requested capabilities
 */
@Component
public class SearchTaskDelegatorImpl implements SearchTaskDelegator {
  private static final Logger LOG = LoggerFactory.getLogger(SearchTaskDelegatorImpl.class);

  @Autowired TaskFactory taskFactory;

  @Override
  public List<SearchSuggestion> retrieveSearchResults(final String query, final long capabilities) {
    final ArrayList<SearchTaskService> tasks = findTasks(capabilities);

    LOG.debug("Delegating search query [{}] to {} tasks", query, tasks.size());

    return Flowable.fromIterable(tasks)
        .parallel()
        .runOn(Schedulers.computation())
        .map(v -> v.retrieveSearchResults(query))
        .sequential()
        .collectInto(new ArrayList<SearchSuggestion>(), ArrayList::addAll)
        .blockingGet();
  }

  private ArrayList<SearchTaskService> findTasks(final long capabilities) {
    final ArrayList<SearchTaskService> tasks = new ArrayList<>();

    for (final SearchCapability capability : SearchCapability.values()) {
      if (hasCapability(capabilities, capability)) {

        try {
          tasks.add(taskFactory.getTask(capability));
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
