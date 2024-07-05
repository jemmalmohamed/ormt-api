CREATE TABLE IF NOT EXISTS basemap (
	id BIGSERIAL PRIMARY KEY,
	nom varchar(255) NOT NULL,
	url varchar(255) NOT NULL,
	is_default BOOLEAN DEFAULT false,
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);