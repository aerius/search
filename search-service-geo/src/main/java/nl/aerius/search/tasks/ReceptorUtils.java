package nl.aerius.search.tasks;

import nl.aerius.geo.domain.ReceptorPoint;
import nl.aerius.geo.util.ReceptorUtil;
import nl.aerius.search.domain.SearchResultBuilder;
import nl.aerius.search.domain.SearchSuggestion;
import nl.aerius.search.domain.SearchSuggestionBuilder;
import nl.aerius.search.domain.SearchSuggestionType;
import nl.aerius.search.domain.SearchTaskResult;

public class ReceptorUtils {
  // TODO i18n
  private static final String RECEPTOR_FORMAT = "Receptor %s - x:%s y:%s";

  private ReceptorUtils() {
  }

  public static SearchTaskResult tryParse(final ReceptorUtil receptorUtil, final String query) throws NumberFormatException {
    final int id = Integer.parseInt(query);

    final ReceptorPoint rec = receptorUtil.createReceptorPointFromId(id);
    return SearchResultBuilder.of(getSearchSuggestion(rec));
  }

  public static SearchSuggestion getSearchSuggestion(final ReceptorPoint rec) {
    return SearchSuggestionBuilder.create(String.format(RECEPTOR_FORMAT, rec.getId(), (int) rec.getX(), (int) rec.getY()),
        100, SearchSuggestionType.RECEPTOR);
  }

}
