-- Create SalaireMinimum table
CREATE TABLE salaire_minimum (
    id BIGSERIAL PRIMARY KEY,
    smig FLOAT,
    smag FLOAT,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create SalairesProductivite table
CREATE TABLE salaires_productivite (
    id BIGSERIAL PRIMARY KEY,
    salaire_moyen_regional FLOAT,
    productivite_travail FLOAT,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create CompetitiviteSalaires table
CREATE TABLE competitivite_salaires (
    id BIGSERIAL PRIMARY KEY,
    salaire_minimum_id BIGINT REFERENCES salaire_minimum(id),
    salaires_productivite_id BIGINT REFERENCES salaires_productivite(id)
);

-- Add indexes for better query performance
CREATE INDEX idx_salaire_minimum_source ON salaire_minimum(source_donnees_id);
CREATE INDEX idx_salaires_productivite_source ON salaires_productivite(source_donnees_id); 