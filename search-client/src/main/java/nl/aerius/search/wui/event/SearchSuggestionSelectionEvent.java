package nl.aerius.search.wui.event;

import nl.aerius.search.wui.domain.SearchSuggestion;
import nl.aerius.wui.event.SimpleGenericEvent;

public class SearchSuggestionSelectionEvent extends SimpleGenericEvent<SearchSuggestion> {
  public SearchSuggestionSelectionEvent(final SearchSuggestion value) {
    super(value);
  }
}
