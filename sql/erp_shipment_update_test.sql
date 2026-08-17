-- Seventh demo test case (healthy/completes-successfully by design - no deliberate bug).
CREATE TABLE IF NOT EXISTS ERP_SHIPMENT_UPDATE_TEST (
  ID            IDENTITY PRIMARY KEY,
  SHIPMENT_ID   VARCHAR(50),
  STATUS        VARCHAR(20),
  CREATED_DATE  TIMESTAMP,
  MESSAGE       VARCHAR(500)
);

INSERT INTO ERP_SHIPMENT_UPDATE_TEST (SHIPMENT_ID, STATUS, CREATED_DATE, MESSAGE)
  VALUES ('SHIP-500', 'PENDING', CURRENT_TIMESTAMP, 'Seed row for shipment update demo');

CREATE ALIAS IF NOT EXISTS SP_ERP_SHIPMENT_UPDATE FOR "poc.stubs.PocProcedures.updateShipment";
