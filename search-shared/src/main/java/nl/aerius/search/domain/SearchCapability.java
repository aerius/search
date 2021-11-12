package nl.aerius.search.domain;

public enum SearchCapability {
  RECEPTOR,

  /**
   * Addresses, municipalities, postal codes, etc.
   */
  BASIC_INFO,

  /**
   * Capabilities used for testing purposes
   */
  MOCK_0,
  MOCK_01,
  MOCK_05,
  MOCK_1,
  MOCK_5,

  MOCK_GROUP_0,
  MOCK_GROUP_1;

  public static SearchCapability safeValueOf(final String name) {
    try {
      return valueOf(name);
    } catch (final Exception e) {
      return null;
    }
  }
}
