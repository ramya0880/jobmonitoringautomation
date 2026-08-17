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
 * (erpjobs-h2-poc/erp-stubs, never written into erpjobs/src). Fifth,
 * independent demo case (CODE FIX) - new table/procedure, does not touch
 * ERP_INVENTORY_SYNC_TEST or any other demo job/table. Same
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
@Description(value="POC DEMO-FAILURE job: intentionally calls a misspelled procedure name (SP_ERP_ORDR_CLOSE) to seed a code-fix demo failure.",
            urls= {"http://www.globusmedical.com"})

public class GmDynamicProcedureOrderCloseJob extends GmActionJob implements SchedulableJob{
    Logger log = GmLogger.getInstance(this.getClass().getName());

    // BUG (intentional, for the POC): should be "SP_ERP_ORDER_CLOSE" - missing
    // the "E" ("ORDR" instead of "ORDER") does not match the real H2 alias.
    private static final String PROCEDURE_NAME = "SP_ERP_ORDR_CLOSE";

    @Override
    public void execute(Context context) throws Exception {
        JobConfig jobConfig = context.getConfig();
        GmCommonClass gmCommonClass = new GmCommonClass();

        GmDataStoreVO gmDataStoreVO = gmCommonClass.getGmDataStoreVO(jobConfig);
        GmDynamicProcedureBean gmDynamicProcedureBean = new GmDynamicProcedureBean(gmDataStoreVO);
        gmDynamicProcedureBean.processDynamicProcedureNoParam(PROCEDURE_NAME);
    }

}
