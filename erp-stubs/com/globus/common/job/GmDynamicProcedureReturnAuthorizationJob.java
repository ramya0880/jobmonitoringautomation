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
 * type-mismatch scenario. Not part of the erpjobs source tree, lives only
 * in this isolated POC repo. Calls the real no-op H2 procedure (SP_POC_NOOP)
 * so the only failure is the deliberate Java-side type defect below.
 */
@Configuration(knownParameters={
        @Parameter(name="companyId", required=true, type=Type.STRING),
        @Parameter(name="plantId", required=true, type=Type.STRING),
        @Parameter(name="compDateFmt", required=true, type=Type.STRING),
        @Parameter(name="compTimeZone", required=true, type=Type.STRING),
        @Parameter(name="DBConnection", required=false, type=Type.STRING, listArgs={"Test","Stage","PreProd"})
    })
@Description(value="DEMO job: intentionally casts a String-valued constant to Integer - seeds a CODE_CHANGE_REQUIRED (type mismatch) scenario for the RCA automation pipeline.",
            urls= {"http://www.globusmedical.com"})

public class GmDynamicProcedureReturnAuthorizationJob extends GmActionJob implements SchedulableJob{
    Logger log = GmLogger.getInstance(this.getClass().getName());

    private static final String PROCEDURE_NAME = "SP_POC_NOOP";

    // BUG (intentional, for the demo): RMA_STATUS_CODE actually holds a String value
    // at runtime, but is cast below as if it were an Integer - throws ClassCastException
    // before the procedure is ever invoked.
    private static final Object RMA_STATUS_CODE = "ACTIVE";

    @Override
    public void execute(Context context) throws Exception {
        JobConfig jobConfig = context.getConfig();
        GmCommonClass gmCommonClass = new GmCommonClass();

        Integer statusCode = (Integer) RMA_STATUS_CODE;
        log.info("Return authorization status code: " + statusCode);

        GmDataStoreVO gmDataStoreVO = gmCommonClass.getGmDataStoreVO(jobConfig);
        GmDynamicProcedureBean gmDynamicProcedureBean = new GmDynamicProcedureBean(gmDataStoreVO);
        gmDynamicProcedureBean.processDynamicProcedureNoParam(PROCEDURE_NAME);
    }

}
