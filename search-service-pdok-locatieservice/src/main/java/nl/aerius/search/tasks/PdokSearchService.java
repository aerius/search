package nl.aerius.search.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Single;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchSuggestionBuilder;
import nl.aerius.search.domain.SearchTaskResult;

@Component
@ImplementsCapability(value = SearchCapability.BASIC_INFO, region = SearchRegion.NL)
public class PdokSearchService implements SearchTaskService {
  private static final Logger LOG = LoggerFactory.getLogger(PdokSearchService.class);

  @Override
  public Single<SearchTaskResult> retrieveSearchResults(final String query) {
    return Single.just(SearchResultBuilder
        .of(SearchSuggestionBuilder.create(String.format("pdok result: %s", query))));
  }
}
