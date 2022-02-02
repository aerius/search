package nl.aerius.search.tasks;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kong.unirest.Unirest;

/**
 * Rinky dink wfs interpreter that fetches natura 2000 areas and geometries from
 * an open data resource.
 *
 * Should be replaced over the medium term.
 */
@Component
public class Natura2000WfsInterpreter {
  private static final double DOUGLAS_PEUCKER_TOLERANCE = 5D;

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentAreaSearchService.class);

  private static final int SRID_RDNEW = 28992;

  private final GeometryFactory rdNewGeometryFactory = new GeometryFactory(new PrecisionModel(), SRID_RDNEW);

  private MathTransform wgsToRdNewtransform;

  // @formatter:off
  /**
   * @see <a href="https://www.nationaalgeoregister.nl/geonetwork/srv/api/records/280ed37a-b8d2-4ac5-8567-04d84fad3a41">Resource URL</a>
   * @see <a href="https://nationaalgeoregister.nl/geonetwork/srv/dut/catalog.search#/metadata/280ed37a-b8d2-4ac5-8567-04d84fad3a41">Catalog URL</a>
   */
  @Value("${nl.aerius.wfs.n2000:http://geodata.nationaalgeoregister.nl/inspire/ps-natura2000/wfs?request=GetFeature&service=WFS&version=2.0.0&typeName=ps-natura2000:ProtectedSite&outputFormat=application%2Fgml%2Bxml%3B%20version%3D3.2}")
  private String wfsNatura2000Url;
  // @formatter:on

  @SuppressWarnings("unchecked")
  public Map<String, Nature2000Area> retrieveAreas() {
    final CoordinateReferenceSystem targetCRS;
    try {
      final CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4258");
      targetCRS = CRS.decode("EPSG:28992");

      wgsToRdNewtransform = CRS.findMathTransform(sourceCRS, targetCRS);
    } catch (final FactoryException e) {
      LOG.error("Could not initialize coordinate reference system");
      throw new RuntimeException(e);
    }

    LOG.info("Retrieving from {}", wfsNatura2000Url.split("\\?")[0]);
    final byte[] body = Unirest.get(wfsNatura2000Url)
        .asBytes()
        .getBody();

    LOG.info("Got WFS response.");

    final InputStream wfsStream = new ByteArrayInputStream(body);

    final SAXReader reader = new SAXReader();
    Document document;
    try {
      document = reader.read(wfsStream);
    } catch (final DocumentException e) {
      LOG.error("Could not interpret WFS response; assessment areas will not be searchable as a result.");
      throw new RuntimeException(e);
    }

    final Map<String, Nature2000Area> areas = new HashMap<>();

    final Element rootElem = document.getRootElement();
    ((List<DefaultElement>) rootElem.elements()).forEach(elem -> {
      final Nature2000Area area = processArea(elem.element("ProtectedSite"));
      areas.merge(area.getNormalizedName(), area, (a, b) -> merge(a, b, targetCRS));
    });

    return areas;
  }

  private Nature2000Area merge(final Nature2000Area a, final Nature2000Area b, final CoordinateReferenceSystem crs) {
    final String geometryAWkt = a.getWktGeometry();
    final String geometryBWkt = b.getWktGeometry();

    final WKTReader rdr = new WKTReader();
    try {
      final Geometry geometryA = rdr.read(geometryAWkt);
      final Geometry geometryB = rdr.read(geometryBWkt);

      final WKTWriter writer = new WKTWriter();

      final Geometry union = geometryA.union(geometryB);
      final String unionWkt = writer.write(union);

      final Geometry unionCentroid = union.getCentroid();
      final String centroidWkt = writer.write(unionCentroid);
      a.setWktGeometry(unionWkt);
      a.setWktCentroid(centroidWkt);

    } catch (final ParseException e) {
      throw new RuntimeException("Cannot read WKT geometries.", e);
    }

    return a;
  }

  private Nature2000Area processArea(final Element protectedSite) {
    final String id = protectedSite
        .element("inspireID")
        .element("Identifier")
        .elementTextTrim("localId");
    final String name = protectedSite
        .element("siteName")
        .element("GeographicalName")
        .element("spelling")
        .element("SpellingOfName")
        .elementTextTrim("text");

    final String normalizedName = normalize(name);

    final Geometry resultGeometry = readGeometry(protectedSite);

    LOG.info("Area: {} - {} - {} - {}m2", id, normalizedName, name, resultGeometry.getArea());

    final WKTWriter wktWriter = new WKTWriter();
    final String wktGeometry = wktWriter.write(resultGeometry);
    final String wktCentroid = wktWriter.write(resultGeometry.getCentroid());

    return new Nature2000Area(id, name, normalizedName, wktGeometry, wktCentroid, resultGeometry.getArea());
  }

  private Geometry readGeometry(final Element protectedSite) {
    final List members = protectedSite
        .element("geometry")
        .element("MultiSurface")
        .elements("surfaceMember");
    LOG.info("Joining {} members.", members.size());

    Geometry finalGeometry = null;
    for (int i = 0; i < members.size(); i++) {
      final Element member = (Element) members.get(i);
      final String geometry = member
          .element("Polygon")
          .element("exterior")
          .element("LinearRing")
          .elementTextTrim("posList");

      final Coordinate[] coords = coordinatesFromString(geometry);
      final Polygon ext = rdNewGeometryFactory.createPolygon(coords);

      if (finalGeometry == null) {
        finalGeometry = ext;
      } else {
        finalGeometry = finalGeometry.union(ext);
      }
    }

    final DouglasPeuckerSimplifier simplifier = new DouglasPeuckerSimplifier(finalGeometry);
    simplifier.setDistanceTolerance(DOUGLAS_PEUCKER_TOLERANCE);

    return simplifier.getResultGeometry();
  }

  public String normalize(final String name) {
    return Normalizer.normalize(name, Form.NFC)
        .toLowerCase();
  }

  private Coordinate[] coordinatesFromString(final String feature) {
    final String[] parts = feature.split(" ");

    final Coordinate[] coordinates = new Coordinate[parts.length / 2];

    for (int i = 0; i < parts.length; i += 2) {
      try {
        final Coordinate coord = new Coordinate(Double.parseDouble(parts[i]), Double.parseDouble(parts[i + 1]));

        Envelope result;
        try {
          result = JTS.transform(new Envelope(coord), wgsToRdNewtransform);
        } catch (final TransformException e) {
          throw new RuntimeException("Failed transform", e);
        }

        coordinates[i / 2] = result.centre();
      } catch (final NumberFormatException e) {
        LOG.info("Parsing: {} > {}", parts[i], parts[i + 1]);
        continue;
      }
    }

    return coordinates;
  }
}
