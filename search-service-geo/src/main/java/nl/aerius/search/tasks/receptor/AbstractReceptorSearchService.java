package nl.aerius.search.tasks.receptor;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchTaskResult;
import nl.aerius.search.tasks.ReceptorUtils;
import nl.aerius.search.tasks.SearchTaskService;
import nl.overheid.aerius.shared.domain.geo.HexagonZoomLevel;
import nl.overheid.aerius.shared.domain.geo.ReceptorGridSettings;
import nl.overheid.aerius.shared.geometry.ReceptorUtil;

public class AbstractReceptorSearchService implements SearchTaskService {
  private final ReceptorUtil util;
  private final HexagonZoomLevel minimumZoomLevel;

  public AbstractReceptorSearchService(final ReceptorGridSettings settings) {
    this.util = new ReceptorUtil(settings);
    this.minimumZoomLevel = settings.getZoomLevel1();
  }

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    return Single.just(query)
        .map(v -> ReceptorUtils.tryParse(v, util, minimumZoomLevel))
        .onErrorReturn(e -> SearchResultBuilder.empty());
  }
}
