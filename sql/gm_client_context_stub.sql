-- Stub for gm_pkg_cor_client_context.gm_sav_client_context - a real Oracle
-- package procedure that only exists in the real ERP schema, not in this
-- repo or in H2. DBConnectionWrapper.loadAppContext() (real, unmodified)
-- calls it whenever compTimeZone is non-empty; Obsidian's own "required"
-- validation on that job parameter won't allow it to be blank, so this
-- call always happens in practice. H2 has no such package, so it's stubbed
-- here as a true no-op with the same 6 string parameters DBConnectionWrapper
-- passes.
CREATE SCHEMA IF NOT EXISTS GM_PKG_COR_CLIENT_CONTEXT;
CREATE ALIAS IF NOT EXISTS GM_PKG_COR_CLIENT_CONTEXT.GM_SAV_CLIENT_CONTEXT
  FOR "poc.stubs.PocProcedures.noOpClientContext";
