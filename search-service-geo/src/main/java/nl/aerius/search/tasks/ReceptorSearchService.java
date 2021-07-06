package nl.aerius.search.tasks;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.aerius.geo.util.ReceptorUtil;
import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchTaskResult;

@Component
@ImplementsCapability(SearchCapability.RECEPTORS_28992)
public class ReceptorSearchService implements SearchTaskService {
  @Autowired ReceptorUtil util;

  @Override
  public SearchTaskResult retrieveSearchResults(final String query) {
    return Optional.ofNullable(ReceptorUtils.tryParse(util, query))
        .orElse(SearchResultBuilder.empty());
  }
}
