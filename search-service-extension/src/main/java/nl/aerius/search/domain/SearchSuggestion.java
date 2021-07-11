package nl.aerius.search.domain;

/**
 * TODO Expand with more useful fields, such as a weight, type, icon, link, or whatever else
 */
public class SearchSuggestion {
  private String description;

  public void setDescription(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
