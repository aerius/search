package nl.aerius.search.tasks;

import nl.overheid.aerius.geo.shared.BBox;
import nl.overheid.aerius.geo.shared.RDNew;
import nl.overheid.aerius.shared.geometry.ReceptorUtil;

public class RDNewConstants {
  public static final int MIN_ZL_SURFACE_AREA = 10_000;
  public static final int HEX_HOR = 1529;
  public static final BBox BOUNDS = new BBox(3604.0, 287959.0, 296800.0, 629300.0);

  public static ReceptorUtil create() {
    return ReceptorUtils.createReceptorUtil(RDNew.SRID, MIN_ZL_SURFACE_AREA, HEX_HOR, BOUNDS);
  }
}
