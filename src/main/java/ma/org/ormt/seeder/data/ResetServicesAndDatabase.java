package ma.org.ormt.seeder.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
@Order(1)
public class ResetServicesAndDatabase implements CommandLineRunner {

    @Value("${starter.database.reset}")
    private String resetDatabase;

    @Value("${starter.database.seed}")
    private String seedDatabase;

    @Value("${starter.geoserver.reset}")
    private String resetGeoserver;

    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        if (!resetDatabase.equals("true")) {
            log.warn("### DATABASE: Skipping database reset");
            return;
        }

        log.warn("### DATABASE: Resetting database...");
        resetDatabase();
        log.warn("### DATABASE: Database reset successfully.");
    }

    private void resetDatabase() {

        truncateTableIfExists("mission_objet");

        truncateTableIfExists("photo_orientation");
        truncateTableIfExists("photo_execution");
        truncateTableIfExists("photo_planification");

        truncateTableIfExists("scan_execution");
        truncateTableIfExists("bande");

        truncateTableIfExists("analogique_attribut");
        truncateTableIfExists("lidar_attribut");
        truncateTableIfExists("numerique_attribut");
        truncateTableIfExists("objet");
        truncateTableIfExists("mission");
        truncateTableIfExists("plan_action");
        truncateTableIfExists("organisme");
        truncateTableIfExists("avion");
        truncateTableIfExists("capteur");
    }

    @Transactional
    public void truncateTableIfExists(String tableName) {
        String dropConstraintsQuery = "DECLARE @sql NVARCHAR(MAX) = ''; " +
                "SELECT @sql += 'ALTER TABLE ' + QUOTENAME(OBJECT_NAME(parent_object_id)) + ' DROP CONSTRAINT ' + QUOTENAME(name) + '; ' "
                +
                "FROM sys.foreign_keys " +
                "WHERE referenced_object_id = OBJECT_ID(:tableName); " +
                "EXEC sp_executesql @sql;";
        entityManager.createNativeQuery(dropConstraintsQuery)
                .setParameter("tableName", tableName)
                .executeUpdate();

        String checkTableExistenceQuery = "IF EXISTS (SELECT 1 FROM sys.tables WHERE name = :tableName) " +
                "BEGIN " +
                "TRUNCATE TABLE " + tableName + "; " +
                "END";
        entityManager.createNativeQuery(checkTableExistenceQuery)
                .setParameter("tableName", tableName)
                .executeUpdate();

        System.out.println("### DATABASE: Checked and truncated table if exists: " + tableName);
    }

    // @Transactional
    // public void truncateTableIfExists(String tableName) {

    // String checkTableExistenceQuery = "IF EXISTS (SELECT 1 FROM sys.tables WHERE
    // name = :tableName) " +
    // "BEGIN " +
    // "TRUNCATE TABLE " + tableName + "; " +
    // "END";
    // entityManager.createNativeQuery(checkTableExistenceQuery)
    // .setParameter("tableName", tableName)
    // .executeUpdate();
    // log.warn("### DATABASE: Checked and truncated table if exists: " +
    // tableName);
    // }

    @Transactional
    public void deleteRecordsIfExists(String tableName) {
        String checkTableExistenceQuery = "IF EXISTS (SELECT 1 FROM sys.tables WHERE name = :tableName) " +
                "BEGIN " +
                "DELETE FROM " + tableName + "; " +
                "END";
        entityManager.createNativeQuery(checkTableExistenceQuery)
                .setParameter("tableName", tableName)
                .executeUpdate();
        log.warn("### DATABASE: Checked and deleted records if table exists: " + tableName);
    }

}