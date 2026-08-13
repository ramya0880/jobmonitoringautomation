/**
 * Utility Class for handling Connections, Callable Statement and Prepared Statement. Would be used
 * for executing Procedures and Prepared Statement
 * 
 * Usage : 1. Initialize the DBManager by passing the procedure name along and the number of
 * elements to the procedure as parameter This would initialize a Connection and a Callable
 * Statement (callableStatement) for the procedure GmDBManager gmDBManager = new
 * GmDBManager("gm_pd_sav_partnumber",24);
 * 
 * 2. Set all the parameters for the procdure through the instance gmDBManager created earlier
 * gmDBManager.setString(1,strPartNumber);
 * gmDBManager.setInt(8,Integer.parseInt(strMeasuringDevice));
 * 
 * 3. Execute the procedure by calling executeStatement gmDBManager.executeStatement();
 * 
 * 4. If the connection need to be sent to another method say saveLog, then call getActiveConnection
 * gmCommonBean.saveLog(gmDBManager.getActiveConnection(),strPartNumber , strLogReason,strUsername,
 * "1218");
 * 
 * 5. The Transaction can be explicitly commited by calling commitConnection
 * gmDBManager.commitConnection();
 * 
 * Advantages : 1. All the DB Connections are centrally controlled and managed by DBManager so we
 * could avoid any stale connection issues
 * 
 */
package com.globus.common.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;


import com.globus.common.beans.GmCommonClass;
import com.globus.common.beans.GmExceptionBean;
import com.globus.common.beans.GmLogger;
import com.globus.valueobject.common.GmDataStoreVO;


/**
 * @author JBalaraman
 *
 */
public class GmDBManager extends DBConnectionWrapper {
	public static final long serialVersionUID = 1232323453L;
	Logger log = GmLogger.getInstance(this.getClass().getName());
	/*
	 * Following is an enumeration concept where we can specify respective
	 * connections. So for OLTP and JOBs it refers different connections.
	 */
	public enum GmDNSNamesEnum {
		WEBGLOBUS("jdbc/globus"), WEBGLOBUSTEST("jdbc/globus_test"), WEBGLOBUSPROD(
				"jdbc/globus_prod"), WEBGLOBUSSTAGE("jdbc/globus_stage"), WEBGLOBUSPREPROD(
				"jdbc/globus_preprod"), EXTTIME_WEBGLOBUS("jdbc/globus_extweb");

		private String strDNSName;

		private GmDNSNamesEnum(String value) {
			this.strDNSName = value;
		}

		public String getDNSName() {
			return this.strDNSName;
		}
	};

	/*
	 * Default constructor which will refer the WEBGLOBUS to connect
	 */
	public GmDBManager() {
		this.enDNSName = enDNSName;
	}

	/**
	 * Constructor will set time zone and it will used to set in DB connection
	 * session
	 * 
	 * @param gmDataStoreVO
	 */
	public GmDBManager(GmDataStoreVO gmDataStoreVO) {
		super(gmDataStoreVO);
		this.enDNSName = enDNSName;
	}

	/*
	 * parameterized constructor which will refer the parameter to connect
	 */
	public GmDBManager(GmDNSNamesEnum enDNSName_) {
		this.enDNSName = enDNSName_;
	}

	/**
	 * Constructor will set time zone and it will used to set in DB connection
	 * session
	 * 
	 * @param enDNSName_
	 * @param gmDataStoreVO
	 */
	public GmDBManager(GmDNSNamesEnum enDNSName_, GmDataStoreVO gmDataStoreVO) {
		super(gmDataStoreVO);
		this.enDNSName = enDNSName_;
	}

	/**
	 * GmDBManager - Initializes the CallableStatement and Connection for the
	 * procedure
	 * 
	 * @param strProcedureName
	 *            , intProcedureParameters
	 * @throws Exception
	 */
	public void setPrepareString(String strProcedureName,
			int intProcedureParameters) throws Exception {
		this.strProcedureName = strProcedureName;
		this.intProcedureParameters = intProcedureParameters;

		try {

			if (strProcedureName != null && intProcedureParameters != -1) {
				instantiate(this.enDNSName);
				strPrepareString = this.getStrPrepareString(strProcedureName,
						intProcedureParameters);
				preparedStatement = conn.prepareCall(strPrepareString);
				callableStatement = (CallableStatement) preparedStatement;

				setTimeOut();
			}
		} catch (Exception e) {
			terminate();
			String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
			throw new Exception(strErrorMsg);
		}
	}

