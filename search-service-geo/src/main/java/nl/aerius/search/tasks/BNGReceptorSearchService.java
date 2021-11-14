package nl.aerius.search.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.geo.epsg.EPSGRDNew;
import nl.aerius.geo.epsg.RDNewReceptorGridSettings;
import nl.aerius.geo.util.ReceptorUtil;
import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchTaskResult;

@Component
@ImplementsCapability(value = SearchCapability.RECEPTOR, region = SearchRegion.UK)
public class BNGReceptorSearchService implements SearchTaskService {
  private static final Logger LOG = LoggerFactory.getLogger(BNGReceptorSearchService.class);

  private final ReceptorUtil util;

  public BNGReceptorSearchService() {
    // TODO Use BNG grid settings
    util = new ReceptorUtil(new RDNewReceptorGridSettings(new EPSGRDNew()));
  }

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    return Single.just(query)
        .map(v -> ReceptorUtils.tryParse(util, v))
        .onErrorReturn(e -> SearchResultBuilder.empty());
  }
}
