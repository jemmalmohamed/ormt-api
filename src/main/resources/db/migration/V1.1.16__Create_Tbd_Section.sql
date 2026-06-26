CREATE TABLE tbd_section (
    id              BIGSERIAL PRIMARY KEY,
    dashboard_id    BIGINT NOT NULL REFERENCES tbd_dashboard(id) ON DELETE CASCADE,
    label           VARCHAR(255),
    ordre           INTEGER DEFAULT 0,
    size_percent    INTEGER DEFAULT 33,
    actif           BOOLEAN DEFAULT TRUE,
   status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT chk_tbd_section_size CHECK (size_percent BETWEEN 5 AND 100)
);
COMMENT ON COLUMN tbd_section.size_percent IS 'Hauteur % de la section dans le splitter vertical global. Somme des sections = 100.';
COMMENT ON COLUMN tbd_section.label IS 'Titre affiché au-dessus de la section. Null = pas de titre.';
