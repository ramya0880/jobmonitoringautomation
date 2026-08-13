package com.globus.common.beans;

import com.globus.valueobject.common.GmDataStoreVO;

/**
 * @author vprasath
 * 
 */
public class GmBean {
	private String compDateFmt = "";
	private String compTimeZone = "";
	private String compId = "";
	private String compPlantId = "";
	private String compCountry = "";
	private String complangid = "";
	private String dBConnection = "";

	private GmDataStoreVO gmDataStoreVO = new GmDataStoreVO();

	/**
	 * All Bean needs to extend this bean.
	 */
	/**
	 * @return GmDataStoreVO
	 */
	public GmDataStoreVO getGmDataStoreVO() {
		return this.gmDataStoreVO;
	}

	/**
	 * @return compDateFmt
	 */
	public String getCompDateFmt() {
		return compDateFmt;
	}

	/**
	 * @return compTimeZone
	 */
	public String getCompTimeZone() {
		return compTimeZone;
	}

	/**
	 * @return compId
	 */
	public String getCompId() {
		return compId;
	}

	/**
	 * @return compPlantId
	 */
	public String getCompPlantId() {
		return compPlantId;
	}

	/**
	 * @return compCountry
	 */
	public String getCompCountry() {
		return compCountry;
	}

	public String getComplangid() {
		return complangid;
	}
	
	/**
	 * @return dBConnection
	 */
	public String getdBConnection() {
		return dBConnection;
	}

	/**
	 * Constructor will validate and populate company info. in gmDataStoreVO.
	 * 
	 * @param gmDataStoreVO
	 */
	public GmBean(GmDataStoreVO gmDataStoreVO) {
		super();
		this.gmDataStoreVO = gmDataStoreVO;
		this.compDateFmt = gmDataStoreVO.getCmpdfmt();
		this.compTimeZone = gmDataStoreVO.getCmptzone();
		this.compId = gmDataStoreVO.getCmpid();
		this.compPlantId = gmDataStoreVO.getPlantid();
		this.compCountry = gmDataStoreVO.getCmpcountry();
		this.dBConnection = gmDataStoreVO.getDbconnection();
		this.complangid = gmDataStoreVO.getCmplangid();
	}

}
