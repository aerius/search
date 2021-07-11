package nl.aerius.search.domain;

public enum SearchCapability {
  RECEPTOR,

  /**
   * Capabilities used for testing purposes
   */
  MOCK0,
  MOCK1,
  MOCK5;

  public static SearchCapability safeValueOf(final String name) {
    try {
      return valueOf(name);
    } catch (final Exception e) {
      return null;
    }

  }
}
