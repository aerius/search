package nl.aerius.search.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.aerius.geo.domain.ReceptorPoint;
import nl.aerius.geo.util.ReceptorUtil;
import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchTaskResult;
import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchSuggestionBuilder;

@Component
@ImplementsCapability(SearchCapability.RECEPTORS_28992)
public class RDNewReceptorSearchService implements SearchTaskService {
  @Autowired ReceptorUtil util;

  @Override
  public SearchTaskResult retrieveSearchResults(final String query) {
    try {
      final int id = Integer.parseInt(query);

      final ReceptorPoint rec = util.createReceptorPointFromId(id);
      if (rec != null) {
        return SearchResultBuilder
            .of(SearchSuggestionBuilder.create("Receptor id " + rec.getId() + " at " + (int) rec.getX() + ":" + (int) rec.getY()));
      }
    } catch (final NumberFormatException e) {
      // Eat
    }

    return SearchResultBuilder.empty();
  }
}
