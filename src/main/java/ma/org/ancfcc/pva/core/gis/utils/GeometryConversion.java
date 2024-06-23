package ma.org.ancfcc.pva.core.gis.utils;

import java.util.Arrays;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeometryConversion {

    /**
     * Converts a 3D geometry to its 2D counterpart.
     * 
     * @param geometry the 3D geometry to be converted
     * @return the 2D geometry
     * @throws IllegalArgumentException if the geometry type is unsupported
     */
    public static Geometry convert3DTo2D(Geometry geometry) {
        validateNotNull(geometry, "Geometry cannot be null");

        if (geometry instanceof Polygon) {
            return convertPolygonTo2D((Polygon) geometry);
        } else if (geometry instanceof MultiPolygon) {
            return convertMultiPolygonTo2D((MultiPolygon) geometry);
        } else if (geometry instanceof Point) {
            return convertPointTo2D((Point) geometry);
        } else if (geometry instanceof LineString) {
            return convertLineStringTo2D((LineString) geometry);
        } else {
            throw new IllegalArgumentException("Unsupported geometry type: " + geometry.getGeometryType());
        }
    }

    public static Geometry convertTo2D(Geometry geometry) {
        validateNotNull(geometry, "Geometry cannot be null");
        return !Double.isNaN(geometry.getCoordinate().z) ? convert3DTo2D(geometry) : geometry;
    }

    private static Polygon convertPolygonTo2D(Polygon polygon) {
        GeometryFactory geomFactory = polygon.getFactory();
        LinearRing shell = GeometryCreation.create2DLinearRing(polygon.getExteriorRing(), geomFactory);
        LinearRing[] holes = new LinearRing[polygon.getNumInteriorRing()];
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            holes[i] = GeometryCreation.create2DLinearRing(polygon.getInteriorRingN(i), geomFactory);
        }
        return geomFactory.createPolygon(shell, holes);
    }

    private static MultiPolygon convertMultiPolygonTo2D(MultiPolygon multiPolygon) {
        GeometryFactory geomFactory = multiPolygon.getFactory();
        int numGeometries = multiPolygon.getNumGeometries();
        Polygon[] polygons = new Polygon[numGeometries];
        for (int i = 0; i < numGeometries; i++) {
            polygons[i] = convertPolygonTo2D((Polygon) multiPolygon.getGeometryN(i));
        }
        return geomFactory.createMultiPolygon(polygons);
    }

    private static Point convertPointTo2D(Point point) {
        GeometryFactory geomFactory = point.getFactory();
        Coordinate newCoord = new Coordinate(point.getX(), point.getY());
        return geomFactory.createPoint(newCoord);
    }

    private static LineString convertLineStringTo2D(LineString line) {
        GeometryFactory geomFactory = line.getFactory();
        Coordinate[] newCoords = Arrays.stream(line.getCoordinates())
                .map(coord -> new Coordinate(coord.x, coord.y))
                .toArray(Coordinate[]::new);
        return geomFactory.createLineString(newCoords);
    }

    private static void validateNotNull(Object obj, String errorMsg) {
        if (obj == null) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

}
