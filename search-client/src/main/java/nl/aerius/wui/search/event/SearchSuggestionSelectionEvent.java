package nl.aerius.wui.search.event;

import nl.aerius.wui.event.SimpleGenericEvent;
import nl.aerius.wui.search.domain.SearchSuggestion;

public class SearchSuggestionSelectionEvent extends SimpleGenericEvent<SearchSuggestion> {
  public SearchSuggestionSelectionEvent(final SearchSuggestion value) {
    super(value);
  }
}
