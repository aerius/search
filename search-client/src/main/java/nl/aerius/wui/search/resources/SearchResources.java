package nl.aerius.wui.search.resources;

import com.google.gwt.core.client.GWT;

public class SearchResources {
  private static final SearchImageResources RESOURCES = GWT.create(SearchImageResources.class);

  /**
   * Access to image resources.
   */
  public static SearchImageResources images() {
    return RESOURCES;
  }
}
