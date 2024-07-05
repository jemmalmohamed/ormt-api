package ma.org.ancfcc.pva.core.gis.utils;

import java.util.Arrays;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ma.org.ancfcc.pva.core.commun.rest.responses.MessageResponse;
import ma.org.ancfcc.pva.core.exceptions.handlers.ShapefileProcessingException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeometryUtils {

    private static final String INVALID_GEOMETRY_MESSAGE = "Invalide géométrie";

    public static Geometry geometryIsPolygonOrMultiPolygon(Geometry geometry) {
        // Check if the geometry is a Polygon or MultiPolygon
        if (!(geometry instanceof Polygon) && !(geometry instanceof MultiPolygon)) {
            String message = MessageResponse.builder()
                    .title(INVALID_GEOMETRY_MESSAGE)
                    .mainMessage("La géométrie n'est pas un polygone ou un multipolygon")
                    .build()
                    .format();
            throw new ShapefileProcessingException(message);
        }
        // Return the validated geometry
        return geometry;
    }

    public static Geometry geometryIsPoint(Geometry geometry) {
        // Check if the geometry is a Point
        if (!geometry.getGeometryType().equals("Point")) {
            String message = MessageResponse.builder()
                    .title(INVALID_GEOMETRY_MESSAGE)
                    .mainMessage("La géométrie n'est pas un point")
                    .build()
                    .format();
            throw new ShapefileProcessingException(message);
        }
        // Return the validated geometry
        return geometry;
    }

    public static Geometry geometryIsPolygon(Geometry geometry) {
        // Check if the geometry is a Polygon or MultiPolygon
        if (!(geometry instanceof Polygon)) {
            String message = MessageResponse.builder()
                    .title(INVALID_GEOMETRY_MESSAGE)
                    .mainMessage("La géométrie n'est pas un polygon")
                    .build()
                    .format();
            throw new ShapefileProcessingException(message);
        }
        // Return the validated geometry
        return geometry;
    }

    public static SimpleFeatureCollection validateMultiGeometry(SimpleFeatureCollection collection) {
        // Check if the geometry is a Polygon or MultiPolygon
        boolean hasMultiFeature = collection.size() > 1;
        if (hasMultiFeature) {
            String message = MessageResponse.builder()
                    .title(INVALID_GEOMETRY_MESSAGE)
                    .mainMessage("Le shapefile contient plusieurs géométries")
                    .build()
                    .format();
            throw new ShapefileProcessingException(message);
        }
        // Return the validated geometry
        return collection;
    }

    public static boolean multiPolygonHasOnePolygon(MultiPolygon multiPolygon) {
        return multiPolygon.getNumGeometries() <= 1;
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

    public static Point extract2DPointFromFeature(SimpleFeature feature) {
        Geometry geometry = (Geometry) feature.getDefaultGeometry();
        GeometryUtils.geometryIsPoint(geometry);
        return (Point) GeometryConversion.convertTo2D(geometry);
    }

    public static Point extract2DPointFromFeature(SimpleFeature feature, Integer srid) {
        Point point = extract2DPointFromFeature(feature);
        point.setSRID(srid);
        return point;
    }

}
