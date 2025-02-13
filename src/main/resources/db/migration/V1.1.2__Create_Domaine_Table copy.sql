CREATE TABLE IF NOT EXISTS domaine (
	id BIGSERIAL PRIMARY KEY,
	titre varchar(255) NOT NULL,
	description varchar(255) NULL,
	
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	version int8 NULL,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);   