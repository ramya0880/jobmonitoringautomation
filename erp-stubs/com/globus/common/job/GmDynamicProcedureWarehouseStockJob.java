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
 * POC-only job class - NOT part of the erpjobs source tree. Tenth demo
 * case (healthy - no deliberate bug), new table/procedure, does not touch
 * any other demo job/table.
 */
@Configuration(knownParameters={
        @Parameter(name="companyId", required=true, type=Type.STRING),
        @Parameter(name="plantId", required=true, type=Type.STRING),
        @Parameter(name="compDateFmt", required=true, type=Type.STRING),
        @Parameter(name="compTimeZone", required=true, type=Type.STRING),
        @Parameter(name="DBConnection", required=false, type=Type.STRING, listArgs={"Test","Stage","PreProd"})
    })
@Description(value="POC job: runs SP_ERP_WAREHOUSE_STOCK_UPDATE (marks ITEM-800 UPDATED) in ERP_WAREHOUSE_STOCK_TEST.",
            urls= {"http://www.globusmedical.com"})

public class GmDynamicProcedureWarehouseStockJob extends GmActionJob implements SchedulableJob{
    Logger log = GmLogger.getInstance(this.getClass().getName());

    // BUG (intentional, for the POC): should be "SP_ERP_WAREHOUSE_STOCK_UPDATE" -
    // missing "H" ("WAREHOUSE" -> "WAREOUSE") does not match the real H2 alias.
    // Fresh variant for a genuinely new failure signature - real Claude RCA connectivity test.
    private static final String PROCEDURE_NAME = "SP_ERP_WAREHOUSE_STOCK_UPDATE";

    @Override
    public void execute(Context context) throws Exception {
        JobConfig jobConfig = context.getConfig();
        GmCommonClass gmCommonClass = new GmCommonClass();

        GmDataStoreVO gmDataStoreVO = gmCommonClass.getGmDataStoreVO(jobConfig);
        GmDynamicProcedureBean gmDynamicProcedureBean = new GmDynamicProcedureBean(gmDataStoreVO);
        gmDynamicProcedureBean.processDynamicProcedureNoParam(PROCEDURE_NAME);
    }

}
