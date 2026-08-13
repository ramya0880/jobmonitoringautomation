package poc;

import java.nio.charset.Charset;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;

import poc.jndi.PocContext;

import com.globus.common.beans.GmDynamicProcedureBean;
import com.globus.valueobject.common.GmDataStoreVO;

/**
 * Smoke test for Step 4/5 of the ERPJobs Automation Blueprint: prove that
 * GmDBManager/DBConnectionWrapper can resolve "jdbc/globus_test" via JNDI
 * and successfully call a stored procedure against H2.
 *
 * Exercises the exact same code GmDynamicProcedureNoParamJob.execute() calls
 * after unwrapping its Obsidian job parameters - see
 * GmDynamicProcedureBean.processDynamicProcedureNoParam().
 */
public class PocSmokeTestRunner {

    private static final String H2_URL =
            "jdbc:h2:file:C:/Workspace/erpjobs-h2-poc/data/erpjobs_poc;DB_CLOSE_ON_EXIT=FALSE";
    private static final String JNDI_NAME = "jdbc/globus_test";
    private static final String SCHEMA_FILE = "C:/Workspace/erpjobs-h2-poc/sql/poc_schema.sql";
    private static final String PROCEDURE_NAME = "SP_POC_NOOP";

    public static void main(String[] args) throws Exception {
        System.out.println("[1/3] Loading POC schema into H2: " + H2_URL);
        RunScript.execute(H2_URL, "sa", "", SCHEMA_FILE, Charset.forName("UTF-8"), false);
        System.out.println("      Schema loaded OK.");

        System.out.println("[2/3] Registering JNDI binding '" + JNDI_NAME + "' -> H2 DataSource");
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(H2_URL);
        ds.setUser("sa");
        ds.setPassword("");
        PocContext.register(JNDI_NAME, ds);
        System.out.println("      Bound OK.");

        System.out.println("[3/3] Calling GmDynamicProcedureBean.processDynamicProcedureNoParam(\""
                + PROCEDURE_NAME + "\") via GmDBManager/DBConnectionWrapper");
        try {
            GmDataStoreVO vo = new GmDataStoreVO();
            vo.setDbconnection("Test"); // -> GmDNSNamesEnum.WEBGLOBUSTEST -> "jdbc/globus_test"
            // compTimeZone intentionally left at its default "" so
            // DBConnectionWrapper.getConnection() skips loadAppContext()
            // (the gm_sav_client_context call) entirely - see blueprint Step 5 notes.

            GmDynamicProcedureBean bean = new GmDynamicProcedureBean(vo);
            bean.processDynamicProcedureNoParam(PROCEDURE_NAME);

            System.out.println();
            System.out.println("RESULT: PASS - GmDBManager executed and committed against H2 with no exception.");
        } catch (Exception e) {
            System.out.println();
            System.out.println("RESULT: FAIL - exception propagated exactly as it would inside GmActionJob:");
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
