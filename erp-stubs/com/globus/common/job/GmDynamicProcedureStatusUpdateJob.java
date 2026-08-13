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
 * POC-only job class - NOT part of the erpjobs source tree
 * (erpjobs-h2-poc/erp-stubs, never written into erpjobs/src). A genuinely
 * separate job from com.globus.common.job.GmDynamicProcedureNoParamJob
 * (real, unmodified, untouched by this class), dedicated to one fixed
 * procedure rather than taking a procedureName parameter. Same
 * lifecycle/interface/base-class pattern as the real Globus job classes:
 * extends GmActionJob, implements SchedulableJob, same
 * GmCommonClass -> GmDataStoreVO -> GmDynamicProcedureBean call chain
 * (all real, unmodified erpjobs classes) as GmDynamicProcedureNoParamJob
 * uses.
 */
@Configuration(knownParameters={
        @Parameter(name="companyId", required=true, type=Type.STRING),
        @Parameter(name="plantId", required=true, type=Type.STRING),
        @Parameter(name="compDateFmt", required=true, type=Type.STRING),
        @Parameter(name="compTimeZone", required=true, type=Type.STRING),
        @Parameter(name="DBConnection", required=false, type=Type.STRING, listArgs={"Test","Stage","PreProd"})
    })
@Description(value="POC job: runs SP_ERP_JOB_STATUS_UPDATE (TYPE=4110 rows -> STATUS=5) in ERP_JOB_STATUS_TEST.",
            urls= {"http://www.globusmedical.com"})

public class GmDynamicProcedureStatusUpdateJob extends GmActionJob implements SchedulableJob{
    Logger log = GmLogger.getInstance(this.getClass().getName());

    // BUG (intentional, for the POC): should be "SP_ERP_JOB_STATUS_UPDATE" -
    // missing trailing "E" does not match the real H2 alias.
    private static final String PROCEDURE_NAME = "SP_ERP_JOB_STATUS_UPDAT";

    @Override
    public void execute(Context context) throws Exception {
        JobConfig jobConfig = context.getConfig();
        GmCommonClass gmCommonClass = new GmCommonClass();

        GmDataStoreVO gmDataStoreVO = gmCommonClass.getGmDataStoreVO(jobConfig);
        GmDynamicProcedureBean gmDynamicProcedureBean = new GmDynamicProcedureBean(gmDataStoreVO);
        gmDynamicProcedureBean.processDynamicProcedureNoParam(PROCEDURE_NAME);
    }

}
