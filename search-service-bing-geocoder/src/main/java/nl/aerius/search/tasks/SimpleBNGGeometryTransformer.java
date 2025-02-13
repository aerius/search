/*
 * Crown copyright
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

import org.geotools.api.geometry.MismatchedDimensionException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
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
    } catch (final MismatchedDimensionException | ParseException | TransformException e) {
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
