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
 * second, independent DATA_FIX_REQUIRED scenario (the first, in
 * GmDynamicProcedureDataFixDemoJob / GmDynamicProcedureStatusUpdateJob, is
 * now fixed and succeeds). Not part of the erpjobs source tree, lives only
 * in this isolated POC repo.
 * Calls the real, correctly-spelled H2 alias SP_ERP_JOB_PRIORITY_UPDATE -
 * the procedure name itself is not the bug. The underlying
 * ERP_JOB_PRIORITY_TEST table's PRIORITY column is currently named
 * PRIORITY_TMP in the live H2 schema (see erpjobs-h2-poc/sql/
 * erp_job_priority_test.sql), so the UPDATE inside the procedure fails with
 * "Column PRIORITY not found" - a schema/data-state mismatch, not a code
 * defect, which is exactly what should classify as DATA_FIX_REQUIRED.
 */
@Configuration(knownParameters={
        @Parameter(name="companyId", required=true, type=Type.STRING),
        @Parameter(name="plantId", required=true, type=Type.STRING),
        @Parameter(name="compDateFmt", required=true, type=Type.STRING),
        @Parameter(name="compTimeZone", required=true, type=Type.STRING),
        @Parameter(name="DBConnection", required=false, type=Type.STRING, listArgs={"Test","Stage","PreProd"})
    })
@Description(value="DEMO job: runs SP_ERP_JOB_PRIORITY_UPDATE against ERP_JOB_PRIORITY_TEST, which currently has a schema mismatch (PRIORITY_TMP instead of PRIORITY) - seeds a DATA_FIX_REQUIRED scenario for the RCA automation pipeline.",
            urls= {"http://www.globusmedical.com"})

public class GmDynamicProcedureDataFixDemo2Job extends GmActionJob implements SchedulableJob{
    Logger log = GmLogger.getInstance(this.getClass().getName());

    private static final String PROCEDURE_NAME = "SP_ERP_JOB_PRIORITY_UPDATE";

    @Override
    public void execute(Context context) throws Exception {
        JobConfig jobConfig = context.getConfig();
        GmCommonClass gmCommonClass = new GmCommonClass();

        GmDataStoreVO gmDataStoreVO = gmCommonClass.getGmDataStoreVO(jobConfig);
        GmDynamicProcedureBean gmDynamicProcedureBean = new GmDynamicProcedureBean(gmDataStoreVO);
        gmDynamicProcedureBean.processDynamicProcedureNoParam(PROCEDURE_NAME);
    }

}
