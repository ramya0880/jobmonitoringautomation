package com.globus.common.beans;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.log4j.Logger;
/**
 * @author JBalaraman
 *
 */
public class GmExceptionBean extends GmCommonClass {
	Logger log = GmLogger.getInstance(this.getClass().getName());
	  /**
	   * This method returns the Exception message
	   * 
	   * @param e
	   * @param strNewLine
	   * @return String
	   */
	  public static String getExceptionStackTrace(Exception e, String strNewLine) {
	    try {
	      StackTraceElement stack[] = e.getStackTrace();
	      StringBuffer strErrorBuffer = new StringBuffer();
	      // stack[0] contains the method that created the exception.
	      // stack[stack.length-1] contains the oldest method call.
	      // Enumerate each stack element.
	      strErrorBuffer.append(e.getMessage() + strNewLine);
	      for (int i = 0; i < stack.length; i++) {
	        String filename = stack[i].getFileName();
	        if (filename == null) {
	          filename = " The source filename is not available ";
	        }
	        String className = stack[i].getClassName();
	        String methodName = stack[i].getMethodName();
	        int line = stack[i].getLineNumber();
	        strErrorBuffer.append("\t at " + className);
	        strErrorBuffer.append("." + methodName + "(");
	        strErrorBuffer.append(filename + ":");
	        strErrorBuffer.append(line + ")" + strNewLine);
	      }
	      return strErrorBuffer.toString();
	    } catch (Exception ex) {
	      return "Exception stack trace not found.";
	    }
	  }

	  /*
	   * getExceptionStackTrace is an overloaded version and used to convert the stack trace to string
	   * for better logging.
	   */
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
} // End of GmCommonClass

