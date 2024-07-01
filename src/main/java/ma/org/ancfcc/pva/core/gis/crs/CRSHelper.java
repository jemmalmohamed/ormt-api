package ma.org.ancfcc.pva.core.gis.crs;

import java.io.IOException;
import java.util.Set;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.NoSuchAuthorityCodeException;
import org.geotools.api.referencing.ReferenceIdentifier;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.metadata.iso.citation.Citations;
import org.geotools.referencing.CRS;
import org.geotools.referencing.wkt.Formattable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CRSHelper {

    public static Integer getSridFromCRS(CoordinateReferenceSystem crs) {
        if (crs == null) {
            return null;
        }

        Set<ReferenceIdentifier> identifiers = crs.getIdentifiers();
        if (identifiers != null && !identifiers.isEmpty()) {
            for (ReferenceIdentifier id : identifiers) {
                if ("EPSG".equalsIgnoreCase(id.getCodeSpace())) {
                    try {
                        return Integer.parseInt(id.getCode());
                    } catch (NumberFormatException e) {
                        // Handle the exception as needed
                    }
                }
            }
        }

        return null;
    }

    public static CoordinateReferenceSystem getCrsFromWkt(String wkt) {
        try {
            return CRS.parseWKT(wkt);
        } catch (FactoryException e) {
            return null;
        }
    }

    public static Integer getOfficialCodeFromCrs(CoordinateReferenceSystem crs) {

        try {
            return CRS.lookupEpsgCode(crs, true);
        } catch (FactoryException ignored) {
            log.debug("Error retrieving official code from CRS", ignored);
        }

        Formattable f = (Formattable) crs;
        String esriWKT = f.toWKT(Citations.ESRI, 2);
        try {
            return CRS.lookupEpsgCode(CRS.parseWKT(esriWKT), true);
        } catch (FactoryException e) {
            log.error("Error retrieving official code from WKT", e);
            return null;
        }
    }

    public static boolean wktIsWGS(String wkt) {
        return wkt.contains("WGS");
    }

    public static String normalizeWKT(String wkt) {
        // Convert to upper case for uniformity
        wkt = wkt.toUpperCase();

        // Replace common synonyms and remove any spaces or unnecessary zeros
        wkt = wkt.replace("LAT/LON", "GCS")
                .replace("D_WGS 84", "WGS_1984")
                .replaceAll("0+?$", "") // Remove trailing zeros
                .replaceAll("[.]0+?$", "."); // Remove trailing zeros after a dot

        return wkt;
    }

    public static Integer getSRIDFromEPSG(String epsgCode) throws FactoryException, IOException {
        try {
            CoordinateReferenceSystem crs = CRS.decode(epsgCode);
            return CRS.lookupEpsgCode(crs, true);
        } catch (NoSuchAuthorityCodeException e) {
            throw new IOException("Error decoding EPSG:" + epsgCode, e);
        }
    }
}
