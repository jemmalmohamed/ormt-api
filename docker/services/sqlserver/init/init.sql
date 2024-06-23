-- Create a new database
CREATE DATABASE pva;

GO
    -- Switch to the new database
    USE pva;

GO

-- Create the geometry_columns table
CREATE TABLE geometry_columns (
    f_table_catalog VARCHAR(256),
    f_table_schema VARCHAR(256),
    f_table_name VARCHAR(256),
    f_geometry_column VARCHAR(256),
    coord_dimension INTEGER,
    srid INTEGER,
    type VARCHAR(30)
);
GO