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
 * DELIBERATE DEMO-FAILURE job for the Obsidian Job Monitoring Automation POC.
 * Not part of the erpjobs source tree - lives only in this isolated POC repo,
 * never in erpjobs/src. Same structural pattern as
 * GmDynamicProcedureStatusUpdateJob (real GmActionJob/GmDynamicProcedureBean/
 * GmDBManager underneath, untouched), with one intentional bug: the
 * procedure name below has a copy-paste typo (STAUS instead of STATUS) and
 * does not match any real H2 alias, so this job reliably fails with
 * "Function ... not found" - a realistic target for the RCA/fix pipeline to
 * diagnose and correct. Does not touch GmDynamicProcedureNoParamJob or
 * GmDynamicProcedureStatusUpdateJob, both of which keep working unmodified.
 */
@Configuration(knownParameters={
        @Parameter(name="companyId", required=true, type=Type.STRING),
        @Parameter(name="plantId", required=true, type=Type.STRING),
        @Parameter(name="compDateFmt", required=true, type=Type.STRING),
        @Parameter(name="compTimeZone", required=true, type=Type.STRING),
        @Parameter(name="DBConnection", required=false, type=Type.STRING, listArgs={"Test","Stage","PreProd"})
    })
@Description(value="POC DEMO-FAILURE job: intentionally calls a misspelled procedure name to seed a realistic failure for the RCA automation pipeline.",
            urls= {"http://www.globusmedical.com"})

public class GmDynamicProcedureBadConfigJob extends GmActionJob implements SchedulableJob{
    Logger log = GmLogger.getInstance(this.getClass().getName());

    // BUG (intentional, for the POC): should be "SP_ERP_JOB_STATUS_UPDATE" - missing the
    // "U" in "UPDATE" does not match the real H2 alias. Fresh variant for a genuinely new
    // failure signature - real Claude RCA test after fixing the markdown-fence JSON parse bug.
    private static final String PROCEDURE_NAME = "SP_ERP_JOB_STATUS_UPDATE";

    @Override
    public void execute(Context context) throws Exception {
        JobConfig jobConfig = context.getConfig();
        GmCommonClass gmCommonClass = new GmCommonClass();

        GmDataStoreVO gmDataStoreVO = gmCommonClass.getGmDataStoreVO(jobConfig);
        GmDynamicProcedureBean gmDynamicProcedureBean = new GmDynamicProcedureBean(gmDataStoreVO);
        gmDynamicProcedureBean.processDynamicProcedureNoParam(PROCEDURE_NAME);
    }

}
