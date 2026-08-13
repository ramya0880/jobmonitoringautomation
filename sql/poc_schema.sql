-- Minimum H2 schema for the GmDynamicProcedureNoParamJob smoke test.
-- This is a from-scratch POC table/procedure, not a real Globus object.
CREATE TABLE IF NOT EXISTS T_POC_SMOKE_LOG (
  RUN_ID IDENTITY PRIMARY KEY,
  RUN_TS TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE ALIAS IF NOT EXISTS SP_POC_NOOP FOR "poc.stubs.PocProcedures.noOp";
