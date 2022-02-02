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
package nl.aerius.search.tasks;

public class Nature2000Area {
  private final String id;
  private final String name;
  private final String normalizedName;
  private String wktGeometry;
  private String wktCentroid;
  private final double area;

  public Nature2000Area(final String id, final String name, final String normalizedName, final String wktGeometry, final String wktCentroid,
      final double area) {
    this.id = id;
    this.name = name;
    this.normalizedName = normalizedName;
    this.wktGeometry = wktGeometry;
    this.wktCentroid = wktCentroid;
    this.area = area;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getNormalizedName() {
    return normalizedName;
  }

  public String getWktGeometry() {
    return wktGeometry;
  }

  public String getWktCentroid() {
    return wktCentroid;
  }

  public double getArea() {
    return area;
  }

  public void setWktGeometry(final String wktGeometry) {
    this.wktGeometry = wktGeometry;
  }

  public void setWktCentroid(final String wktCentroid) {
    this.wktCentroid = wktCentroid;
  }
}
