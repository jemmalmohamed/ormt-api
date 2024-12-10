-- Create IntermediationPublique table
CREATE TABLE intermediation_publique (
    id BIGSERIAL PRIMARY KEY,
    nombre_chercheurs_emploi INTEGER,
    nombre_entretiens_positionnement INTEGER,
    nombre_participants_ateliers INTEGER,
    nombre_offres_emploi INTEGER,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create IntermediationPrivee table
CREATE TABLE intermediation_privee (
    id BIGSERIAL PRIMARY KEY,
    nombre_agences_recrutement INTEGER,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create IntermediationTravail table
CREATE TABLE intermediation_travail (
    id BIGSERIAL PRIMARY KEY,
    intermediation_publique_id BIGINT REFERENCES intermediation_publique(id),
    intermediation_privee_id BIGINT REFERENCES intermediation_privee(id)
);

-- Add indexes for better query performance
CREATE INDEX idx_intermediation_publique_source ON intermediation_publique(source_donnees_id);
CREATE INDEX idx_intermediation_privee_source ON intermediation_privee(source_donnees_id); 