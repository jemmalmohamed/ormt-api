ALTER TABLE tbd_assignation
DROP CONSTRAINT chk_tbd_assignation_type;

ALTER TABLE tbd_assignation
ADD CONSTRAINT chk_tbd_assignation_type
CHECK (cible_type IN ('DOMAINE', 'CATEGORIE', 'SOUS_DOMAINE'));

COMMENT ON COLUMN tbd_assignation.cible_type IS 'DOMAINE → domaine.id | CATEGORIE → tbd_categorie.id | SOUS_DOMAINE → sous_domaine.id';
