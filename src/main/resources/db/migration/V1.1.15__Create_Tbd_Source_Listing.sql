CREATE TABLE tbd_source_listing (
    id              BIGSERIAL PRIMARY KEY,
    dashboard_id    BIGINT NOT NULL REFERENCES tbd_dashboard(id) ON DELETE CASCADE,
    source_id       BIGINT NOT NULL REFERENCES source(id),
    ordre           INTEGER DEFAULT 0,
      status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50)
    CONSTRAINT uq_tbd_source_listing UNIQUE (dashboard_id, source_id)
);
