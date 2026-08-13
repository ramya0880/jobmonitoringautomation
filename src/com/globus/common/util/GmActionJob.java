/**
 * FileName : GmActionJob.java Description : Author : sthadeshwar Date : Mar 24, 2009 Copyright :
 * Globus Medical Inc
 */
package com.globus.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import com.carfey.ops.job.Context;
import com.globus.common.beans.GmCommonEmailBean;
import com.globus.common.beans.GmLogger;


/**
 * @author sthadeshwar
 * 
 */
public abstract class GmActionJob {

  Logger log = GmLogger.getInstance(this.getClass().getName());

  public static final String JOB_EXCEPTION_MAIL_TEMPLATE = "GmJobExceptionMail";

  private String jobName;
  private String jobStartTime;
  private String jobEndTime;
  private String jobStatus;
  private String jobException;
  private String additionalParam;
  private String companyId;

  public abstract void execute(Context context) throws Exception;
  
  /**
   * @return the jobName
   */
  public String getJobName() {
    return jobName;
  }

  /**
   * @param jobName the jobName to set
   */
  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  /**
   * @return the jobStartTime
   */
  public String getJobStartTime() {
    return jobStartTime;
  }

  /**
   * @param jobStartTime the jobStartTime to set
   */
  public void setJobStartTime(String jobStartTime) {
    this.jobStartTime = jobStartTime;
  }

  /**
   * @return the jobEndTime
   */
  public String getJobEndTime() {
    return jobEndTime;
  }

  /**
   * @param jobEndTime the jobEndTime to set
   */
  public void setJobEndTime(String jobEndTime) {
    this.jobEndTime = jobEndTime;
  }

  /**
   * @return the jobStatus
   */
  public String getJobStatus() {
    return jobStatus;
  }

  /**
   * @param jobStatus the jobStatus to set
   */
  public void setJobStatus(String jobStatus) {
    this.jobStatus = jobStatus;
  }

  /**
   * @return the jobException
   */
  public String getJobException() {
    return jobException;
  }

  /**
   * @param jobException the jobException to set
   */
  public void setJobException(String jobException) {
    this.jobException = jobException;
  }

  /**
   * @return the additionalParam
   */
  public String getAdditionalParam() {
    return additionalParam;
  }

  /**
   * @param additionalParam the additionalParam to set
   */
  public void setAdditionalParam(String additionalParam) {
    this.additionalParam = additionalParam;
  }

  /**
   * @return the companyId
   */
  public String getCompanyId() {
    return companyId;
  }

  /**
   * @param companyId the companyId to set
   */
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void sendJobExceptionEmail() {
		try {
			ArrayList alMailParams = new ArrayList();
			HashMap hmMailParams = new HashMap();
			hmMailParams.put(GmJob.JOBNAME, jobName);
			hmMailParams.put(GmJob.STARTTIME, jobStartTime);
			hmMailParams.put(GmJob.ENDTIME, jobEndTime);
			hmMailParams.put(GmJob.STATUS, jobStatus);
			hmMailParams.put(GmJob.EXCEPTION, jobException);
			hmMailParams.put(GmJob.ADDITIONALPARAM, additionalParam);
			hmMailParams.put("COMPANY_ID", companyId);
			alMailParams.add(hmMailParams);
			GmCommonEmailBean.sendEmailFromTemplate(JOB_EXCEPTION_MAIL_TEMPLATE,alMailParams, hmMailParams);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
}
