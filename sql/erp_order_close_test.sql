-- Fifth demo test case for the ERPJobs + Obsidian + H2 POC (CODE FIX demo,
-- new table/procedure - independent of ERP_INVENTORY_SYNC_TEST, which already
-- demos the same "typo'd hardcoded procedure name" trick on its own table).
-- Lives in the SAME existing database (jdbc:h2:file:C:/Workspace/erpjobs-h2-poc/data/erpjobs_poc).

CREATE TABLE IF NOT EXISTS ERP_ORDER_CLOSE_TEST (
  ID            IDENTITY PRIMARY KEY,
  ORDER_ID      VARCHAR(50),
  STATUS        VARCHAR(20),
  CLOSED_DATE   TIMESTAMP,
  MESSAGE       VARCHAR(500)
);

INSERT INTO ERP_ORDER_CLOSE_TEST (ORDER_ID, STATUS, CLOSED_DATE, MESSAGE)
  VALUES ('ORDER-300', 'OPEN', NULL, 'Seed row for order close demo');

-- SP_ERP_ORDER_CLOSE: marks the seeded order CLOSED. Backed by a new method
-- in the same existing poc.stubs.PocProcedures class.
CREATE ALIAS IF NOT EXISTS SP_ERP_ORDER_CLOSE FOR "poc.stubs.PocProcedures.closeOrder";
