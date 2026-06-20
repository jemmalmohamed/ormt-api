CREATE TABLE tbd_dashboard (
    id                  BIGSERIAL PRIMARY KEY,
    nom                 VARCHAR(255) NOT NULL,
    titre               VARCHAR(255),
    sous_titre          VARCHAR(255),
    description         TEXT,
    periode_label       VARCHAR(100),
    actif               BOOLEAN DEFAULT TRUE,
    status              VARCHAR(30) DEFAULT 'DRAFT',
    status_code         INTEGER DEFAULT 1,
    created_date        TIMESTAMPTZ DEFAULT NOW(),
    last_modified_date  TIMESTAMPTZ DEFAULT NOW(),
    version             BIGINT DEFAULT 0,
    created_by          VARCHAR(50),
    last_modified_by    VARCHAR(50),
    CONSTRAINT chk_tbd_dashboard_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED'))
);
