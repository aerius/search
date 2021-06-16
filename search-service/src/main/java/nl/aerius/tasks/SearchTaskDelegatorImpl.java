package nl.aerius.tasks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.aerius.domain.SearchCapability;
import nl.aerius.domain.SearchSuggestion;
import nl.aerius.tasks.mock.Mock0SecondTask;
import nl.aerius.tasks.mock.Mock1SecondTask;
import nl.aerius.tasks.mock.Mock5SecondTask;

/**
 * TODO Replace manual task selection with dynamic task selection
 * 
 * For example: https://stackoverflow.com/questions/19155345/spring-dynamic-factory-based-on-enum
 */
@Component
public class SearchTaskDelegatorImpl implements SearchTaskDelegator {
  @Autowired Mock0SecondTask mock0SecondTask;
  @Autowired Mock1SecondTask mock1SecondTask;
  @Autowired Mock5SecondTask mock5SecondTask;

  /**
   * TODO Parallelize
   */
  @Override
  public List<SearchSuggestion> retrieveSearchResults(final String query, final long capabilities) {
    final ArrayList<SearchSuggestion> lst = new ArrayList<>();

    if (hasCapability(capabilities, SearchCapability.MOCK0)) {
      lst.addAll(mock0SecondTask.retrieveSearchResults(query));
    }

    if (hasCapability(capabilities, SearchCapability.MOCK1)) {
      lst.addAll(mock1SecondTask.retrieveSearchResults(query));
    }

    if (hasCapability(capabilities, SearchCapability.MOCK5)) {
      lst.addAll(mock5SecondTask.retrieveSearchResults(query));
    }

    return lst;
  }

  private static boolean hasCapability(final long capabilities, final SearchCapability capability) {
    return (capabilities & 1 << capability.getBit()) > 0;
  }
}
