-- Third demo test case for the ERPJobs + Obsidian + H2 POC (CODE FIX demo,
-- new table/procedure - independent of ERP_JOB_TEST/ERP_JOB_STATUS_TEST).
-- Lives in the SAME existing database (jdbc:h2:file:C:/Workspace/erpjobs-h2-poc/data/erpjobs_poc).

CREATE TABLE IF NOT EXISTS ERP_INVENTORY_SYNC_TEST (
  ID            IDENTITY PRIMARY KEY,
  ITEM_CODE     VARCHAR(50),
  QTY           INTEGER,
  STATUS        VARCHAR(20),
  CREATED_DATE  TIMESTAMP,
  MESSAGE       VARCHAR(500)
);

INSERT INTO ERP_INVENTORY_SYNC_TEST (ITEM_CODE, QTY, STATUS, CREATED_DATE, MESSAGE)
  VALUES ('DEMO-ITEM-100', 25, 'PENDING', CURRENT_TIMESTAMP, 'Seed row for inventory sync demo');

-- SP_ERP_INVENTORY_SYNC: marks the seeded row SYNCED. Backed by a new method
-- in the same existing poc.stubs.PocProcedures class.
CREATE ALIAS IF NOT EXISTS SP_ERP_INVENTORY_SYNC FOR "poc.stubs.PocProcedures.syncInventory";
