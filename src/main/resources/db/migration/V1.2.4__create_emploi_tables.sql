-- Create EmploiTissuEconomique table
CREATE TABLE emploi_tissu_economique (
    id BIGSERIAL PRIMARY KEY,
    emploi_total FLOAT,
    taux_emploi FLOAT,
    creation_nette_emplois INTEGER,
    nombre_entreprises_formelles INTEGER,
    nombre_salaries_formels INTEGER,
    nombre_entreprises_nouvelles INTEGER,
    emplois_generes INTEGER,
    unites_informelles INTEGER,
    emploi_informel FLOAT,
    nombre_cooperatives_agricoles INTEGER,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create QualiteEmploi table
CREATE TABLE qualite_emploi (
    id BIGSERIAL PRIMARY KEY,
    taux_couverture_medicale FLOAT,
    taux_contrat_travail FLOAT,
    taux_travailleurs_occasionnels FLOAT,
    taux_emploi_informel FLOAT,
    taux_sous_emploi FLOAT,
    taux_travail_excessif FLOAT,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create BesoinsEmploisCompetences table
CREATE TABLE besoins_emplois_competences (
    id BIGSERIAL PRIMARY KEY,
    besoins_emplois_region INTEGER,
    besoins_competences_region JSONB,
    besoins_recrutement_region INTEGER,
    besoins_formation_region JSONB,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create DemandeTravail table
CREATE TABLE demande_travail (
    id BIGSERIAL PRIMARY KEY,
    emploi_tissu_economique_id BIGINT REFERENCES emploi_tissu_economique(id),
    qualite_emploi_id BIGINT REFERENCES qualite_emploi(id),
    besoins_emplois_competences_id BIGINT REFERENCES besoins_emplois_competences(id)
);

-- Add indexes for better query performance
CREATE INDEX idx_emploi_tissu_economique_source ON emploi_tissu_economique(source_donnees_id);
CREATE INDEX idx_qualite_emploi_source ON qualite_emploi(source_donnees_id);
CREATE INDEX idx_besoins_emplois_competences_source ON besoins_emplois_competences(source_donnees_id); 