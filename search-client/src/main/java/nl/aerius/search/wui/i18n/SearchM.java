package nl.aerius.search.wui.i18n;

import com.google.gwt.core.client.GWT;

public class SearchM {
  private static final SearchMessages SEARCH_MESSAGES = GWT.create(SearchMessages.class);

  public static SearchMessages messages() {
    return SEARCH_MESSAGES;
  }
}
