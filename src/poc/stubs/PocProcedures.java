package poc.stubs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * H2-side body for SP_POC_NOOP, the zero-argument procedure the smoke test
 * passes to GmDynamicProcedureBean.processDynamicProcedureNoParam(). H2
 * auto-injects the current session Connection as the first parameter for
 * a Java alias, so this becomes the target of GmDBManager's
 * "call SP_POC_NOOP()" CallableStatement.
 */
public class PocProcedures {
    public static void noOp(Connection conn) throws SQLException {
        conn.createStatement().execute("INSERT INTO T_POC_SMOKE_LOG DEFAULT VALUES");
    }

    /**
     * H2-side body for SP_ERP_JOB_TEST_RUN. Inserts exactly one fixed row
     * into ERP_JOB_TEST - no parameters, no business logic.
     */
    public static void insertErpJobTestRow(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO ERP_JOB_TEST (JOB_NAME, STATUS, CREATED_DATE, MESSAGE) "
              + "VALUES (?, ?, CURRENT_TIMESTAMP, ?)");
        ps.setString(1, "ERP_JOB_TEST");
        ps.setString(2, "SUCCESS");
        ps.setString(3, "ERP Job executed successfully");
        ps.execute();
    }

    /**
     * H2-side stand-in for the real Oracle package procedure
     * gm_pkg_cor_client_context.gm_sav_client_context, which only exists in
     * the real ERP schema (not in this repo, not in H2). Called by
     * DBConnectionWrapper.loadAppContext() (real, unmodified) whenever
     * compTimeZone is non-empty. Six matching String params, same order
     * DBConnectionWrapper.java sets them (company/timezone/dateFmt/plant/
     * party/lang) - genuinely does nothing with them.
     */
    public static void noOpClientContext(String companyId, String timeZone, String compDateFmt,
            String plantId, String partyId, String compLangId) {
        // no-op - real Oracle session-context package, not present in H2
    }

    /**
     * H2-side body for SP_ERP_JOB_STATUS_UPDATE - the second, independent
     * test case. Conditional UPDATE: every ERP_JOB_STATUS_TEST row with
     * TYPE=4110 gets STATUS=5; every other TYPE is left untouched. Does not
     * touch ERP_JOB_TEST or T_POC_SMOKE_LOG.
     */
    public static void updateErpJobStatus(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE ERP_JOB_STATUS_TEST SET STATUS = 5 WHERE TYPE = 4110");
        ps.execute();
    }
}
