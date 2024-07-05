package ma.org.ormt.core.geometry.serializer;

import java.io.IOException;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class PointSerializer extends JsonSerializer<Point> {

    @Override
    public void serialize(Point value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", value.getGeometryType());

        gen.writeArrayFieldStart("coordinates");
        gen.writeNumber(value.getX());
        gen.writeNumber(value.getY());
        gen.writeEndArray();

        gen.writeEndObject();
    }
}