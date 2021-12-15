package nl.aerius.search.tasks;

public class Nature2000Area {
  private final String id;
  private final String name;
  private final String normalizedName;
  private final String wktGeometry;
  private final String wktCentroid;

  public Nature2000Area(final String id, final String name, final String normalizedName, final String wktGeometry, final String wktCentroid) {
    this.id = id;
    this.name = name;
    this.normalizedName = normalizedName;
    this.wktGeometry = wktGeometry;
    this.wktCentroid = wktCentroid;
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
}
