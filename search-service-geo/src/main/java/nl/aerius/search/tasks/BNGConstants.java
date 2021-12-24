package nl.aerius.search.tasks;

import nl.overheid.aerius.geo.shared.BBox;
import nl.overheid.aerius.geo.shared.BNGrid;
import nl.overheid.aerius.shared.geometry.ReceptorUtil;

public class BNGConstants {
  // NOTE Both of the below values are not yet guaranteed to be as constant as the
  // RDNew counterparts
  public static final int MIN_ZL_SURFACE_AREA = 250_000;
  public static final int HEX_HOR = 721;
  public static final BBox BOUNDS = new BBox(-90619.29, 10097.13, 612435.55, 1234954.16);

  public static ReceptorUtil create() {
    return ReceptorUtils.createReceptorUtil(BNGrid.SRID, BNGConstants.MIN_ZL_SURFACE_AREA, BNGConstants.HEX_HOR, BNGConstants.BOUNDS);
  }
}
