-- Eighth demo test case (healthy/completes-successfully by design - no deliberate bug).
CREATE TABLE IF NOT EXISTS ERP_INVOICE_CLOSE_TEST (
  ID            IDENTITY PRIMARY KEY,
  INVOICE_ID    VARCHAR(50),
  STATUS        VARCHAR(20),
  CLOSED_DATE   TIMESTAMP,
  MESSAGE       VARCHAR(500)
);

INSERT INTO ERP_INVOICE_CLOSE_TEST (INVOICE_ID, STATUS, CLOSED_DATE, MESSAGE)
  VALUES ('INV-600', 'OPEN', NULL, 'Seed row for invoice close demo');

CREATE ALIAS IF NOT EXISTS SP_ERP_INVOICE_CLOSE FOR "poc.stubs.PocProcedures.closeInvoice";
