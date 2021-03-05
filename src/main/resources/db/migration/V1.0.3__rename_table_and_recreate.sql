SET
search_path TO wa_workflow_api;

DROP TABLE IF EXISTS idempotent_keys;
DROP TABLE IF EXISTS idempotency_keys;

CREATE TABLE idempotency_keys
(
    idempotency_key varchar(200) NOT NULL,
    tenant_id       varchar(20)  NOT NULL,
    process_id      varchar(200) NOT NULL,
    created_at      TIMESTAMP    NOT NULL default CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP    NOT NULL default CURRENT_TIMESTAMP,
    PRIMARY KEY (idempotency_key, tenant_id)
);

COMMIT;
