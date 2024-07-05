package ma.org.ormt.core.gis.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeometryCreation {

    public static LineString createLineString(String... coordinates) {
        List<Coordinate> coordinateList = new ArrayList<>();
        for (String coordinate : coordinates) {
            coordinateList.add(parseCoordinate(coordinate));
        }

        GeometryFactory factory = new GeometryFactory();
        return factory.createLineString(coordinateList.toArray(new Coordinate[0]));
    }

    public static Point createPoint(String coordinate, Integer srid) {
        Point point = createPoint(coordinate);
        point.setSRID(srid);
        return point;
    }

    public static Point createPoint(String coordinate) {
        Coordinate coordinateObj = parseCoordinate(coordinate);
        GeometryFactory factory = new GeometryFactory();

        return factory.createPoint(coordinateObj);
    }

    public static LineString createLineString(String start, String end) {
        Coordinate startCoordinate = parseCoordinate(start);
        Coordinate endCoordinate = parseCoordinate(end);
        return createLingStringFromCoordinate(startCoordinate, endCoordinate);
    }

    public static LineString createLineFromPoints(Point start, Point end) {
        Coordinate startCoordinate = start.getCoordinate();
        Coordinate endCoordinate = end.getCoordinate();
        return createLingStringFromCoordinate(startCoordinate, endCoordinate);
    }

    public static LineString createLingStringFromCoordinate(Coordinate start, Coordinate end) {
        GeometryFactory factory = new GeometryFactory();
        return factory.createLineString(new Coordinate[] { start, end });
    }

    public static Coordinate parseCoordinate(String str) {
        // Parsing string of the format "33°16'48,8\"N 7°34'00,7\"W 4116,5"
        String[] latlong = str.split(" ");
        String lat = latlong[0];
        String lon = latlong[1];

        double latDecimal = parseToDecimalDegrees(lat);
        double lonDecimal = parseToDecimalDegrees(lon);

        return new Coordinate(lonDecimal, latDecimal);
    }

    public static double parseToDecimalDegrees(String dms) {
        // Parsing strings of the format "33°16'48,8\"N"
        String[] parts = dms.split("[°'\"]+");
        double degrees = Double.parseDouble(parts[0]);
        double minutes = Double.parseDouble(parts[1]);
        double seconds = Double.parseDouble(parts[2].replace(",", "."));

        // Convert to decimal
        double decimal = Math.abs(degrees) + (minutes / 60.0) + (seconds / 3600.0);

        // Applying negative sign for S and W coordinates
        if (dms.contains("S") || dms.contains("W")) {
            decimal = -decimal;
        }

        return decimal;
    }

    public static LinearRing create2DLinearRing(LinearRing ring, GeometryFactory geomFactory) {
        Coordinate[] newCoords = Arrays.stream(ring.getCoordinates())
                .map(coord -> new Coordinate(coord.x, coord.y))
                .toArray(Coordinate[]::new);
        return geomFactory.createLinearRing(newCoords);
    }

    public static Polygon ensureRightHandRule(Polygon polygon) {
        GeometryFactory geometryFactory = new GeometryFactory();
        LinearRing shell = polygon.getExteriorRing();
        Coordinate[] coords = shell.getCoordinates();
        if (!Orientation.isCCW(coords)) {
            shell = geometryFactory.createLinearRing(reverseCoordinates(coords));
        }
        return geometryFactory.createPolygon(shell, null);
    }

    public static MultiPolygon ensureRightHandRule(MultiPolygon multiPolygon) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Polygon[] polygons = new Polygon[multiPolygon.getNumGeometries()];
        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            polygons[i] = ensureRightHandRule((Polygon) multiPolygon.getGeometryN(i));
        }
        return geometryFactory.createMultiPolygon(polygons);
    }

    private static Coordinate[] reverseCoordinates(Coordinate[] coords) {
        Coordinate[] reversed = new Coordinate[coords.length];
        for (int i = 0; i < coords.length; i++) {
            reversed[i] = coords[coords.length - 1 - i];
        }
        return reversed;
    }

}
