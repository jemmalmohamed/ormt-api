package ma.org.ormt.core.geometry.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

import org.locationtech.jts.geom.Geometry;

public class GeometrySerializer extends JsonSerializer<Geometry> {

    @Override
    public void serialize(Geometry geometry, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", geometry.getGeometryType());

        // Assuming geometry.getCoordinates() returns a proper GeoJSON coordinate
        // structure
        gen.writeObjectField("coordinates", geometry.getCoordinates());

        gen.writeEndObject();
    }
}