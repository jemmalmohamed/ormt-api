CREATE TABLE IF NOT EXISTS population (
	id BIGSERIAL PRIMARY KEY,
	taux FLOAT NOT NULL,
	milieu_id BIGINT REFERENCES milieu(id),
	sexe_id BIGINT REFERENCES sexe(id),
	niveau_instruction_id BIGINT REFERENCES niveau_instruction(id),
	periode VARCHAR(30) NOT NULL,
	province_id BIGINT REFERENCES PROVINCE(id),
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	region_id BIGINT REFERENCES REGION(id),
	version int8 NULL,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);
CREATE TABLE TAUX_POPULATION_NIVEAU_INSTRUCTION (
	id BIGINT PRIMARY KEY,
	periode VARCHAR(30),
	taux FLOAT,
	milieu_id BIGINT REFERENCES milieu(id),
	niveau_instruction_id BIGINT REFERENCES niveau_instruction(id),
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	region_id BIGINT REFERENCES REGION(id),
	province_id BIGINT REFERENCES PROVINCE(id),
	version int8 NULL,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);
-- Création de la table POPULATION_ACTIVITE
CREATE TABLE POPULATION_ACTIVE (
	id BIGINT PRIMARY KEY,
	periode VARCHAR(30),
	active BOOLEAN,
	nombre FLOAT,
	milieu_id BIGINT REFERENCES milieu(id),
	sexe_id BIGINT REFERENCES sexe(id),
	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	region_id BIGINT REFERENCES REGION(id),
	province_id BIGINT REFERENCES PROVINCE(id),
	version int8 NULL,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);