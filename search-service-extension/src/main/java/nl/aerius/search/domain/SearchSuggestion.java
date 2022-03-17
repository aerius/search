/*
 * Copyright the State of the Netherlands
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package nl.aerius.search.domain;

public class SearchSuggestion {
  public static final double MAX_SCORE = 100;

  private String id;
  private String description;

  private double score;

  private SearchSuggestionType type;
  private String centroid;
  private String geometry;
  private String bbox;

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
    this.score = Math.max(0, Math.min(score, MAX_SCORE));
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

  public void setGeometry(final String geometry) {
    this.geometry = geometry;
  }

  public String getGeometry() {
    return geometry;
  }

  public void setBbox(final String bbox) {
    this.bbox = bbox;
  }

  public String getBbox() {
    return bbox;
  }

  @Override
  public String toString() {
    return String.format("SearchSuggestion [id=%s, description=%s, score=%s, type=%s, centroid=%s, geometry=%s]", id, description, score, type,
        centroid, geometry);
  }
}
