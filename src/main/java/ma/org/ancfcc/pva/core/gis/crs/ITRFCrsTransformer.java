package ma.org.ancfcc.pva.core.gis.crs;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ITRFCrsTransformer {

    public static final int CRS_WGS84_SRID = 4326;
    public static final int CRS_ITRF1_SRID = 900914;
    public static final int CRS_ITRF2_SRID = 900915;
    public static final int CRS_ITRF_UNIFIED_SRID = 900918;

    public static final String CRS_ITRF1_PROJ = "PROJCS[\"RFM_ITRF05 / Maroc_Zone_1\",GEOGCS[\"GRS80\",DATUM[\"GRS80\",SPHEROID[\"GRS 1980\",6378137,298.257222101]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Lambert_Conformal_Conic_1SP\"],PARAMETER[\"latitude_of_origin\",33.75],PARAMETER[\"central_meridian\",-5.4],PARAMETER[\"scale_factor\",0.999612517],PARAMETER[\"false_easting\",600000],PARAMETER[\"false_northing\",1400000],UNIT[\"metre\",1]]";
    public static final String CRS_ITRF2_PROJ = "PROJCS[\"RFM_ITRF05 / Maroc_Zone_2\",GEOGCS[\"GRS80\",DATUM[\"GRS80\",SPHEROID[\"GRS 1980\",6378137,298.257222101]], PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Lambert_Conformal_Conic_1SP\"],PARAMETER[\"latitude_of_origin\",30.15],PARAMETER[\"central_meridian\",-5.4],PARAMETER[\"scale_factor\",0.999613123],PARAMETER[\"false_easting\",900000],PARAMETER[\"false_northing\",2400000],UNIT[\"metre\",1]]";

    public static final String CRS_WGS84_PROJ = DefaultGeographicCRS.WGS84.toWKT();

    public static final String CRS_ITRF_UNIFIED_PROJ = "PROJCS[\"RFM_ITRF05 / Maroc_Unified\",\r\n"
            + "  GEOGCS[\"GRS80\",\r\n"
            + "    DATUM[\"GRS80\",\r\n" + "      SPHEROID[\"GRS 1980\",6378137,298.257222101]\r\n" + "    ],\r\n"
            + "    PRIMEM[\"Greenwich\",0],\r\n" + "    UNIT[\"degree\",0.0174532925199433]\r\n" + "  ],\r\n"
            + "  PROJECTION[\"Lambert_Conformal_Conic_1SP\"],\r\n"
            + "  PARAMETER[\"latitude_of_origin\",27.9], \r\n" + "  PARAMETER[\"central_meridian\",-7.2],  \r\n"
            + "  PARAMETER[\"scale_factor\",0.995418633], \r\n" + "  PARAMETER[\"false_easting\",1100000],    \r\n"
            + "  PARAMETER[\"false_northing\",800000],  \r\n" + "  UNIT[\"metre\",1]\r\n" + "]";

    private static String getProjectionBySrid(int srid) {
        switch (srid) {
            case CRS_ITRF1_SRID:
                return CRS_ITRF1_PROJ;
            case CRS_ITRF2_SRID:
                return CRS_ITRF2_PROJ;
            case CRS_ITRF_UNIFIED_SRID:
                return CRS_ITRF_UNIFIED_PROJ;
            case CRS_WGS84_SRID:
                return DefaultGeographicCRS.WGS84.toWKT();

            default:
                return "";
        }
    }

    public static Geometry transformFromTo(Geometry originalGeometry, int sridSource, int sridDestination) {
        try {
            String sourceProj = getProjectionBySrid(sridSource);
            String destinationProj = getProjectionBySrid(sridDestination);

            CoordinateReferenceSystem sourceCRS = CRS.parseWKT(sourceProj);
            CoordinateReferenceSystem targetCRS = CRS.parseWKT(destinationProj);
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
            Geometry transformedGeometry = JTS.transform(originalGeometry, transform);
            transformedGeometry.setSRID(sridDestination);
            return transformedGeometry;
        } catch (Exception e) {
            log.error("Error transforming geometry", e);
            return originalGeometry;
        }
    }

    // public static Geometry transformFromTo(Geometry originalGeometry, String
    // sridSource, String sridDestination) {

    // String sourceProj = "";
    // String destinationProj = "";

    // // source
    // if (sridSource.equals(CRS_ITRF1_SRID)) {
    // sourceProj = CRS_ITRF1_PROJ;
    // }
    // if (sridSource.equals(CRS_ITRF2_SRID)) {
    // sourceProj = CRS_ITRF2_PROJ;
    // }

    // if (sridSource.equals(CRS_ITRF_UNIFIED_SRID)) {
    // sourceProj = CRS_ITRF_UNIFIED_PROJ;
    // }

    // if (sridSource.equals(CRS_WGS84_SRID)) {
    // sourceProj = CRS_WGS84_PROJ;
    // }

    // // destination
    // if (sridDestination.equals(CRS_ITRF1_SRID)) {
    // destinationProj = CRS_ITRF1_PROJ;
    // }
    // if (sridDestination.equals(CRS_ITRF2_SRID)) {
    // destinationProj = CRS_ITRF2_PROJ;
    // }

    // if (sridDestination.equals(CRS_ITRF_UNIFIED_SRID)) {
    // destinationProj = CRS_ITRF_UNIFIED_PROJ;
    // }

    // if (sridDestination.equals(CRS_WGS84_SRID)) {
    // destinationProj = CRS_WGS84_PROJ;
    // }

    // try {

    // CoordinateReferenceSystem sourceCRS = CRS.parseWKT(sourceProj);
    // CoordinateReferenceSystem targetCRS = CRS.parseWKT(destinationProj);
    // MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
    // Geometry transformedGeometry = JTS.transform(originalGeometry, transform);
    // transformedGeometry.setSRID(900918);
    // return transformedGeometry;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return originalGeometry;
    // }

    // public CoordinateReferenceSystem getCoordinateReferenceSystem(String srid) {
    // CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
    // String proj = "";
    // if (srid.equals(CRS_ITRF1_SRID)) {
    // proj = CRS_ITRF1_PROJ;
    // }
    // if (srid.equals(CRS_ITRF2_SRID)) {
    // proj = CRS_ITRF2_PROJ;
    // }

    // if (srid.equals(CRS_ITRF_UNIFIED_SRID)) {
    // proj = CRS_ITRF_UNIFIED_PROJ;
    // }

    // try {
    // crs = CRS.parseWKT(proj);
    // } catch (FactoryException e) {
    // log.info("FactoryException CoordinateReferenceSystem");
    // }
    // return crs;
    // }

}
