-- Fourth demo test case for the ERPJobs + Obsidian + H2 POC (DATA FIX demo,
-- new table/procedure - independent of ERP_JOB_STATUS_TEST, which already
-- demos the same "renamed column" trick on its own table).
-- Lives in the SAME existing database (jdbc:h2:file:C:/Workspace/erpjobs-h2-poc/data/erpjobs_poc).

CREATE TABLE IF NOT EXISTS ERP_VENDOR_PAYMENT_TEST (
  ID            IDENTITY PRIMARY KEY,
  VENDOR_ID     VARCHAR(50),
  AMOUNT        DECIMAL(12,2),
  PAID_STATUS   VARCHAR(20),
  CREATED_DATE  TIMESTAMP,
  MESSAGE       VARCHAR(500)
);

INSERT INTO ERP_VENDOR_PAYMENT_TEST (VENDOR_ID, AMOUNT, PAID_STATUS, CREATED_DATE, MESSAGE)
  VALUES ('VENDOR-200', 1500.00, 'PENDING', CURRENT_TIMESTAMP, 'Seed row for vendor payment demo');

-- SP_ERP_VENDOR_PAYMENT_UPDATE: marks pending vendor payments PAID. Backed by
-- a new method in the same existing poc.stubs.PocProcedures class. The job
-- class's hardcoded procedure name is correct - to demo the DATA FIX path,
-- rename PAID_STATUS live (e.g. "ALTER TABLE ERP_VENDOR_PAYMENT_TEST ALTER
-- COLUMN PAID_STATUS RENAME TO PAID_STATUS_TMP;") to simulate schema drift,
-- same technique already used on ERP_JOB_STATUS_TEST.STATUS.
CREATE ALIAS IF NOT EXISTS SP_ERP_VENDOR_PAYMENT_UPDATE FOR "poc.stubs.PocProcedures.updateVendorPayment";
