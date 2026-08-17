-- Ninth demo test case (healthy/completes-successfully by design - no deliberate bug).
CREATE TABLE IF NOT EXISTS ERP_PURCHASE_ORDER_SYNC_TEST (
  ID            IDENTITY PRIMARY KEY,
  PO_NUMBER     VARCHAR(50),
  STATUS        VARCHAR(20),
  CREATED_DATE  TIMESTAMP,
  MESSAGE       VARCHAR(500)
);

INSERT INTO ERP_PURCHASE_ORDER_SYNC_TEST (PO_NUMBER, STATUS, CREATED_DATE, MESSAGE)
  VALUES ('PO-700', 'PENDING', CURRENT_TIMESTAMP, 'Seed row for purchase order sync demo');

CREATE ALIAS IF NOT EXISTS SP_ERP_PURCHASE_ORDER_SYNC FOR "poc.stubs.PocProcedures.syncPurchaseOrder";
