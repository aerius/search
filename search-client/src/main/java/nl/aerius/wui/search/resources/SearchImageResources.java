package nl.aerius.wui.search.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;

public interface SearchImageResources extends ClientBundle {
  @Source("images/search-button.svg")
  @MimeType("image/svg+xml")
  DataResource searchButton();
}
