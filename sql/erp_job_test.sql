-- Very simple ERP job test table + procedure for the ERPJobs + Obsidian POC.
-- Lives in the same H2 database already used by erpjobs-h2-poc
-- (jdbc:h2:file:C:/Workspace/erpjobs-h2-poc/data/erpjobs_poc). No new database.

CREATE TABLE IF NOT EXISTS ERP_JOB_TEST (
  ID            IDENTITY PRIMARY KEY,
  JOB_NAME      VARCHAR(100),
  STATUS        VARCHAR(50),
  CREATED_DATE  TIMESTAMP,
  MESSAGE       VARCHAR(500)
);

CREATE ALIAS IF NOT EXISTS SP_ERP_JOB_TEST_RUN FOR "poc.stubs.PocProcedures.insertErpJobTestRow";
