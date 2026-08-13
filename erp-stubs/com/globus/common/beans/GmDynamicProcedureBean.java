package com.globus.common.beans;
import com.globus.common.beans.GmBean;
import com.globus.common.db.GmDBManager;
import com.globus.common.db.GmDBManager.GmDNSNamesEnum;
import com.globus.valueobject.common.GmDataStoreVO;


/**
 * @author jgurunathan
 * PC-691 - This method will pass the dynamic procedure with or without any input or output parameters
 */
public class GmDynamicProcedureBean extends GmBean {
	/**
	 * Constructor will populate company info.
	 * 
	 * @param gmDataStore
	 */
	public GmDynamicProcedureBean(GmDataStoreVO gmDataStore) {
		super(gmDataStore);
	}

	/**
	 * @author jgurunathan
	 * @param strProcedureName
	 * PC-691 - This method will pass the dynamic procedure with no parameters
	 */
	public void processDynamicProcedureNoParam(String strProcedureName) throws Exception {
		GmDBManager gmDBManager = GmDBManager.getGmDBManager(getGmDataStoreVO());
        gmDBManager.setPrepareString(strProcedureName, 0);
        gmDBManager.execute();
        gmDBManager.commit();
	}


	/**
	 * @author jgurunathan
	 * @param strProcedureName
	 * PC-691 - This method will pass the dynamic procedure with no parameters and Extra time DBManager 
	 */
	public void processDynamicProcedureNoParamWithExtTime(String strProcedureName) throws Exception {
		GmDBManager gmDBManager = new GmDBManager(GmDNSNamesEnum.EXTTIME_WEBGLOBUS, getGmDataStoreVO());
        gmDBManager.setPrepareString(strProcedureName, 0);
        gmDBManager.execute();
        gmDBManager.commit();
	}

}
