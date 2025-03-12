CREATE TABLE
	IF NOT EXISTS region (
		id BIGSERIAL PRIMARY KEY,
		nom varchar(255) NOT NULL,
		superficie BIGINT NULL,
		description varchar(255) NULL,
		-- delimitation geometry(multipolygon, 4326) NULL,
    status_code INT NULL,
		created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
		last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
		version BIGINT NOT NULL DEFAULT 0,
		created_by VARCHAR(50),
		last_modified_by VARCHAR(50)
	);

CREATE TABLE
	IF NOT EXISTS province (
		id BIGSERIAL PRIMARY KEY,
		nom varchar(255) NOT NULL,
		superficie BIGINT NULL,
		description varchar(255) NULL,
		type_collectivite varchar(255) NULL,
		-- delimitation geometry(multipolygon, 4326) NULL,
		region_id int8 NULL,
		CONSTRAINT fk_region_id FOREIGN KEY (region_id) REFERENCES region (id) ON DELETE CASCADE,
	 
    status_code INT NULL,
		created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
		last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
		version BIGINT NOT NULL DEFAULT 0,
		created_by VARCHAR(50),
		last_modified_by VARCHAR(50)
	);