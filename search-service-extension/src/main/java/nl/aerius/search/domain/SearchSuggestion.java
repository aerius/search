package nl.aerius.search.domain;

/**
 * TODO Expand with more useful fields, such as a weight, type, icon, link, or whatever else
 */
public class SearchSuggestion {
  private String id;
  
  private String group;
  
  private String description;

  public void setDescription(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(final String group) {
    this.group = group;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
