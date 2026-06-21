CREATE TABLE tbd_assignation (
    id              BIGSERIAL PRIMARY KEY,
    dashboard_id    BIGINT NOT NULL REFERENCES tbd_dashboard(id) ON DELETE CASCADE,
    cible_type      VARCHAR(20) NOT NULL,
    cible_id        BIGINT NOT NULL,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT chk_tbd_assignation_type CHECK (cible_type IN ('DOMAINE', 'CATEGORIE')),
    CONSTRAINT uq_tbd_assignation UNIQUE (dashboard_id)
);
COMMENT ON COLUMN tbd_assignation.cible_type IS 'DOMAINE → domaine.id | CATEGORIE → tbd_categorie.id';
