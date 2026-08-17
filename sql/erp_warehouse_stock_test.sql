-- Tenth demo test case (healthy/completes-successfully by design - no deliberate bug).
CREATE TABLE IF NOT EXISTS ERP_WAREHOUSE_STOCK_TEST (
  ID            IDENTITY PRIMARY KEY,
  ITEM_CODE     VARCHAR(50),
  QTY           INTEGER,
  STATUS        VARCHAR(20),
  CREATED_DATE  TIMESTAMP,
  MESSAGE       VARCHAR(500)
);

INSERT INTO ERP_WAREHOUSE_STOCK_TEST (ITEM_CODE, QTY, STATUS, CREATED_DATE, MESSAGE)
  VALUES ('ITEM-800', 50, 'PENDING', CURRENT_TIMESTAMP, 'Seed row for warehouse stock demo');

CREATE ALIAS IF NOT EXISTS SP_ERP_WAREHOUSE_STOCK_UPDATE FOR "poc.stubs.PocProcedures.updateWarehouseStock";
