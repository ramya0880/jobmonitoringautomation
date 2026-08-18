package com.globus.common.job;

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
import com.globus.common.util.GmActionJob;
import com.globus.valueobject.common.GmDataStoreVO;

/**
 * POC-only job class - NOT part of the erpjobs source tree. Ninth demo
 * case (CODE FIX via NullPointerException - a missing else branch, not a
 * spelling typo like the other code-fix demos), new table/procedure, does
 * not touch any other demo job/table.
 */
@Configuration(knownParameters={
        @Parameter(name="companyId", required=true, type=Type.STRING),
        @Parameter(name="plantId", required=true, type=Type.STRING),
        @Parameter(name="compDateFmt", required=true, type=Type.STRING),
        @Parameter(name="compTimeZone", required=true, type=Type.STRING),
        @Parameter(name="DBConnection", required=false, type=Type.STRING, listArgs={"Test","Stage","PreProd"})
    })
@Description(value="POC DEMO-FAILURE job: a batch-label lookup is missing its else branch, throwing NullPointerException before SP_ERP_PURCHASE_ORDER_SYNC (marks PO-700 SYNCED) ever runs.",
            urls= {"http://www.globusmedical.com"})

public class GmDynamicProcedurePurchaseOrderSyncJob extends GmActionJob implements SchedulableJob{
    Logger log = GmLogger.getInstance(this.getClass().getName());

    private static final String PROCEDURE_NAME = "SP_ERP_PURCHASE_ORDER_SYNC";

    @Override
    public void execute(Context context) throws Exception {
        JobConfig jobConfig = context.getConfig();
        GmCommonClass gmCommonClass = new GmCommonClass();

        GmDataStoreVO gmDataStoreVO = gmCommonClass.getGmDataStoreVO(jobConfig);
        GmDynamicProcedureBean gmDynamicProcedureBean = new GmDynamicProcedureBean(gmDataStoreVO);

        // BUG (intentional, for the POC): batchLabel is only assigned in the "HIGH" branch -
        // the else needed for the STANDARD/default priority (the only value this job ever
        // uses) was never added, so batchLabel stays null and the log line below throws
        // NullPointerException before the procedure call is ever reached.
        String priorityCode = "STANDARD";
        String batchLabel = null;
        if ("HIGH".equals(priorityCode)) {
            batchLabel = "PO-PRIORITY-BATCH";
        } else {
            batchLabel = "PO-STANDARD-BATCH";
        }
        log.info("Processing purchase order sync, batch=" + batchLabel.toUpperCase());

        gmDynamicProcedureBean.processDynamicProcedureNoParam(PROCEDURE_NAME);
    }

}
