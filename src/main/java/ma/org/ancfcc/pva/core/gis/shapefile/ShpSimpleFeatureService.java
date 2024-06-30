package ma.org.ancfcc.pva.core.gis.shapefile;

import java.util.ArrayList;
import java.util.List;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.locationtech.jts.geom.Geometry;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ma.org.ancfcc.pva.core.gis.utils.GeometryConversion;
import ma.org.ancfcc.pva.core.gis.utils.GeometryUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShpSimpleFeatureService {

    public static String findFeatureAttributIgnoreCase(SimpleFeature feature, String attribut) {
        String attributName = null;
        List<AttributeDescriptor> attributeDescriptors = feature.getType().getAttributeDescriptors();

        for (AttributeDescriptor descriptor : attributeDescriptors) {
            if (descriptor.getLocalName().equalsIgnoreCase(attribut)) {
                attributName = descriptor.getLocalName();
                break;
            }
        }
        return attributName;
    }

    public static List<String> findFeatureListWithPatternIgnoreCase(SimpleFeature feature, String pattern) {
        List<String> matchingAttributes = new ArrayList<>();
        List<AttributeDescriptor> attributeDescriptors = feature.getType().getAttributeDescriptors();

        for (AttributeDescriptor descriptor : attributeDescriptors) {
            if (descriptor.getLocalName().toLowerCase().contains(pattern.toLowerCase())) {
                matchingAttributes.add(descriptor.getLocalName());
            }
        }
        return matchingAttributes;
    }

    public static Geometry get2DGeometryFromFeature(SimpleFeature feature, Integer srid) {
        if (feature.getDefaultGeometry() instanceof Geometry) {
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            geometry = GeometryUtils.geometryIsPolygonOrMultiPolygon(geometry);
            geometry = GeometryConversion.convertTo2D(geometry);
            geometry.setSRID(srid);
            return geometry;
        }
        return null;

    }

}