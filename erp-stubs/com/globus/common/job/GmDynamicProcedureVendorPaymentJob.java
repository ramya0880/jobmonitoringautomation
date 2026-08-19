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
 * (erpjobs-h2-poc/erp-stubs, never written into erpjobs/src). Fourth,
 * independent demo case (DATA FIX) - new table/procedure, does not touch
 * ERP_JOB_TEST/ERP_JOB_STATUS_TEST or their jobs. The procedure name below
 * is CORRECT (matches the real H2 alias) - to seed a data-fix demo failure,
 * rename the ERP_VENDOR_PAYMENT_TEST.PAID_STATUS column live (schema drift),
 * the same technique already used on ERP_JOB_STATUS_TEST.STATUS. Same
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
@Description(value="POC job: runs SP_ERP_VENDOR_PAYMENT_UPDATE (marks VENDOR-200 PAID) in ERP_VENDOR_PAYMENT_TEST.",
            urls= {"http://www.globusmedical.com"})

public class GmDynamicProcedureVendorPaymentJob extends GmActionJob implements SchedulableJob{
    Logger log = GmLogger.getInstance(this.getClass().getName());

    private static final String PROCEDURE_NAME = "SP_ERP_VENDOR_PAYMENT_UPDATE";

    // POC demo bug (NullPointerException variant): left null instead of a real default.
    private static final String PAYMENT_BATCH_ID = null;

    @Override
    public void execute(Context context) throws Exception {
        JobConfig jobConfig = context.getConfig();
        GmCommonClass gmCommonClass = new GmCommonClass();

        log.info("Payment batch: " + PAYMENT_BATCH_ID.trim());

        GmDataStoreVO gmDataStoreVO = gmCommonClass.getGmDataStoreVO(jobConfig);
        GmDynamicProcedureBean gmDynamicProcedureBean = new GmDynamicProcedureBean(gmDataStoreVO);
        gmDynamicProcedureBean.processDynamicProcedureNoParam(PROCEDURE_NAME);
    }

}
