-- Second, independent test case for the ERPJobs + Obsidian + H2 POC.
-- Lives in the SAME existing database as ERP_JOB_TEST/SP_ERP_JOB_TEST_RUN
-- (jdbc:h2:file:C:/Workspace/erpjobs-h2-poc/data/erpjobs_poc). Does not
-- touch that table or procedure - this is a separate table/alias pair.

CREATE TABLE IF NOT EXISTS ERP_JOB_STATUS_TEST (
  ID            IDENTITY PRIMARY KEY,
  TYPE          INTEGER,
  STATUS        INTEGER,
  CREATED_DATE  TIMESTAMP,
  MESSAGE       VARCHAR(500)
);

-- Seed data. This block is written to run once; re-running the script would
-- append a second set of rows since H2 1.4.192 has no easy single-statement
-- "insert only if empty" guard for a literal VALUES list.
INSERT INTO ERP_JOB_STATUS_TEST (TYPE, STATUS, CREATED_DATE, MESSAGE)
  VALUES (4110, 1, CURRENT_TIMESTAMP, 'Test record type 4110');
INSERT INTO ERP_JOB_STATUS_TEST (TYPE, STATUS, CREATED_DATE, MESSAGE)
  VALUES (4110, 2, CURRENT_TIMESTAMP, 'Another type 4110');
INSERT INTO ERP_JOB_STATUS_TEST (TYPE, STATUS, CREATED_DATE, MESSAGE)
  VALUES (4120, 1, CURRENT_TIMESTAMP, 'Test record type 4120');
INSERT INTO ERP_JOB_STATUS_TEST (TYPE, STATUS, CREATED_DATE, MESSAGE)
  VALUES (4130, 3, CURRENT_TIMESTAMP, 'Test record type 4130');
INSERT INTO ERP_JOB_STATUS_TEST (TYPE, STATUS, CREATED_DATE, MESSAGE)
  VALUES (4110, 4, CURRENT_TIMESTAMP, 'Third type 4110');

-- SP_ERP_JOB_STATUS_UPDATE: conditional UPDATE, TYPE=4110 rows -> STATUS=5.
-- Backed by a new method in the same existing poc.stubs.PocProcedures class
-- already used by SP_POC_NOOP / SP_ERP_JOB_TEST_RUN.
CREATE ALIAS IF NOT EXISTS SP_ERP_JOB_STATUS_UPDATE FOR "poc.stubs.PocProcedures.updateErpJobStatus";
