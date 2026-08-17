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

    /**
     * H2-side body for SP_ERP_INVENTORY_SYNC - third, independent demo case
     * (CODE FIX: the job's hardcoded procedure name is deliberately typo'd,
     * this alias/method itself is unaffected). Marks the seeded demo row
     * SYNCED. Does not touch ERP_JOB_TEST/ERP_JOB_STATUS_TEST/T_POC_SMOKE_LOG.
     */
    public static void syncInventory(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE ERP_INVENTORY_SYNC_TEST SET STATUS = 'SYNCED' WHERE ITEM_CODE = 'DEMO-ITEM-100'");
        ps.execute();
    }

    /**
     * H2-side body for SP_ERP_VENDOR_PAYMENT_UPDATE - fourth, independent
     * demo case (DATA FIX: the job's hardcoded procedure name is correct;
     * the demo instead renames the PAID_STATUS column live to simulate
     * schema drift, same technique as ERP_JOB_STATUS_TEST.STATUS). Marks the
     * seeded demo row PAID.
     */
    public static void updateVendorPayment(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE ERP_VENDOR_PAYMENT_TEST SET PAID_STATUS = 'PAID' WHERE VENDOR_ID = 'VENDOR-200'");
        ps.execute();
    }

    /**
     * H2-side body for SP_ERP_ORDER_CLOSE - fifth, independent demo case
     * (CODE FIX: the job's hardcoded procedure name is deliberately typo'd,
     * this alias/method itself is unaffected). Marks the seeded demo order
     * CLOSED.
     */
    public static void closeOrder(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE ERP_ORDER_CLOSE_TEST SET STATUS = 'CLOSED', CLOSED_DATE = CURRENT_TIMESTAMP "
              + "WHERE ORDER_ID = 'ORDER-300'");
        ps.execute();
    }

    /**
     * H2-side body for SP_ERP_CUSTOMER_SYNC - sixth demo case (healthy - no
     * deliberate bug). Marks the seeded demo customer SYNCED.
     */
    public static void syncCustomer(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE ERP_CUSTOMER_SYNC_TEST SET STATUS = 'SYNCED' WHERE CUSTOMER_CODE = 'CUST-400'");
        ps.execute();
    }

    /**
     * H2-side body for SP_ERP_SHIPMENT_UPDATE - seventh demo case (healthy -
     * no deliberate bug). Marks the seeded demo shipment SHIPPED.
     */
    public static void updateShipment(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE ERP_SHIPMENT_UPDATE_TEST SET STATUS = 'SHIPPED' WHERE SHIPMENT_ID = 'SHIP-500'");
        ps.execute();
    }

    /**
     * H2-side body for SP_ERP_INVOICE_CLOSE - eighth demo case (healthy - no
     * deliberate bug). Marks the seeded demo invoice CLOSED.
     */
    public static void closeInvoice(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE ERP_INVOICE_CLOSE_TEST SET STATUS = 'CLOSED', CLOSED_DATE = CURRENT_TIMESTAMP "
              + "WHERE INVOICE_ID = 'INV-600'");
        ps.execute();
    }

    /**
     * H2-side body for SP_ERP_PURCHASE_ORDER_SYNC - ninth demo case (healthy
     * - no deliberate bug). Marks the seeded demo PO SYNCED.
     */
    public static void syncPurchaseOrder(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE ERP_PURCHASE_ORDER_SYNC_TEST SET STATUS = 'SYNCED' WHERE PO_NUMBER = 'PO-700'");
        ps.execute();
    }

    /**
     * H2-side body for SP_ERP_WAREHOUSE_STOCK_UPDATE - tenth demo case
     * (healthy - no deliberate bug). Marks the seeded demo item UPDATED.
     */
    public static void updateWarehouseStock(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE ERP_WAREHOUSE_STOCK_TEST SET STATUS = 'UPDATED' WHERE ITEM_CODE = 'ITEM-800'");
        ps.execute();
    }

    /**
     * H2-side body for SP_ERP_JOB_PRIORITY_UPDATE - second, independent DATA
     * FIX demo case. References the correct column name (PRIORITY); the live
     * schema currently has PRIORITY_TMP instead (see sql/erp_job_priority_test.sql),
     * so this fails with "Column PRIORITY not found" until the data fix renames
     * it back - the same "half-finished rename" pattern as updateErpJobStatus.
     */
    public static void updateErpJobPriority(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE ERP_JOB_PRIORITY_TEST SET PRIORITY = 1 WHERE TYPE = 7200");
        ps.execute();
    }
}
