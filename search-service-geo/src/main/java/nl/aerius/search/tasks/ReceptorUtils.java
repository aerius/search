package nl.aerius.search.tasks;

import nl.aerius.geo.domain.ReceptorPoint;
import nl.aerius.geo.util.ReceptorUtil;
import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchSuggestionBuilder;
import nl.aerius.search.domain.SearchTaskResult;

public class ReceptorUtils {
  public static SearchTaskResult tryParse(final ReceptorUtil receptorUtil, final String query) {
    try {
      final int id = Integer.parseInt(query);

      final ReceptorPoint rec = receptorUtil.createReceptorPointFromId(id);
      if (rec != null) {
        return SearchResultBuilder
            .of(SearchSuggestionBuilder.create("Receptor id " + rec.getId() + " at " + (int) rec.getX() + ":" + (int) rec.getY()));
      } else {
        return null;
      }
    } catch (final NumberFormatException e) {
      return null;
    }
  }

}
