package com.globus.common.beans;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.carfey.ops.job.config.JobConfig;
import com.carfey.ops.parameter.ParameterException;
import com.globus.valueobject.common.GmDataStoreVO;

/**
 * POC-only stand-in for the real com.globus.common.beans.GmCommonClass
 * (erpjobs/src/com/globus/common/beans/GmCommonClass.java - untouched on disk).
 *
 * DBConnectionWrapper, GmDBManager and GmDynamicProcedureNoParamJob only ever
 * call parseNull(), getExceptionStackTrace() and getGmDataStoreVO(); the real
 * file's many other methods (PDF/email/barcode/OCR) are unrelated to the job
 * execution path but force the whole file to compile together, and one of
 * those imports (org.apache.pdfbox) is not even declared in erpjobs/pom.xml.
 * All three method bodies below are copied verbatim from the real file
 * (lines 54-56, 592-606 and 77-104) so behavior is identical for the calls
 * this POC actually exercises.
 */
public class GmCommonClass {

    public static String parseNull(String string) {
        return (string == null || string.equals("")) ? "" : string.trim();
    }

    public GmDataStoreVO getGmDataStoreVO(JobConfig jobConfig) {
        GmDataStoreVO gmDataStoreVO = new GmDataStoreVO();
        String strCompId;
        String strPlantId;
        String strCompDateFmt;
        String strCompTimeZone;
        String strDBConnection;
        try {
            strCompId = GmCommonClass.parseNull((String) jobConfig.getString("companyId"));
            strCompDateFmt = GmCommonClass.parseNull((String) jobConfig.getString("compDateFmt"));
            strCompTimeZone = GmCommonClass.parseNull((String) jobConfig.getString("compTimeZone"));
            strPlantId = GmCommonClass.parseNull((String) jobConfig.getString("plantId"));
            strDBConnection = GmCommonClass.parseNull((String) jobConfig.getString("DBConnection"));
            gmDataStoreVO.setCmpid(strCompId);
            gmDataStoreVO.setPlantid(strPlantId);
            gmDataStoreVO.setCmptzone(strCompTimeZone);
            gmDataStoreVO.setCmpdfmt(strCompDateFmt);
            gmDataStoreVO.setDbconnection(strDBConnection);
        } catch (ParameterException e) {
            e.printStackTrace();
        }
        return gmDataStoreVO;
    }

    public static String getExceptionStackTrace(Exception e) {
        StringBuilder strErrorMsg = new StringBuilder();
        String strMsg = "";
        try {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            strErrorMsg.append(sw.toString());
            strMsg = strErrorMsg.toString();
        } catch (Exception ex) {
            strMsg = "Exception stack trace not found.";
        }
        return strMsg;
    }
}
