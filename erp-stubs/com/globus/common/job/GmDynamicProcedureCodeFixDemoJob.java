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
 * dedicated CODE_CHANGE_REQUIRED scenario, kept separate from
 * GmDynamicProcedureBadConfigJob so the two can run side by side in a demo.
 * Not part of the erpjobs source tree, lives only in this isolated POC repo.
 * Intentional bug: PROCEDURE_NAME below is a one-character typo (missing
 * one "O") of the real, registered H2 alias SP_POC_NOOP (see
 * erpjobs-h2-poc/sql/poc_schema.sql) - a copy-paste/typo defect in source,
 * not a data or configuration issue, which is exactly what should classify
 * as CODE_CHANGE_REQUIRED.
 */
@Configuration(knownParameters={
        @Parameter(name="companyId", required=true, type=Type.STRING),
        @Parameter(name="plantId", required=true, type=Type.STRING),
        @Parameter(name="compDateFmt", required=true, type=Type.STRING),
        @Parameter(name="compTimeZone", required=true, type=Type.STRING),
        @Parameter(name="DBConnection", required=false, type=Type.STRING, listArgs={"Test","Stage","PreProd"})
    })
@Description(value="DEMO job: intentionally calls a misspelled procedure name (SP_POC_NOP instead of SP_POC_NOOP) - seeds a CODE_CHANGE_REQUIRED scenario for the RCA automation pipeline.",
            urls= {"http://www.globusmedical.com"})

public class GmDynamicProcedureCodeFixDemoJob extends GmActionJob implements SchedulableJob{
    Logger log = GmLogger.getInstance(this.getClass().getName());

    // BUG (intentional, for the demo): should be "SP_POC_NOOP" - missing one
    // "O" does not match the real H2 alias.
    private static final String PROCEDURE_NAME = "SP_POC_NOP";

    @Override
    public void execute(Context context) throws Exception {
        JobConfig jobConfig = context.getConfig();
        GmCommonClass gmCommonClass = new GmCommonClass();

        GmDataStoreVO gmDataStoreVO = gmCommonClass.getGmDataStoreVO(jobConfig);
        GmDynamicProcedureBean gmDynamicProcedureBean = new GmDynamicProcedureBean(gmDataStoreVO);
        gmDynamicProcedureBean.processDynamicProcedureNoParam(PROCEDURE_NAME);
    }

}
