package nl.aerius.search.tasks;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleBNGGeometryTransformer {
  private static final Logger LOG = LoggerFactory.getLogger(SimpleBNGGeometryTransformer.class);

  private static final int SRID_BNG = 27700;

  private final MathTransform wgsToRdNewtransform;

  public SimpleBNGGeometryTransformer() {
    this.wgsToRdNewtransform = createCRSTransform();
  }

  public String toBNGWKT(final String wkt) {
    LOG.info("Converting: {}", wkt);

    final WKTReader rdr = new WKTReader();
    try {
      final Geometry geom = rdr.read(wkt);

      final Geometry result = JTS.transform(geom, wgsToRdNewtransform);

      final WKTWriter writer = new WKTWriter();
      final String unionWkt = writer.write(result);
      LOG.info("Input: {} Output: {}", wkt, unionWkt);
      return unionWkt;
    } catch (final MismatchedDimensionException e) {
      throw new InterpretationRuntimeException(e);
    } catch (final TransformException e) {
      throw new InterpretationRuntimeException(e);
    } catch (final ParseException e) {
      throw new InterpretationRuntimeException(e);
    }
  }

  private static MathTransform createCRSTransform() {
    final CoordinateReferenceSystem targetCRS;
    try {
      final CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4258");
      targetCRS = CRS.decode("EPSG:" + SRID_BNG);

      return CRS.findMathTransform(sourceCRS, targetCRS);
    } catch (final FactoryException e) {
      LOG.error("Could not initialize coordinate reference system");
      throw new InterpretationRuntimeException(e);
    }
  }
}