	/**
	 * instantiate - instantiates the Connection and CallableStatement /
	 * PreparedStatement
	 * 
	 * @throws Exception
	 */
	private void instantiate(GmDNSNamesEnum enDNSName) throws Exception {
		try {
			if (conn == null) {
				// conn = getOracleStaticConnection();
				getConnection(enDNSName);
				conn.setAutoCommit(false);
			}

		  } catch (Exception e) {		
			terminate();
			String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
			// log.error("Exception e " + strErrorMsg);
			  throw new Exception(strErrorMsg);
		}
	}

	/**
	 * finalize - calls terminate to close any open connection or
	 * callablestatement
	 */
	/**
	 * execute - will execute the active CallableStatement
	 * 
	 * @throws Exception
	 */
	public void execute() throws Exception {
		try {
			callableStatement.execute();
		    } catch (Exception e) {
			 String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
			// log.error("Exception e " + strErrorMsg);
			// PC-19777: Add terminate to close connection 
			// on execute exception handling
			    terminate();
			    throw new Exception(strErrorMsg);
			}
	}

	/**
	 * commit - will commitConnection the active conn
	 * 
	 * @throws Exception
	 */
	public void commit() throws Exception {
		try {
			conn.commit();
		} catch (Exception e) {
			 String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
			// log.error("Exception e " + strErrorMsg);
				      throw new Exception(strErrorMsg);
			} finally {
			terminate();
		}
	}

	/**
	 * commit - will commitConnection the active conn
	 * 
	 * @throws Exception
	 */
	public void close() throws Exception {
		try {
			terminate();
		} catch (Exception e) {
			 String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
			// log.error("Exception e " + strErrorMsg);
				      throw new Exception(strErrorMsg);
			} finally {
			terminate();
		}
	}

	/**
	 * setString - sets the String at the intParameterIndex in the
	 * CallableStatement
	 */
	public void setString(int intParameterIndex, String strValue)
			throws Exception {
		try {
			preparedStatement.setString(intParameterIndex, strValue);
		
	        } catch (Exception exp) {
	        		terminate();
	        		exp.printStackTrace();
	        		String strErrorMsg = GmExceptionBean.getExceptionStackTrace(exp);
		          // log.error("Exception e " + strErrorMsg);
			      throw new Exception(strErrorMsg);
		 } 

	}

	/**
	 * setInt - sets the intValue at the intParameterIndex in the
	 * CallableStatement
	 */

	public void setInt(int intParameterIndex, int intValue) throws Exception {
		try {
			preparedStatement.setInt(intParameterIndex, intValue);
		 } catch (Exception exp) {
     		terminate();
     		exp.printStackTrace();
     		String strErrorMsg = GmExceptionBean.getExceptionStackTrace(exp);
	          // log.error("Exception e " + strErrorMsg);
		      throw new Exception(strErrorMsg);
	 } 
	}

