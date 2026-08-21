package com.globus.common.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.carfey.ops.job.Context;
import com.carfey.ops.job.SchedulableJob;
import com.carfey.ops.job.config.JobConfig;
import com.carfey.ops.job.param.Configuration;
import com.carfey.ops.job.param.Description;
import com.carfey.ops.job.param.Parameter;
import com.carfey.ops.job.param.Type;
import com.globus.common.beans.GmCommonClass;
import com.globus.common.beans.GmLogger;
import com.globus.common.beans.GmDynamicProcedureBean;
import com.globus.common.db.GmDBManager;
import com.globus.common.util.GmActionJob;
import com.globus.valueobject.common.GmDataStoreVO;

/**
 * POC-only job class - NOT part of the erpjobs source tree. Sixth demo case
 * (healthy - no deliberate bug), new table/procedure, does not touch any
 * other demo job/table.
 *
 * BUG (intentional, for the POC): an "exact fetch" pre-check was added before
 * the sync call - it expects CUSTOMER_CODE='CUST-400' to identify exactly one
 * row (mirrors a real Oracle single-row SELECT INTO), but if the H2 demo data
 * has been seeded with a duplicate CUST-400 row, the second rs.next() finds
 * it and this throws - a genuinely different failure shape (not a typo, not a
 * null field) than every other demo job in this repo.
 */
@Configuration(knownParameters={
        @Parameter(name="companyId", required=true, type=Type.STRING),
        @Parameter(name="plantId", required=true, type=Type.STRING),
        @Parameter(name="compDateFmt", required=true, type=Type.STRING),
        @Parameter(name="compTimeZone", required=true, type=Type.STRING),
        @Parameter(name="DBConnection", required=false, type=Type.STRING, listArgs={"Test","Stage","PreProd"})
    })
@Description(value="POC job: runs SP_ERP_CUSTOMER_SYNC (marks CUST-400 SYNCED) in ERP_CUSTOMER_SYNC_TEST.",
            urls= {"http://www.globusmedical.com"})

public class GmDynamicProcedureCustomerSyncJob extends GmActionJob implements SchedulableJob{
    Logger log = GmLogger.getInstance(this.getClass().getName());

    private static final String PROCEDURE_NAME = "SP_ERP_CUSTOMER_SYNC";

    @Override
    public void execute(Context context) throws Exception {
        JobConfig jobConfig = context.getConfig();
        GmCommonClass gmCommonClass = new GmCommonClass();

        GmDataStoreVO gmDataStoreVO = gmCommonClass.getGmDataStoreVO(jobConfig);

        GmDBManager gmDBManager = GmDBManager.getGmDBManager(gmDataStoreVO);
        Connection conn = gmDBManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT CUSTOMER_CODE FROM ERP_CUSTOMER_SYNC_TEST WHERE CUSTOMER_CODE = 'CUST-400'");
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            throw new Exception("No customer row found for CUST-400");
        }
        if (rs.next()) {
            throw new Exception("ORA-01422: exact fetch returns more than requested number of rows");
        }

        GmDynamicProcedureBean gmDynamicProcedureBean = new GmDynamicProcedureBean(gmDataStoreVO);
        gmDynamicProcedureBean.processDynamicProcedureNoParam(PROCEDURE_NAME);
    }

}
