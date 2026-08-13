package com.globus.common.db;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.beanutils.RowSetDynaClass;

import com.globus.common.beans.GmCommonClass;
import com.globus.common.beans.GmExceptionBean;
import com.globus.common.db.GmDBManager.GmDNSNamesEnum;
import com.globus.valueobject.common.GmDataStoreVO;

/**
 * This is the Database utility class which is used for handling all the database operations
 * throughout the application It essentially provides methods to execute DML statements using Normal
 * Statements, Prepared Statements. It also provides methods for standard transactional handling
 */
/**
 * @author JBalaraman
 *
 */
public class DBConnectionWrapper implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Context ic;
	protected Connection conn = null;
	protected String strPrepareString = null;
	protected String strSQL = null;
	protected String strProcedureName = null;
	protected int intProcedureParameters = -1;
	protected String strFunctionName = null;
	protected int intFunctionInParameters = -1;
	protected CallableStatement callableStatement = null;
	protected PreparedStatement preparedStatement = null;
	// protected Logger log = GmLogger.getInstance(this.getClass().getName());//
	// Code to Initialize the
	// Logger Class.
	protected GmDNSNamesEnum enDNSName = GmDNSNamesEnum.WEBGLOBUS;
	protected String strTimeZone = "";
	protected String strCompanyID = "";
	protected String strCompDateFmt = "";
	protected String strPlantID = "";
	protected String strPartyID = "";
	protected String strCompLangID = "103097"; // Default as English(103097)

	static {
		try {
			ic = new InitialContext();
		} catch (Exception e) {

		}
	}

	/**
	 * Default constructor
	 * 
	 * @throws Exception
	 *             This is the standard Application error object
	 */

	public DBConnectionWrapper() {

	} // END of DBSession constructor

	/**
	 * constructor - set company info
	 * 
	 * @paramGmDataStoreVO
	 * @throws Exception
	 *             This is the standard Application error object
	 */
	public DBConnectionWrapper(GmDataStoreVO gmDataStoreVO) {
		this.strTimeZone = GmCommonClass.parseNull(gmDataStoreVO.getCmptzone());
		this.strCompanyID = GmCommonClass.parseNull(gmDataStoreVO.getCmpid());
		this.strCompDateFmt = GmCommonClass.parseNull(gmDataStoreVO
				.getCmpdfmt());
		this.strPlantID = GmCommonClass.parseNull(gmDataStoreVO.getPlantid());
	}

	/**
	 * The function is used to establish the connection
	 * 
	 * @throws Exception
	 *             This is the standard Application error object
	 */
	public Connection getConnection() throws Exception {
		return getConnection(enDNSName);
	}

	/**
	 * The function is used to establish the connection
	 * 
	 * @throws Exception
	 *             This is the standard Application error object
	 */
	public Connection getConnection(GmDNSNamesEnum enDNSName) throws Exception {
		// Connection connection = null;
		// String dsName = "jdbc/globus";
		try {
			DataSource ds = (DataSource) ic.lookup(enDNSName.getDNSName());
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			// If time zone is not empty then DB session time zone will alter
			// with given time zone and
			// application context will set
			// Company Id, Plant Id, Time Zone, date format
			if (!strTimeZone.equals("")) {
				setSessionTimeZone(strTimeZone);
				loadAppContext(conn);
			}

		} catch (Exception e) {
			 String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
			// log.error("Exception e " + strErrorMsg);
				      throw new Exception(strErrorMsg);
			} 


		return conn;
	}

	/**
	 * This method will set DB session time zone for every connection
	 * 
	 * @param strTimeZone
	 * @throws Exception
	 */
	private void setSessionTimeZone(String strTimeZone)
			throws Exception {
		Statement stmt = null;
		if (conn != null) {
			try {
				stmt = conn.createStatement();
				stmt = setTimeOut(stmt);
				stmt.execute("alter session set time_zone=\'" + strTimeZone
						+ "\'");
			} catch (Exception e) {
				 String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
				// log.error("Exception e " + strErrorMsg);
					      throw new Exception(strErrorMsg);
				} 
		}
	}

	/**
	 * This method will be called every time connection is established. This
	 * method will set the Required Parameters to the Oracle Session Context.
	 * These values will / can be accessed in the Oracle PL/SQL Block.
	 */
	public void loadAppContext(Connection conn) throws SQLException, Exception {
		CallableStatement csmt = null;
		strPrepareString = getStrPrepareString(
				"gm_pkg_cor_client_context.gm_sav_client_context", 6);
		csmt = conn.prepareCall(strPrepareString);
		csmt.setString(1, strCompanyID);
		csmt.setString(2, strTimeZone);
		csmt.setString(3, strCompDateFmt);
		csmt.setString(4, strPlantID);
		csmt.setString(5, strPartyID);
		csmt.setString(6, strCompLangID);
		csmt.execute();

	}

	/**
	 * This function is used to execute SQL "SELECT" queries. The function
	 * allows to query the database and get the result data from it.
	 * 
	 * @param astrQuery
	 *            This parameter is a string containing plain SQL query.
	 * @return Hashtable containing all the values returned in the resultset.
	 * @throws Exception
	 *             This is the standard Application error object
	 */

	/**
	 * This method returns a PreparedString used to interface with the Oracle
	 * stored procedures.
	 * 
	 * @return String
	 * @throws SQLException
	 */

	public String getStrPrepareString(String strProcedureName, int intNoArgs)
			throws SQLException {
		String strPrepareString = "call " + strProcedureName + "(";
		for (int i = 0; i < intNoArgs; i++) {
			strPrepareString += "?";
			if (i != intNoArgs - 1)
				strPrepareString += ",";
		}
		strPrepareString += ")";

		return strPrepareString;
	}// END of getStrPrepareString method

	/**
	 * terminate - will close any active callablestatement or connection will be
	 * called from finally
	 * 
	 * @throws Exception
	 */
	protected void terminate() throws Exception {
		try {
			if (callableStatement != null && !callableStatement.isClosed()) {
				callableStatement.close();

			}// Enf of if (callableStatement != null)

			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}

			if (conn != null && !conn.isClosed()) {
				conn.rollback();
				conn.close(); // closing the Connections
			}
		} catch (Exception e) {
			 String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
			// log.error("Exception e " + strErrorMsg);
				      throw new Exception(strErrorMsg);
			} // End of catch
		finally {
			preparedStatement = null;
			callableStatement = null;
			conn = null;
		}
	}

	/**
	 * Set timeout - will call for preparedStatement and callablestatement
	 * connection time out
	 * 
	 * @throws Exception
	 */
	public void setTimeOut() throws Exception {
		try {
			if (preparedStatement != null && !preparedStatement.isClosed()) {

				if ((this.enDNSName.getDNSName())
						.equalsIgnoreCase(GmDNSNamesEnum.WEBGLOBUS.getDNSName()))
				{

					preparedStatement.setQueryTimeout(300);
				}
			}
		} catch (Exception e) {
			 String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
			// log.error("Exception e " + strErrorMsg);
				      throw new Exception(strErrorMsg);
			}

	}

	/**
	 * Set timeout - will call for query execution connection time out
	 * 
	 * @throws Exception
	 */
	public Statement setTimeOut(Statement stmt) throws Exception {
		try {
			if (stmt != null && !stmt.isClosed()) {

				if ((this.enDNSName.getDNSName())
						.equalsIgnoreCase(GmDNSNamesEnum.WEBGLOBUS.getDNSName()))

				{
					stmt.setQueryTimeout(300);
				}
			}
			return stmt;
		} catch (Exception e) {
			 String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
			// log.error("Exception e " + strErrorMsg);
				      throw new Exception(strErrorMsg);
			}

	}
	  /**
	   * This function is used to execute SQL "SELECT" queries. The function allows to query the
	   * database and get the result data from it.
	   * 
	   * @param astrQuery This parameter is a string containing plain SQL query.
	   * @return ArrayList of HashMaps containing all the values returned in the resultset.
	   * @throws AppError This is the standard Application error object
	   */
	  public ArrayList returnArrayList(ResultSet rs) throws Exception {
	    String str = null;
	    ResultSetMetaData meta;
	    ArrayList alistResult;

	    try {
	      meta = rs.getMetaData();
	      int count = meta.getColumnCount();
	      alistResult = new ArrayList(1);

	      while (rs.next()) {
	        int size = (int) (count * 1.33);
	        HashMap hmapResult = new LinkedHashMap(size);
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
	      // rs = null;
	    } catch (Exception e) {
	      terminate();
	      String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
	      throw new Exception(strErrorMsg);
	    } finally {
	      try {
	        if (rs != null && !rs.isClosed()) {
	          rs.close();
	        }
	      } catch (Exception e) {
	        String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
	        throw new Exception(strErrorMsg);
	      }
	    }
	    return alistResult;
	  } // END of method returnArrayList
	  
	  
	  /**
	   * This function is used to execute SQL "SELECT" queries. The function allows to query the
	   * database and get the result data from it and Key is converted in Lowercase.
	   * 
	   * @param astrQuery This parameter is a string containing plain SQL query.
	   * @return ArrayList of HashMaps containing all the values returned in the resultset. 
	   * @throws AppError This is the standard Application error object
	   */
	  public ArrayList returnLowerCaseKeyArrayList(ResultSet rs) throws Exception {
	    String str = null;
	    ResultSetMetaData meta;
	    ArrayList alistResult;

	    try {
	      meta = rs.getMetaData();
	      int count = meta.getColumnCount();
	      alistResult = new ArrayList(1);

	      while (rs.next()) {
	        int size = (int) (count * 1.33);
	        HashMap hmapResult = new LinkedHashMap(size);
	        for (int i = 1; i <= count; i++) {
	        String columnNmLC = meta.getColumnName(i).toLowerCase();
	          if (meta.getColumnTypeName(i).equals("DATE")) {
	            hmapResult.put(columnNmLC, rs.getDate(i));
	          } else {
	            str = rs.getString(i);

	            if (str == null) {
	              str = "";
	            }
	            hmapResult.put(columnNmLC, str.trim());
	          }
	        }
	        alistResult.add(hmapResult);
	        hmapResult = null;
	      }
	      // rs = null;
	    } catch (Exception e) {
	      terminate();
	      String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
	      throw new Exception(strErrorMsg);
	    } finally {
	      try {
	        if (rs != null && !rs.isClosed()) {
	          rs.close();
	        }
	      } catch (Exception e) {
	        String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
	        throw new Exception(strErrorMsg);
	      }
	    }
	    return alistResult;
	  } // END of method returnArrayListLowerCaseKey
	  
	  /**
	   * This function is used to execute SQL "SELECT" queries. The function allows to query the
	   * database and get the result data from it and Key is converted in Lowercase.
	   * 
	   * @param astrQuery This parameter is a string containing plain SQL query.
	   * @return ArrayList of HashMaps containing all the values returned in the resultset. 
	   * @throws AppError This is the standard Application error object
	   */
	  public ArrayList returnLowerCaseKeyWithNumberTypeArrayList(ResultSet rs) throws Exception {
	    String str = null;
	    ResultSetMetaData meta;
	    ArrayList alistResult;

	    try {
	      meta = rs.getMetaData();
	      int count = meta.getColumnCount();
	      alistResult = new ArrayList(1);

	      while (rs.next()) {
	        int size = (int) (count * 1.33);
	        HashMap hmapResult = new LinkedHashMap(size);
	        for (int i = 1; i <= count; i++) {
	        String columnNmLC = meta.getColumnName(i).toLowerCase();
	          if (meta.getColumnTypeName(i).equals("DATE")) {
	            hmapResult.put(columnNmLC, rs.getDate(i));
	          }else if (meta.getColumnTypeName(i).equals("NUMBER")) {
		            hmapResult.put(columnNmLC, rs.getInt(i));
		      } else {
	            str = rs.getString(i);

	            if (str == null) {
	              str = "";
	            }
	            hmapResult.put(columnNmLC, str.trim());
	          }
	        }
	        alistResult.add(hmapResult);
	        hmapResult = null;
	      }
	      // rs = null;
	    } catch (Exception e) {
	      terminate();
	      String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
	      throw new Exception(strErrorMsg);
	    } finally {
	      try {
	        if (rs != null && !rs.isClosed()) {
	          rs.close();
	        }
	      } catch (Exception e) {
	        String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
	        throw new Exception(strErrorMsg);
	      }
	    }
	    return alistResult;
	  } // END of method returnArrayListLowerCaseKey
	  	  
	  /**
	   * This function is used to execute SQL "SELECT" queries. The function allows to query the
	   * database and get the result data from it and returns the result set value
	   * 
	   * @param astrQuery This parameter is a string containing plain SQL query.
	   * @return RowSetDynaClass object.
	   * @throws AppError This is the standard Application error object
	   */
	  public RowSetDynaClass QueryDisplayTagRecordset(String astrQuery) throws Exception {
	    String str = null;
	    ResultSetMetaData meta;
	    ArrayList alistResult;
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    RowSetDynaClass resultSet = null;

	    try {
	      conn = getConnection();
	      stmt = conn.createStatement();
	      stmt = setTimeOut(stmt);
	      rs = stmt.executeQuery(astrQuery);

	      // To assign the value to Dynamic tag result set object
	      resultSet = new RowSetDynaClass(rs, false);

	      // stmt = null;
	      // rs = null;
	    } catch (Exception e) {
	      terminate();
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
	    return resultSet;
	  } // END of method queryMultipleRecords
	  public HashMap returnHashMap(ResultSet rs) throws Exception {
		    String str = null;
		    ResultSetMetaData meta;
		    HashMap hmResult;
		    Connection conn = null;
		    Statement stmt = null;

		    try {
		      meta = rs.getMetaData();
		      int count = meta.getColumnCount();
		      hmResult = new HashMap(1);

		      if (rs.next()) {
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
		    } catch (Exception e) {
		      terminate();
		      String strErrorMsg = GmCommonClass.getExceptionStackTrace(e);
		    //  log.error("Exception e " + strErrorMsg);
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
		         String strErrorMsg = GmCommonClass.getExceptionStackTrace(e);
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
	    } catch (Exception e) {
	      String strErrorMsg = GmExceptionBean.getExceptionStackTrace(e);
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
	 * @throws Exception 
	   * @throws AppError This is the standard Application error object
	   */
	  public ArrayList queryMultipleRecords(String astrQuery) throws Exception  {
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
  
}
