package ma.org.ormt.core.geometry.serializer;

import java.io.IOException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LineStringSerializer extends JsonSerializer<LineString> {

    @Override
    public void serialize(LineString value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", value.getGeometryType());
        gen.writeArrayFieldStart("coordinates");

        for (Coordinate coord : value.getCoordinates()) {
            gen.writeStartArray();
            gen.writeNumber(coord.x);
            gen.writeNumber(coord.y);
            gen.writeEndArray();
        }

        gen.writeEndArray();
        gen.writeEndObject();
    }
}