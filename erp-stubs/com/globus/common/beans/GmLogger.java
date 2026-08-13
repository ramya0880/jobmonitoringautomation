/*
 * Module: GmLogger.java
 * Author: Joe Prasanna Kumar
 * Project: Globus Medical App
 * Date-Written: 18 Apr 2006
 * Security:     Unclassified
 * Description:  This is the wrapper class of Log4J for usage in GM Enterprise portal 
 *  This class has to be imported in all the classes and an instance will be created to enable logging in those specific CLASSes
 * 
 * Usage in the CLASS which imports GmLogger for logging purpose
 * GmLogger log = GmLogger.getInstance();
 * log.debug("I am Debug");
 * log.info("I am Info.");
 * log.warn("I am Warning");
 * log.error("I am Error");
 * log.fatal("I am Fatal"); 
 *
 * Revision Log (mm/dd/yy initials description)
 * ---------------------------------------------------------
 * mm/dd/yy xxx  What you changed
 *
 */

// Specify the package information
package com.globus.common.beans;

// Import the log4j API which is present in the classpath
import java.io.FileInputStream;
import java.util.Properties;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/*
Public class which will be used as the Top Level custom class for Logging purpose

*/
/**
 * @author JBalaraman
 *
 */
public class GmLogger {
	private static Logger logger = null;

    // Log4j properties file name
    private static String LOG4J_PROPERTIES ="log4j.properties";
    
    // Static final instance of GmLog which will be used by the CLASSes to enable logging
    private static final GmLogger instance = new GmLogger();
    
   //  private static final String logFilePath = "C:\\com";
    /** The resource bundle for the Application related error messages */
	//private static ResourceBundle rbAppError = ResourceBundle.getBundle("AppError");
   private GmLogger()
   {
      logger = GmLogger.getInstance(this.getClass().getName());
      // PropertyConfigurator.configure("path_to_log4j_properties");
      BasicConfigurator.configure();
   }

   /*
    *  Static getInstance method which will be invoked by external CLASSes to get the instance of this class for enabling logging
    */
   public static GmLogger getInstance()
   {
       return instance;
   }

	public static void configure(String logFilePath) {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(logFilePath+"\\"+LOG4J_PROPERTIES));
			PropertyConfigurator.configure(prop);
			logger = GmLogger.getInstance("com.globus.common.beans.GmLogger");
		} catch (Exception e) {
		}
	}

   /*
    *  Static getInstance method which will be invoked by external CLASSes to get the instance of this class for enabling logging
    */
   public static Logger getInstance(String categoryName)
   {
	   return Logger.getLogger(categoryName);
   }
   /*
    * Method for logging DEBUG messages
    */
    public  void debug(String message)
   {
       logger.debug(message);
   }

    /*
     * Method for logging INFO messages
     */
    public  void info(String message)
    {
        logger.info(message);
   }

    /*
     * Method for logging WARN messages
     */
   public  void warn(String message)
         {
             logger.warn(message);
   }

   /*
    * Method for logging ERROR message
    */
   public  void error(String message)
   {
       logger.error(message);
   }

   /*
    * Method for logging FATAL message
    */
   public  void fatal(String message)
      {
          logger.fatal(message);
   }
}

