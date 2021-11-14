package nl.aerius.search.domain;

public class SearchSuggestion {
  private String id;
  private String description;

  private double score;

  private SearchSuggestionType type;
  private String centroid;

  public void setDescription(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  /**
   * Set a score (any value between 0 and 100)
   *
   * @param score a score, the value will be clamped to between 0 and 100
   */
  public void setScore(final double score) {
    this.score = Math.max(0, Math.min(score, 100));
  }

  public double getScore() {
    return score;
  }

  public SearchSuggestionType getType() {
    return type;
  }

  public void setType(final SearchSuggestionType type) {
    this.type = type;
  }

  public String getCentroid() {
    return centroid;
  }

  public void setCentroid(final String centroid) {
    this.centroid = centroid;
  }

  @Override
  public String toString() {
    return String.format("SearchSuggestion [id=%s, description=%s, score=%s, type=%s, centroid=%s]", id, description, score, type,
        centroid);
  }
}
