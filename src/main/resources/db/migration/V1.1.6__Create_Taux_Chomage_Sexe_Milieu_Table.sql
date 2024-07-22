CREATE TABLE IF NOT EXISTS taux_chomage_diplome_milieu (
	id BIGSERIAL PRIMARY KEY,
	annee varchar(255) NOT NULL,
    taux float4 NOT NULL,
 	milieu varchar(255) NOT NULL,
	diplome varchar(255) NOT NULL,

	region_id int8 NULL,
	CONSTRAINT fk_region_id FOREIGN KEY (region_id) REFERENCES region(id),

	province_id int8 NULL,
	CONSTRAINT province_id FOREIGN KEY (province_id) REFERENCES province(id),


	status_code int4 NULL,
	created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
	version int8 NULL,
	created_by varchar(255) NULL,
	last_modified_by varchar(255) NULL
);  