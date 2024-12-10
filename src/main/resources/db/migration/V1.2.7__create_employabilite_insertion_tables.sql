-- Create InsertionProgrammesActifs table
CREATE TABLE insertion_programmes_actifs (
    id BIGSERIAL PRIMARY KEY,
    beneficiaires_programmes_emploi INTEGER,
    beneficiaires_employabilite INTEGER,
    entreprises_creees INTEGER,
    emplois_generes INTEGER,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create InsertionLaureatsFP table
CREATE TABLE insertion_laureats_fp (
    id BIGSERIAL PRIMARY KEY,
    taux_insertion_9mois FLOAT,
    taux_insertion_3ans FLOAT,
    taux_emploi_laureats_fp FLOAT,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create InsertionDiplomesES table
CREATE TABLE insertion_diplomes_es (
    id BIGSERIAL PRIMARY KEY,
    taux_emploi_diplomes_es FLOAT,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create AdequationFormationEmploi table
CREATE TABLE adequation_formation_emploi (
    id BIGSERIAL PRIMARY KEY,
    taux_adequation FLOAT,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create EmployabiliteInsertion table
CREATE TABLE employabilite_insertion (
    id BIGSERIAL PRIMARY KEY,
    insertion_programmes_actifs_id BIGINT REFERENCES insertion_programmes_actifs(id),
    insertion_laureats_fp_id BIGINT REFERENCES insertion_laureats_fp(id),
    insertion_diplomes_es_id BIGINT REFERENCES insertion_diplomes_es(id),
    adequation_formation_emploi_id BIGINT REFERENCES adequation_formation_emploi(id)
);

-- Add indexes for better query performance
CREATE INDEX idx_insertion_programmes_actifs_source ON insertion_programmes_actifs(source_donnees_id);
CREATE INDEX idx_insertion_laureats_fp_source ON insertion_laureats_fp(source_donnees_id);
CREATE INDEX idx_insertion_diplomes_es_source ON insertion_diplomes_es(source_donnees_id);
CREATE INDEX idx_adequation_formation_emploi_source ON adequation_formation_emploi(source_donnees_id); 