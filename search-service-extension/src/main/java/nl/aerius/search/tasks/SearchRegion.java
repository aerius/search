package nl.aerius.search.tasks;

public enum SearchRegion {
  NL,
  UK;

  public static SearchRegion safeValueOf(final String name) {
    try {
      return valueOf(name);
    } catch (final Exception e) {
      return null;
    }
  }
}
