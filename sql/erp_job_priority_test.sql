-- Third, independent test case for the ERPJobs + Obsidian + H2 POC (demo #2
-- data-fix scenario). Lives in the same existing database as ERP_JOB_TEST /
-- ERP_JOB_STATUS_TEST. Does not touch either of those tables or procedures.
--
-- Deliberate bug for the demo: the column is named PRIORITY_TMP here (not
-- PRIORITY), matching the same "half-finished rename" pattern already seeded
-- in ERP_JOB_STATUS_TEST, so SP_ERP_JOB_PRIORITY_UPDATE's real UPDATE
-- statement (referencing the correct name, PRIORITY) fails with
-- "Column PRIORITY not found" - a genuine schema/data-state mismatch for the
-- RCA automation pipeline to classify as DATA_FIX_REQUIRED.

CREATE TABLE IF NOT EXISTS ERP_JOB_PRIORITY_TEST (
  ID            IDENTITY PRIMARY KEY,
  TYPE          INTEGER,
  PRIORITY_TMP  INTEGER,
  CREATED_DATE  TIMESTAMP,
  MESSAGE       VARCHAR(500)
);

INSERT INTO ERP_JOB_PRIORITY_TEST (TYPE, PRIORITY_TMP, CREATED_DATE, MESSAGE)
  VALUES (7200, 3, CURRENT_TIMESTAMP, 'Test record type 7200');
INSERT INTO ERP_JOB_PRIORITY_TEST (TYPE, PRIORITY_TMP, CREATED_DATE, MESSAGE)
  VALUES (7200, 2, CURRENT_TIMESTAMP, 'Another type 7200');
INSERT INTO ERP_JOB_PRIORITY_TEST (TYPE, PRIORITY_TMP, CREATED_DATE, MESSAGE)
  VALUES (7300, 1, CURRENT_TIMESTAMP, 'Test record type 7300');

CREATE ALIAS IF NOT EXISTS SP_ERP_JOB_PRIORITY_UPDATE FOR "poc.stubs.PocProcedures.updateErpJobPriority";
