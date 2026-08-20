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
 * DEMO job for the Obsidian Job Monitoring Automation RCA/fix pipeline -
 * wrong-parameter scenario. Not part of the erpjobs source tree, lives only
 * in this isolated POC repo. Calls the real no-op H2 procedure (SP_POC_NOOP)
 * so the only failure is the deliberate Java-side parameter defect below.
 */
@Configuration(knownParameters={
        @Parameter(name="companyId", required=true, type=Type.STRING),
        @Parameter(name="plantId", required=true, type=Type.STRING),
        @Parameter(name="compDateFmt", required=true, type=Type.STRING),
        @Parameter(name="compTimeZone", required=true, type=Type.STRING),
        @Parameter(name="DBConnection", required=false, type=Type.STRING, listArgs={"Test","Stage","PreProd"})
    })
@Description(value="DEMO job: intentionally passes a non-positive adjustment percentage - seeds a CODE_CHANGE_REQUIRED (wrong parameter) scenario for the RCA automation pipeline.",
            urls= {"http://www.globusmedical.com"})

public class GmDynamicProcedureCreditMemoSyncJob extends GmActionJob implements SchedulableJob{
    Logger log = GmLogger.getInstance(this.getClass().getName());

    private static final String PROCEDURE_NAME = "SP_POC_NOOP";

    // BUG (intentional, for the demo): ADJUSTMENT_PERCENT is 0, but the validation below
    // requires a strictly positive adjustment percentage - throws IllegalArgumentException
    // before the procedure is ever invoked.
    private static final int ADJUSTMENT_PERCENT = 5;

    @Override
    public void execute(Context context) throws Exception {
        JobConfig jobConfig = context.getConfig();
        GmCommonClass gmCommonClass = new GmCommonClass();

        if (ADJUSTMENT_PERCENT <= 0) {
            throw new IllegalArgumentException("Adjustment percent must be positive: " + ADJUSTMENT_PERCENT);
        }
        log.info("Credit memo adjustment percent: " + ADJUSTMENT_PERCENT);

        GmDataStoreVO gmDataStoreVO = gmCommonClass.getGmDataStoreVO(jobConfig);
        GmDynamicProcedureBean gmDynamicProcedureBean = new GmDynamicProcedureBean(gmDataStoreVO);
        gmDynamicProcedureBean.processDynamicProcedureNoParam(PROCEDURE_NAME);
    }

}
