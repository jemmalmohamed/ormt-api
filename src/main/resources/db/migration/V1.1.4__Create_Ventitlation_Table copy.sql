CREATE TABLE IF NOT EXISTS milieu (
	id BIGSERIAL PRIMARY KEY,
	nom VARCHAR(255) NOT NULL,
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	version int8 NULL,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);
CREATE TABLE sexe (
	id BIGINT PRIMARY KEY,
	nom VARCHAR(30),
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	version int8 NULL,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);
CREATE TABLE niveau_instruction (
	id BIGINT PRIMARY KEY,
	nom VARCHAR(30),
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	version int8 NULL,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);
CREATE TABLE tranche_age (
	id BIGINT PRIMARY KEY,
	nom VARCHAR(30),
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	version int8 NULL,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);