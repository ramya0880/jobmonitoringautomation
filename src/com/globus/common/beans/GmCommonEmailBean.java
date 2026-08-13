package com.globus.common.beans;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * POC-only stand-in for the real com.globus.common.beans.GmCommonEmailBean
 * (erpjobs/src/com/globus/common/beans/GmCommonEmailBean.java - untouched on
 * disk). GmActionJob.sendJobExceptionEmail() calls
 * sendEmailFromTemplate(String, ArrayList, HashMap) - the only method this
 * POC's dependency chain needs. The real file extends GmCommonClass and
 * pulls in resource-bundle/SMTP logic that isn't needed to compile or run
 * GmDynamicProcedureNoParamJob's actual execute() path (email is only sent
 * on failure), so this no-op stand-in avoids that unrelated surface. Same
 * technique already used for GmCommonClass, for the same reason.
 */
public class GmCommonEmailBean {
    public static void sendEmailFromTemplate(String templateId, ArrayList alBody, HashMap hmAdditionalParams) throws Exception {
        // no-op for this POC - email-on-failure path is not exercised
    }
}
