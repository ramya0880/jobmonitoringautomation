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
 * second, independent CODE_CHANGE_REQUIRED scenario (the first, in
 * GmDynamicProcedureCodeFixDemoJob, is now fixed and succeeds). Not part of
 * the erpjobs source tree, lives only in this isolated POC repo.
 * Intentional bug: PROCEDURE_NAME below is a one-character typo (missing
 * one "U") of the real, registered H2 alias SP_ERP_JOB_TEST_RUN (see
 * erpjobs-h2-poc/sql/erp_job_test.sql).
 */
@Configuration(knownParameters={
        @Parameter(name="companyId", required=true, type=Type.STRING),
        @Parameter(name="plantId", required=true, type=Type.STRING),
        @Parameter(name="compDateFmt", required=true, type=Type.STRING),
        @Parameter(name="compTimeZone", required=true, type=Type.STRING),
        @Parameter(name="DBConnection", required=false, type=Type.STRING, listArgs={"Test","Stage","PreProd"})
    })
@Description(value="DEMO job: intentionally calls a misspelled procedure name (SP_ERP_JOB_TEST_RN instead of SP_ERP_JOB_TEST_RUN) - seeds a CODE_CHANGE_REQUIRED scenario for the RCA automation pipeline.",
            urls= {"http://www.globusmedical.com"})

public class GmDynamicProcedureCodeFixDemo2Job extends GmActionJob implements SchedulableJob{
    Logger log = GmLogger.getInstance(this.getClass().getName());

    // BUG (intentional, for the demo): should be "SP_ERP_JOB_TEST_RUN" -
    // missing one "U" does not match the real H2 alias.
    private static final String PROCEDURE_NAME = "SP_ERP_JOB_TEST_RN";

    @Override
    public void execute(Context context) throws Exception {
        JobConfig jobConfig = context.getConfig();
        GmCommonClass gmCommonClass = new GmCommonClass();

        GmDataStoreVO gmDataStoreVO = gmCommonClass.getGmDataStoreVO(jobConfig);
        GmDynamicProcedureBean gmDynamicProcedureBean = new GmDynamicProcedureBean(gmDataStoreVO);
        gmDynamicProcedureBean.processDynamicProcedureNoParam(PROCEDURE_NAME);
    }

}
