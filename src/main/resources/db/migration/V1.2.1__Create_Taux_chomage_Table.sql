CREATE TABLE IF NOT EXISTS taux_chomage (
	id BIGSERIAL PRIMARY KEY,
	taux FLOAT NOT NULL,
	milieu_id BIGINT REFERENCES milieu(id),
	sexe_id BIGINT REFERENCES sexe(id),
	tranche_age_id BIGINT REFERENCES tranche_age(id),
	periode VARCHAR(30) NOT NULL,
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	version int8 NULL,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);
CREATE TABLE TAUX_CHOMAGE_REGION (
	id BIGSERIAL PRIMARY KEY,
	region_id BIGINT REFERENCES REGION(id),
	taux_chomage_id BIGINT REFERENCES TAUX_CHOMAGE(id),
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	version int8 NULL,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);
-- Création de la table TAUX_CHOMAGE_PROVINCE
CREATE TABLE TAUX_CHOMAGE_PROVINCE (
	id BIGSERIAL PRIMARY KEY,
	province_id BIGINT REFERENCES PROVINCE(id),
	taux_chomage_id BIGINT REFERENCES TAUX_CHOMAGE(id),
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	version int8 NULL,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);