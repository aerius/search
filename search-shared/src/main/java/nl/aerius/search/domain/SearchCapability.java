package nl.aerius.search.domain;

public enum SearchCapability {
  RECEPTORS_28992(0),

  RESERVED_1(1),
  RESERVED_2(2),
  RESERVED_3(3),
  RESERVED_4(4),
  RESERVED_5(5),
  RESERVED_6(6),
  RESERVED_7(7),
  RESERVED_8(8),
  RESERVED_9(9),
  RESERVED_10(10),
  RESERVED_11(11),
  RESERVED_12(12),
  RESERVED_13(13),
  RESERVED_14(14),
  RESERVED_15(15),
  RESERVED_16(16),
  RESERVED_17(17),
  RESERVED_18(18),
  RESERVED_19(19),
  RESERVED_20(20),
  RESERVED_21(21),
  RESERVED_22(22),
  RESERVED_23(23),
  RESERVED_24(24),
  RESERVED_25(25),
  RESERVED_26(26),
  RESERVED_27(27),

  /**
   * Capabilities used for testing purposes
   */
  MOCK0(28),
  MOCK1(29),
  MOCK5(30);

  private final int bit;

  private SearchCapability(final int bit) {
    this.bit = bit;
  }

  public int bit() {
    return bit;
  }
}
