CREATE TABLE tbd_widget_row (
    id              BIGSERIAL PRIMARY KEY,
    section_id      BIGINT NOT NULL REFERENCES tbd_section(id) ON DELETE CASCADE,
    ordre           INTEGER DEFAULT 0,
    size_percent    INTEGER DEFAULT 50,
   status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50)
    CONSTRAINT chk_tbd_widget_row_size CHECK (size_percent BETWEEN 5 AND 100)
);
COMMENT ON COLUMN tbd_widget_row.size_percent IS 'Hauteur % de la row dans le splitter vertical interne à la section. Somme des rows dans une section = 100.';
