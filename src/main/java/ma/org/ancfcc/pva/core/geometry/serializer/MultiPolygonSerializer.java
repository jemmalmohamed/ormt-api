package ma.org.ancfcc.pva.core.geometry.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import java.io.IOException;

public class MultiPolygonSerializer extends JsonSerializer<MultiPolygon> {

    @Override
    public void serialize(MultiPolygon value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "MultiPolygon"); // Hardcoding the type to "MultiPolygon" as per GeoJSON spec

        gen.writeArrayFieldStart("coordinates");
        for (int i = 0; i < value.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) value.getGeometryN(i);

            gen.writeStartArray(); // Start of a new polygon

            // Write exterior ring
            writeCoordinates(gen, polygon.getExteriorRing().getCoordinates());

            // Write interior rings
            for (int j = 0; j < polygon.getNumInteriorRing(); j++) {
                writeCoordinates(gen, polygon.getInteriorRingN(j).getCoordinates());
            }

            gen.writeEndArray(); // End of the polygon
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }

    private void writeCoordinates(JsonGenerator gen, Coordinate[] coordinates) throws IOException {
        gen.writeStartArray();
        for (Coordinate coord : coordinates) {
            gen.writeStartArray();
            gen.writeNumber(coord.x);
            gen.writeNumber(coord.y);

            gen.writeEndArray();
        }
        gen.writeEndArray();
    }
}
