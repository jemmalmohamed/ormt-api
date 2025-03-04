CREATE TABLE IF NOT EXISTS region (
	id BIGSERIAL PRIMARY KEY,
	nom varchar(255) NOT NULL,
	superficie BIGINT NULL,
	description varchar(255) NULL,
	delimitation geometry(multipolygon, 4326) NULL,
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	version int8 NOT NULL DEFAULT 0,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);


CREATE TABLE IF NOT EXISTS province (
	id BIGSERIAL PRIMARY KEY,
	nom varchar(255) NOT NULL,
	superficie BIGINT NULL,
	description varchar(255) NULL,
	type_collectivite varchar(255) NULL,
	delimitation geometry(multipolygon, 4326) NULL,
	region_id int8 NULL,
	CONSTRAINT fk_region_id FOREIGN KEY (region_id) REFERENCES region(id) ON DELETE CASCADE,
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	version int8 NOT NULL DEFAULT 0,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);

CREATE TABLE IF NOT EXISTS basemap (
	id BIGSERIAL PRIMARY KEY,
	nom varchar(255) NOT NULL,
	url varchar(255) NOT NULL,
	
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	version int8 NOT NULL DEFAULT 0,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);  