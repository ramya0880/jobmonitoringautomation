/**
 * FileName    : GmJob.java 
 * Description :
 * Author      : vprasath
 * Date        : Feb 26, 2009 
 * Copyright   : Globus Medical Inc
 */
package com.globus.common.util;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.globus.common.beans.GmLogger;

/**
 * @author vprasath
 *
 */
public abstract class GmJob {
	
	public static final String JOBNAME = "JOBNAME";
	public static final String STARTTIME = "STARTTIME";
	public static final String ENDTIME = "ENDTIME";
	public static final String STATUS = "STATUS";
	public static final String EXCEPTION = "EXCEPTION";
	public static final String ADDITIONALPARAM = "ADDITIONALPARAM";
	public static final String JOB_STATUS_SUCCESS = "Success";
	public static final String JOB_STATUS_FAIL = "Fail";
	public static final String JOB_STATUS_MAIL = "GmJobStatusMail";
	
	private String jobName = "";
	private String jobStartTime = "";
	private String jobEndTime = "";
	SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss"); 
	
	Logger log = GmLogger.getInstance(this.getClass().getName());
	
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
	protected String getJobStartTime() {
		return jobStartTime;
	}
	/**
	 * @param jobStartTime the jobStartTime to set
	 */
	protected void setJobStartTime(String jobStartTime) {
		this.jobStartTime = jobStartTime;
	}
	/**
	 * @return the jobEndTime
	 */
	protected String getJobEndTime() {
		return jobEndTime;
	}
	/**
	 * @param jobEndTime the jobEndTime to set
	 */
	protected void setJobEndTime(String jobEndTime) {
		this.jobEndTime = jobEndTime;
	}
	public static String getClassName(String strClasName)
	{
		String[] strPkgs = null; 
		strPkgs	= strClasName.split("\\.");
		return strPkgs[(strPkgs.length)-1];
	}
}
