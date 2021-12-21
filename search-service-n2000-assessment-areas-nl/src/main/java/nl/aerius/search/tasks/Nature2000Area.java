package nl.aerius.search.tasks;

public class Nature2000Area {
  private final String id;
  private final String name;
  private final String normalizedName;
  private String wktGeometry;
  private final String wktCentroid;
  private final double area;

  public Nature2000Area(final String id, final String name, final String normalizedName, final String wktGeometry, final String wktCentroid, final double area) {
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
}