	/**
	 * getGmDBManager - Uses to get the object of GmDBManager based on
	 * JNDI(constant prop.-> JNDI_CONNECTION)
	 * 
	 * @return GmDBManager
	 */
	static public GmDBManager getGmDBManager(GmDataStoreVO gmDataStoreVO) {
		if (gmDataStoreVO.getDbconnection().equals("Test")) {
			return new GmDBManager(GmDNSNamesEnum.WEBGLOBUSTEST, gmDataStoreVO);
		} else if (gmDataStoreVO.getDbconnection().equals("Stage")) {
			return new GmDBManager(GmDNSNamesEnum.WEBGLOBUSSTAGE, gmDataStoreVO);
		} else if (gmDataStoreVO.getDbconnection().equals("PreProd")) {
			return new GmDBManager(GmDNSNamesEnum.WEBGLOBUSPREPROD,
					gmDataStoreVO);
		} else if (gmDataStoreVO.getDbconnection().equals("Prod")) {
			return new GmDBManager(GmDNSNamesEnum.WEBGLOBUSPROD, gmDataStoreVO);
		} else {
			return new GmDBManager(GmDNSNamesEnum.WEBGLOBUS, gmDataStoreVO);
		}
	}
	  /**
	   * registerOurParameter - sets the registerOurParameter at the intParameterIndex in the
	   * CallableStatement
	   */
	  public void registerOutParameter(int intParameterIndex, int intOracleType) throws Exception {
	    try {
	      callableStatement.registerOutParameter(intParameterIndex, intOracleType);
	    }

	    catch (Exception exp) {
	      terminate();
	      exp.printStackTrace();
	      String strErrorMsg = GmExceptionBean.getExceptionStackTrace(exp);
	      throw new Exception(strErrorMsg);
	    }
	  }

	  /**
	   * getString - gets the OUT String
	   */
	  public String getString(int intResultSetIndex) throws Exception {
	    String strResult = "";
	    try {
	      strResult = callableStatement.getString(intResultSetIndex);
	    } catch (Exception exp) {
	      terminate();
	      exp.printStackTrace();
	      String strErrorMsg = GmExceptionBean.getExceptionStackTrace(exp);
	      throw new Exception(strErrorMsg);
	    }
	    return strResult;
	  }
	  /**
	   * getObject - gets the OUT object
	   */
	  public Object getObject(int intResultSetIndex) throws Exception {
	    Object objResult = null;
	    try {
	      objResult = callableStatement.getObject(intResultSetIndex);
	    } catch (Exception exp) {
	      terminate();
	      exp.printStackTrace();
	      String strErrorMsg = GmExceptionBean.getExceptionStackTrace(exp);
	      throw new Exception(strErrorMsg);
	    }
	    return objResult;
	  }
	  /**
	   * GmDBManager - Initializes the CallableStatement and Connection for the procedure
	   * 
	   * @param strProcedureName , intProcedureParameters
	   * @throws AppError
	   */
	  public void setFunctionString(String strFunctionName, int intFunctionParameters) throws Exception {
	    this.strFunctionName = strFunctionName;
	    this.intFunctionInParameters = intFunctionParameters;
	    try {

	      if (strFunctionName != null && intFunctionInParameters != -1) {
	        instantiate(this.enDNSName);
	        strPrepareString =
	            "{?=" + this.getStrPrepareString(strFunctionName, intFunctionInParameters) + "}";
	        preparedStatement = conn.prepareCall(strPrepareString);
	        callableStatement = (CallableStatement) preparedStatement;
	        setTimeOut();
	      }
	    } catch (Exception e) {
	      terminate();
	      String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
	      throw new Exception(strErrorMsg);
	    }
	  }
	  /**
	   * This function is used to execute SQL "SELECT" queries. The function allows to query the
	   * database and get the result data from it.
	   * 
	   * @param astrQuery This parameter is a string containing plain SQL query.
	   * @return Hashtable containing all the values returned in the resultset.
	   * @throws AppError This is the standard Application error object
	   */
	  public HashMap querySingleRecord(String astrQuery) throws Exception {
	    String str = null;
	    ResultSetMetaData meta;
	    HashMap hmResult;
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    try {
	      conn = getConnection();
	      stmt = conn.createStatement();
	      rs = stmt.executeQuery(astrQuery);
	      meta = rs.getMetaData();
	      int count = meta.getColumnCount();
	      hmResult = new HashMap(1);

	      while (rs.next()) {
	        int size = (int) (count * 1.33);
	        for (int i = 1; i <= count; i++) {
	          if (meta.getColumnTypeName(i).equals("DATE")) {
	            hmResult.put(meta.getColumnName(i), rs.getDate(i));
	          } else {
	            str = rs.getString(i);

	            if (str == null) {
	              str = "";
	            }
	            hmResult.put(meta.getColumnName(i), str.trim());
	          }
	        }
	      }
	      // stmt = null;
	      // rs = null;
	    } catch (Exception e) {
	      String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
	      //log.error("Exception e " + strErrorMsg);
	      throw new Exception(strErrorMsg);
	    } finally {
	      try {
	        if (rs != null && !rs.isClosed()) {
	          rs.close();
	        }
	        if (stmt != null && !stmt.isClosed()) {
	          stmt.close();
	        }
	        if (conn != null && !conn.isClosed()) {
	          conn.close();
	        }
	      } catch (Exception e) {
	        String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
	        //log.error("Exception e " + strErrorMsg);
	        throw new Exception(strErrorMsg);
	      }
	    }
	    return hmResult;
	  } // END of method executeResultSet
	  
