package nl.aerius.search.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.geo.util.ReceptorUtil;
import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchTaskResult;

@Component
@ImplementsCapability(SearchCapability.RECEPTOR)
public class ReceptorSearchService implements SearchTaskService {
  private static final Logger LOG = LoggerFactory.getLogger(ReceptorSearchService.class);

  @Autowired ReceptorUtil util;

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    return Single.just(query)
        .map(v -> ReceptorUtils.tryParse(util, v))
        .onErrorReturn(e -> SearchResultBuilder.empty());
  }
}
