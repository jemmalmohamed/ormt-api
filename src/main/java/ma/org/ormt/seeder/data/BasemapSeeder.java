package ma.org.ormt.seeder.data;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.gis.basemap.Basemap;
import ma.org.ormt.modules.gis.basemap.service.BasemapService;

@Component
@Order(2)
@RequiredArgsConstructor
public class BasemapSeeder implements CommandLineRunner {

        @Value("${starter.database.seed}")
        private String seeding;

        private final BasemapService baseMapService;

        @Override
        public void run(String... args) throws Exception {

                if (!seeding.equals("true"))
                        return;

                List<Basemap> basemapList = List.of(

                                // new Basemap("OpenStreet Map",
                                // "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
                                // false),
                                new Basemap("google satellite",
                                                "http://mt0.google.com/vt/lyrs=s&hl=fr&gl=MA&x={x}&y={y}&z={z}"),

                                new Basemap("google terrain",
                                                "http://mt0.google.com/vt/lyrs=p&hl=fr&gl=MA&x={x}&y={y}&z={z}"),
                                new Basemap("google hybrid",
                                                "http://mt0.google.com/vt/lyrs=y&hl=fr&gl=MA&x={x}&y={y}&z={z}"),
                                new Basemap("esri satellite",
                                                "https://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"),
                                new Basemap("esri boundaries  ",
                                                "https://server.arcgisonline.com/ArcGIS/rest/services/reference/World_Boundaries_and_Places/MapServer/tile/{z}/{y}/{x}"),
                                new Basemap("world Shaded Relief",
                                                "https://server.arcgisonline.com/ArcGIS/rest/services/World_Shaded_Relief/MapServer/tile/{z}/{y}/{x}")

                );

                // Save any remaining records that didn't make up a full batch
                if (!basemapList.isEmpty()) {
                        baseMapService.saveAll(basemapList);
                }
        }

}