	  /**
	   * This function is used to execute SQL "SELECT" queries. The function allows to query the
	   * database and get the result data from it.
	   * 
	   * @param astrQuery This parameter is a string containing plain SQL query.
	   * @return ArrayList of HashMaps containing all the values returned in the resultset.
	   * @throws AppError This is the standard Application error object
	   */
	  public ArrayList queryMultipleRecords(String astrQuery) throws Exception {
	    String str = null;
	    ResultSetMetaData meta;
	    ArrayList alistResult;
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    HashMap hmapResult;
	    try {
	      conn = getConnection();

	      stmt = conn.createStatement();
	      stmt = setTimeOut(stmt);
	      rs = stmt.executeQuery(astrQuery);
	      meta = rs.getMetaData();

	      alistResult = new ArrayList(1);

	      int count = meta.getColumnCount();
	      int size = (int) (count * 1.33);

	      while (rs.next()) {
	        hmapResult = new HashMap(size);
	        for (int i = 1; i <= count; i++) {
	          if (meta.getColumnTypeName(i).equals("DATE")) {
	            hmapResult.put(meta.getColumnName(i), rs.getDate(i));
	          } else {
	            str = rs.getString(i);

	            if (str == null) {
	              str = "";
	            }
	            hmapResult.put(meta.getColumnName(i), str.trim());
	          }
	        }

	        alistResult.add(hmapResult);
	        hmapResult = null;
	      }
	      // stmt = null;
	      // rs = null;
	    } catch (Exception e) {
	      String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
	      //log.error("Exception e " + strErrorMsg);
	      throw new Exception(strErrorMsg);
	    } finally {
	      try {
	        if (rs != null && !rs.isClosed()) {
	          rs.close();
	        }
	        if (stmt != null && !stmt.isClosed()) {
	          stmt.close();
	        }
	        if (conn != null && !conn.isClosed()) {
	          conn.close();
	        }
	      } catch (Exception e) {
	        String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
	        //log.error("Exception e " + strErrorMsg);
	        throw new Exception(strErrorMsg);
	      }
	    }
	    return alistResult;
	  } // END of method queryMultipleRecords	  
    
      /**
       * querySingleRecord - Uses the querySingleRecord of DBConnectionWrapper
       * 
       * @return HashMap
       * @throws AppError
       */
      public HashMap querySingleRecord() throws Exception {
        ResultSet rs = null;
        HashMap hmReturn = new HashMap();
        try {
          rs = preparedStatement.executeQuery();
          hmReturn = this.returnHashMap(rs);
          rs = null;
        } catch (Exception exp) {
          // terminate();
          exp.printStackTrace();
          String strErrorMsg = GmCommonClass.getExceptionStackTrace(exp);
         // log.error("Exception e " + strErrorMsg);
          throw new Exception(strErrorMsg);
        } finally {
          terminate();
          try {
            if (rs != null)
              rs.close();
          } catch (Exception exp) {
            exp.printStackTrace();
            String strErrorMsg = GmCommonClass.getExceptionStackTrace(exp);
           // log.error("Exception e " + strErrorMsg);
            throw new Exception(strErrorMsg);
          }
        }
        return hmReturn;
      }
      
