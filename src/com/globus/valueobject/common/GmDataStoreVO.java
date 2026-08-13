package com.globus.valueobject.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author JBalaraman
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GmDataStoreVO extends GmDataVO {
	private String cmpid = "";
	private String cmptzone = "";
	private String cmpdfmt = "";
	private String plantid = "";
	private String cmpcountry = "";
	private String dbconnection = "";
	private String cmplangid = "";
	private String partyid = "";

	public String getCmpcountry() {
		return cmpcountry;
	}

	public void setCmpcountry(String cmpcountry) {
		this.cmpcountry = cmpcountry;
	}

	public String getCmpid() {
		return cmpid;
	}

	public void setCmpid(String cmpid) {
		this.cmpid = cmpid;
	}

	public String getPlantid() {
		return plantid;
	}

	public void setPlantid(String plantid) {
		this.plantid = plantid;
	}

	public String getCmptzone() {
		return cmptzone;
	}

	public void setCmptzone(String cmptzone) {
		this.cmptzone = cmptzone;
	}

	public String getCmpdfmt() {
		return cmpdfmt;
	}

	public void setCmpdfmt(String cmpdfmt) {
		this.cmpdfmt = cmpdfmt;
	}

	public String getDbconnection() {
		return dbconnection;
	}

	public void setDbconnection(String dbconnection) {
		this.dbconnection = dbconnection;
	}

	public String getCmplangid() {
		return cmplangid;
	}

	public void setCmplangid(String cmplangid) {
		this.cmplangid = cmplangid;
	}

	public String getPartyid() {
		return partyid;
	}

	public void setPartyid(String partyid) {
		this.partyid = partyid;
	}

}
