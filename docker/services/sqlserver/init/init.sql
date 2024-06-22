-- Create a new database
CREATE DATABASE pva;

GO
    -- Switch to the new database
    USE pva;

GO
    -- Create a table with a spatial column
    CREATE TABLE SpatialTable (
        Id INT PRIMARY KEY,
        Name NVARCHAR(50),
        Location GEOGRAPHY
    );

GO
    -- Insert a sample record with SRID
INSERT INTO
    SpatialTable (Id, Name, Location)
VALUES
    (
        1,
        'Sample Location',
        geography :: STGeomFromText('POINT(-122.34900 47.65100)', 4326)
    );

GO