      public void setPrepareString(String strSQL) throws Exception {
    	    this.strSQL = strSQL;
    	    try {
    	      if (strSQL != null) {
    	        instantiate(this.enDNSName);
    	        preparedStatement = conn.prepareStatement(strSQL);
    	        setTimeOut();
    	      }
    	    } catch (Exception e) {
    	      terminate();
    	      String strErrorMsg = GmCommonClass.getExceptionStackTrace(e);
    	    //  log.error("Exception e " + strErrorMsg);
    	      throw new Exception(strErrorMsg);
    	    }
    	  } 
      
      /**
       * To invoke an update statement
       * @param strUpdateStatement
       * @return
       * @throws Exception
       */
      public int executeUpdate(String strUpdateStatement) throws Exception {
  	    Connection connection = null;
  	    Statement stmt = null;
  	    int iUpdateCount = -1;
  	    try {
  	      connection = getConnection();
  	      stmt = connection.createStatement();
  	      iUpdateCount = stmt.executeUpdate(strUpdateStatement);
  	    } catch (Exception e) {
  	      String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
  	      throw new Exception(strErrorMsg);
  	    } finally {
  	      try {
  	        if (stmt != null && !stmt.isClosed()) {
  	          stmt.close();
  	        }
  	        if (connection != null && !connection.isClosed()) {
  	          connection.close();
  	        }
  	      } catch (Exception e) {
  	        String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
  	        throw new Exception(strErrorMsg);
  	      }
  	    }
  	    return iUpdateCount;
  	  }
      
      /**
      * This method is used to execute SQL "SELECT" queries.
      * @param strQuery This parameter is a string containing plain SQL query.
 	  * @return String
      **/
		public String queryGetSingleRecord(String query) throws Exception {
			log.info("Executing queryGetSingleRecord for query: " + query);
			Connection conn = null;    Statement stmt = null;    ResultSet rs = null;
			String result = GmCommonClass.parseNull(null);
			try {
				conn = getConnection();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
				ResultSetMetaData meta = rs.getMetaData();
				int columnCount = meta.getColumnCount();
				while (rs.next()) {
					for (int i = 1; i <= columnCount; i++) {
						result = GmCommonClass.parseNull(rs.getString(i));
					}
				}
			} catch (Exception e) {
				log.error("Exception e " + GmCommonClass.parseNull(GmExceptionBean.getExceptionStackTrace(e)));
				throw new Exception(GmCommonClass.parseNull(GmExceptionBean.getExceptionStackTrace(e)));
			} finally {
				try {
					if (rs != null) rs.close();
					if (stmt != null) stmt.close();
					if (conn != null) conn.close();
				} catch (Exception e) {
					log.error("Exception e " + GmCommonClass.parseNull(GmExceptionBean.getExceptionStackTrace(e)));
					throw new Exception(GmCommonClass.parseNull(GmExceptionBean.getExceptionStackTrace(e)));
				}
			}
			return result;
		}

		/**
	      * setDateTime - sets the dateValue at the intParameterIndex in the CallableStatement
	      */

      public void setDateTime(int intParameterIndex, java.util.Date dtValue)
  			throws Exception {
  		try {
  			if (dtValue == null) {
  	           preparedStatement.setTimestamp(intParameterIndex, null);
  	         } else {
  	        	java.sql.Timestamp sqlDateTime = new java.sql.Timestamp(dtValue.getTime());
  	           preparedStatement.setTimestamp(intParameterIndex, sqlDateTime);
  	         }
  	        } catch (Exception exp) {
  	        		terminate();
  	        		exp.printStackTrace();
  	        		String strErrorMsg = GmExceptionBean.getExceptionStackTrace(exp);
  		           log.error("Exception e " + strErrorMsg);
  			      throw new Exception(strErrorMsg);
  		 } 
  	   }
	    
	   public void setBigDecimal(int intParameterIndex, java.math.BigDecimal value) throws Exception {
		try {
			preparedStatement.setBigDecimal(intParameterIndex, value);
		} catch (Exception exp) {
			terminate();
			exp.printStackTrace();
			String strErrorMsg = GmExceptionBean.getExceptionStackTrace(exp);
			 log.error("Exception e " + strErrorMsg);
			throw new Exception(strErrorMsg);
		}
	}
}

