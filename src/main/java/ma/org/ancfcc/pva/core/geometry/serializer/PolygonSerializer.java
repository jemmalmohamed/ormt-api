package ma.org.ancfcc.pva.core.geometry.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import java.io.IOException;

public class PolygonSerializer extends JsonSerializer<Polygon> {

    @Override
    public void serialize(Polygon value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", value.getGeometryType());

        gen.writeArrayFieldStart("coordinates");
        writeCoordinates(gen, value.getExteriorRing().getCoordinates());

        for (int j = 0; j < value.getNumInteriorRing(); j++) {
            writeCoordinates(gen, value.getInteriorRingN(j).getCoordinates());
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