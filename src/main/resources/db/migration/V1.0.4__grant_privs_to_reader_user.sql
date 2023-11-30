SET
search_path TO wa_workflow_api;

GRANT USAGE ON SCHEMA wa_workflow_api TO "${dbReaderUsername}";
GRANT SELECT ON ALL TABLES IN SCHEMA wa_workflow_api TO "${dbReaderUsername}";
