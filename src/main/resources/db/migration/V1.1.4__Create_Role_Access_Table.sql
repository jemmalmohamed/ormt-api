CREATE TABLE
   IF NOT EXISTS  role_acces (
        id BIGSERIAL PRIMARY KEY,
        role_code VARCHAR(50) NOT NULL, -- ex: "public", "decideur", "admin"
        type_ressource VARCHAR(50) NOT NULL, -- ex: "espace", "domaine", "sous_domaine", "indicateur"
        ressource_id BIGINT NOT NULL, -- ID de la ressource spécifique
        niveau_acces VARCHAR(20) NOT NULL, -- ex: "lecture", "modification"
        description varchar(255) NULL, 
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50) ,
        CONSTRAINT uk_role_ressource UNIQUE (role_code, type_ressource, ressource_id)
    );