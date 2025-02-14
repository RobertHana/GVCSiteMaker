package net.global_village.woextensions;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import ognl.webobjects.*;

import org.apache.log4j.*;
import org.apache.log4j.xml.*;

import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.ajax.*;
import er.extensions.appserver.*;
import er.extensions.appserver.ajax.*;
import er.extensions.components._private.*;
import er.extensions.eof.*;
import er.extensions.foundation.*;
import er.extensions.jdbc.*;
import er.extensions.logging.*;
import er.extensions.migration.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;
import net.global_village.jmail.*;


/**
 * <p>A collection of extensions for WOApplication. Your Application class should sub-class this class. <br>
 * Note that this class uses the property lists extensively. You must ensure that the keys exist and contain valid data if you wish to use GVC's WOApplication.  See documentation for loadConfigurationInfo() on how to specify additional property lists. <br>
 * <b>A typical project will set the following keys:</b><br>
 * <ul>
 * <li> WOAdaptorURL (<b>Required</b>) - returns the URL for the web server including the path to the WebObjects CGI adaptor (eg. http://localhost/cgi-bin/WebObjects).</li>
 * <li> SMTPHost (<b>Deprecated</b>  use mail.smtp.host in Properties) - name of the host that will be used to send email messages. (eg. mail.global-village.net)</li>
 * <li> PageCacheSize - size of the internal cache for page instances. The default is 30 instances.</li>
 * <li> PermanentPageCacheSize - size of the internal cache for permanent page instances. The default is 10 instances.</li>
 * <li> CachingEnabled - returns whether or not component caching is enabled. The default is true.</li>
 * <li> SessionTimeoutInMinutes - the number of seconds which will be used as the default timeout for each newly created session.</li>
 * <li> DateFormat - the format to be used for displaying dates. (eg. "%b %d %Y")</li>
 * <li> NumberFormat - the format to be used for displaying numbers (eg. "##.####,0;(##.####)")</li>
 * <li> ApplicationsEmailAddress - Should be the complete e-mail address representing the application. This is used as the from address for all e-mail sent to the supportAddress.</li>
 * <li> SupportsEmailAddress - Should be the complete e-mail address of whoever is doing technical support for the application.  Application error messages go here.  For support messages to be sent, it requires Application.email.applicationsEmailAddress to be supplied.</li>
 * <li> JDBCUrl - the URL to be used by all the EOModels when connecting to the database.</li>
 * </ul>
 *  To use the functionality available you may want to set the following page name defaults in your property list:
 * <ul>
 *   <li> <code>DefaultPageName</code> -  (<b>Required</b>)The name of the page to be shown when the requested page can not be shown.</li>
 *   <li> <code>ExceptionPageName</code> - The name of the page to display when an exception occurs. The DefaultPageName is used if not specified. Note that if specified, the page should implement the ExceptionPage interface as defined in GVCJWOExtensions. </li>
 *   <li> <code>PageRestorationErrorPageName</code> - The name of the page to display when the page cannot be restored (A user has clicked 'back' past the page cache). The DefaultPageName is used if not specified.</li>
 *   <li> <code>SessionCreationErrorPageName</code> - The name of the page to display when an exception occurs while creating a session. The DefaultPageName is used if not specified.</li>
 *   <li> <code>SessionRestorationErrorPageName</code> - The name of the page to display when a session cannot be restored (A user's session has timed out). The DefaultPageName is used if not specified.</li>
 *   <li> <code>PageMapFileName</code> - Maps existing WOComponents to other WOComponents that will be used at run time.</li>
 * </ul>
 * <p>Note also that pageWithName has been overridden and your components may need to implement various methods to work as desired (SSL, page security, etc). See that method for documentation.</p>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 51$
 */
abstract public class WOApplication extends ERXAjaxApplication
{
    protected ApplicationProperties properties;
    protected NSMutableArray requiredProperties = new NSMutableArray();
    public static final String PageMapFileNamePropertyKey = "PageMapFileName";
    protected Class editingContextClass;
    protected NSDictionary pageMap = null;     // Map substituting one page to another

    protected  ModelConnector modelConnector;  //To be used in configuring EOModels
    protected EOPrototypeSelector prototypeSwitcher; // Sets prototypes for active database

    private GVCBoolean isDevelopmentMode;
    private Logger logger = LoggerFactory.makeLogger();

    /**  log4j Logger name for the conceptual area of RR loop logging.  This logger name does not match any code.  */
    public static final String RRLoopLoggerName = "com.webobjects.RequestResponseLoop";
    private Logger rrLoopLogger = Logger.getLogger(RRLoopLoggerName);


    public static final String EditingContextClassNamePropertyKey = "EditingContextClassName";
    public static final String DebuggingEditingContextClassNamePropertyKey = "DebuggingEditingContextClassName";
    public static final String DispatchRequestsConcurrentlyPropertyKey = "DispatchRequestsConcurrently";
    public static final String DebugEditingContextLockingPropertyKey = "DebugEditingContextLocking";
    public static final String LogSlowQueriesKey = "LogSlowQueries";
    public static final String LogSlowQueryThesholdInMillisecondsKey = "LogSlowQueryThesholdInMilliseconds";
    public static final String LogFetchedRowsKey = "LogFetchedRows";
    public static final String LogSQLSourceKey = "LogSQLSource";

    public static final String SessionTimeOutInMinutesKey = "SessionTimeOutInMinutes";
    public static final String DefaultFetchTimestampLag = "DefaultFetchTimestampLag";
    public static final String TestingDirectActionsEnabled   = "TestingDirectActionsEnabled";

    public static final String CookieHeaderKey = "cookie";


    /**
     * Convenience method to access properties() statically.  Returns the properies object containing the application configuration.
     *
     * @return the properies object containing the application configuration.
     */
    static public ApplicationProperties appProperties()
    {
        return ((WOApplication) WOApplication.application()).properties();
    }



    /**
     * This constructor sets up the application object using default values from property lists.
     */
    public WOApplication()
    {
        super();

        /* WOFrameworksBaseURL and WOApplicationBaseURL properties are broken in 5.4.
           This is the workaround from Project Wonder.  This goes along with the
           in the woapplication Ant task when fully embedding frameworks:

           <woapplication name="${build.app.name}"  stdFrameworks="false"
                ...
                frameworksBaseURL="/WebObjects/${build.app.name}.woa/Frameworks">
         */
        frameworksBaseURL();
        applicationBaseURL();
        if (System.getProperty("WOFrameworksBaseURL") != null) {
            setFrameworksBaseURL(System.getProperty("WOFrameworksBaseURL"));
        }
        if (System.getProperty("WOApplicationBaseURL") != null) {
            setApplicationBaseURL(System.getProperty("WOApplicationBaseURL"));
        }

        // Add WOForm replacement needed for some Ajax components
        ERXPatcher.setClassForName(ERXWOForm.class, "WOForm");

        // Redirect System.out and System.err to NSLog.out
        System.setOut(((NSLog.PrintStreamLogger)NSLog.out).printStream());
        System.setErr(((NSLog.PrintStreamLogger)NSLog.out).printStream());

        // Dump the system properties and classpath to aid in debugging
        logger.info(Debug.dumpJVMConfiguration());

        initializeLog4J();

        // Calls willFinishLaunching(NSNotification) when ApplicationWillFinishLaunchingNotification is broadcast
        NSNotificationCenter.defaultCenter().addObserver(this, new NSSelector("willFinishLaunching", ERXConstant.NotificationClassArray), WOApplication.ApplicationWillFinishLaunchingNotification, null);
    }



    /**
     * Notification method called when the application posts the notification
     * {@link WOApplication#ApplicationWillFinishLaunchingNotification}. This method calls
     * initializeApplicationConfiguration() and installs the ApplicationDidDispatchRequestNotification
     * handler.
     *
     * @see #initializeApplicationConfiguration()
     * @see #applicationDidHandleRequest(NSNotification)
     *
     * @param n notification that is posted just before the WOApplication has finished launching
     * and is ready for accepting requests
     */
    public void willFinishLaunching(NSNotification n)
    {
        initializeApplicationConfiguration();
        NSNotificationCenter.defaultCenter().addObserver(this,
                                                         ERXSelectorUtilities.notificationSelector("applicationDidHandleRequest"),
                                                         WOApplication.ApplicationDidDispatchRequestNotification, null);
    }



    /**
     * Initializes log4j with configuration referenced by log4jConfigUrl().  Changes NSLog to use log4j for output.
     * if log4jConfigUrl() returns null, log4j is not activated.
     * <p>
     * This method does not use <code>NSLog.Log4JLogger</code> to direct NSLog messages to log4j.  <code>NSLog.Log4JLogger</code>
     * either has a bug or a conflict with more recent versions of log4j that makes it always report that LogLevelDetailed is on.
     * This causes a lot of extra processing and output.  This method uses <code>er.extensions.ERXNSLogLog4jBridge</code> to direct
     * NSLog messages to log4j.  This requires that the configuration returned by log4jConfigUrl() defines the <code>NSLog</code> logger.
     */
    protected void initializeLog4J()
    {
        URL configURL = log4jConfigUrl();
        if (configURL != null)
        {
            logger.info("Initializing log4j from " + configURL.toString());
            if (configURL.toString().endsWith(".xml"))
            {
                DOMConfigurator.configure(configURL);
            }
            else
            {
                PropertyConfigurator.configure(configURL);
            }

            int allowedLevel = NSLog.debug.allowedDebugLevel();
            NSLog.setOut(new ERXNSLogLog4jBridge(ERXNSLogLog4jBridge.OUT));
            NSLog.setErr(new ERXNSLogLog4jBridge(ERXNSLogLog4jBridge.ERR));
            NSLog.setDebug(new ERXNSLogLog4jBridge(ERXNSLogLog4jBridge.DEBUG));
            NSLog.debug.setAllowedDebugLevel(allowedLevel);
        }
    }



    /**
     * Returns the URL to the log4j configuration (either .properties or .xml).  The default is to return null which results in
     * log4j not being active.  This method can be implemented as:
     * <pre>
     * return resourceManager().pathURLForResourceNamed("log4j.xml", null, null);
     * </pre>
     * @return URL to log4j configuration or null if log4j should not be configured.
     */
    protected URL log4jConfigUrl()
    {
        return resourceManager().pathURLForResourceNamed("log4j.xml", "GVCWOExtensions", null);
        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Loads the application configuration information and configures the debugging and WebObjects environment. The initialization is performed in the following sequence of method calls, where each method can be overriden to add customizations. <br>
     * Called when ApplicationWillFinishLaunchingNotification is broadcast.
     * <b>Steps performed during initialization:</b>
     * <ol>
     * <li>loadConfigurationInfo()</li>
     * <li>performConfigurationDiagnostics()</li>
     * <li>configureDatabaseConnections()</li>
     * <li>configureWebObjectsEnvironment()</li>
     * <li>createFormatters()</li>
     * <li>migrate schema</li>
     * </ol>
     * <br>
     * <b>IMPORTANT:</b> Read notes/instructions carefully on overriding each of these methods to ensure proper initialization.
     */
    protected void initializeApplicationConfiguration()
    {
        logger.info("Initializing Application Configuration...");

        loadConfigurationInfo();
        loadCommandLineOverrides();
        loadPageMap();
        loadPropertyDependantConfiguration();

        // For backwards compatibility, use this property to set the logger level
        if (properties().booleanPropertyForKey("DebugRequestResponseLoop"))
        {
            rrLoopLogger.setLevel(Level.DEBUG);
        }

        if ( ! isTerminating())
        {
            performConfigurationDiagnostics();
        }

        if ( ! isTerminating())
        {
                configureDatabaseConnections(modelsToIgnore());
        }

        if ( ! isTerminating())
        {
                configureWebObjectsEnvironment();
        }

        if ( ! isTerminating())
        {
            // Create formatters for all the date formats to work around a bug in WO 5.1 where there is no time zone set on a formatter by default.
            createFormatters();
        }

        if ( ! isTerminating())
        {
            logger.info("Checking for needed migation");
            if (ERXMigrator.shouldMigrateAtStartup())
            {
                try
                {
                    migrator().migrateToLatest();
                }
                catch (ERXMigrationFailedException e)
                {
                    // It might be a missing plugin problem
                    new ERXJDBCConnectionAnalyzer(databaseConnectionDictionary());
                    throw e;
                }
            }
        }

        if ( ! isTerminating())
        {
            logger.info("Application Configuration Initialized");
        }
        else
        {
            logger.fatal("\n*** SHUTDOWN *** Application shutting down due to configuration issues above.\n");
        }
    }



    /**
     * Loads the application configuration information.  Subclasses can override this method to add more properties. <br>
     * IMPORTANT: When overriding this method, make sure that you call <code>super</code> before doing anything else, to initialize the list and load all the other framework defaults. <br>
     *
     * In example: <br>
     * <code><pre>
     * protected void loadConfigurationInfo()
     * {
     *     super.loadConfigurationInfo();
     *     properties().addPropertiesFromFile(aFile, aFramework);  //to load a plist file
     *     properties().addPropertyForKey(anObject, aKey);  //to load a single property
     * }
     * </pre></code>
     * <br>
     * Take note of the order you load the properties, since properties loaded later override properties loaded earlier.
     */
    protected void loadConfigurationInfo()
    {
        try
        {
            properties = new ApplicationProperties();

            // Convenience default
            properties().addPropertyForKey(cgiAdaptorURL() + "/" + name(), "FrontDoorURL");

            //pre-load all GVC framework property lists which include this framework
            NSArray frameworkNames = (NSArray) NSBundle.frameworkBundles().valueForKey("name");
            Enumeration frameworkNameEnumerator = frameworkNames.objectEnumerator();
            while (frameworkNameEnumerator.hasMoreElements())
            {
                String frameworkName = (String) frameworkNameEnumerator.nextElement();
                if (frameworkName.startsWith("GVC"))
                {
                    properties.addPropertiesFromFile(frameworkName + ".plist", frameworkName);
                }
            }
        }
        catch (java.lang.IllegalArgumentException e)
        {
            logger.fatal("Terminating: Load properties failed with exception: " + e.getMessage());
            terminate();
        }
    }



    /**
     * Adds any properties from the command line and the Properties file to the application properties.
     * This is done after loadConfigurationInfo() so it can be used to override properties loaded that way.
     * if you don't want that to happen, you can override this and not call super.
     */
    protected void loadCommandLineOverrides()
    {
        Properties sysProperties = System.getProperties();
        Enumeration propertyNames = sysProperties.propertyNames();
        while (propertyNames.hasMoreElements())
        {
            String propertyName = (String) propertyNames.nextElement();
            properties().addPropertyForKey(sysProperties.getProperty(propertyName), propertyName);
        }
    }



    /**
     * Loads the page mapping, if any.  The name of the page map file is taken from pageMapFileName().
     * It is loaded from the configurationFrameworkName() if specified, otherwise from the application bundle.
     * This method can be overridden if a different method of loading is needed.
     *
     * @see #pageMapFileName()
     * @see #configurationFrameworkName()
     */
    protected void loadPageMap()
    {
            pageMap = new NSDictionary();

            String pageMapFileName = pageMapFileName();
            if (pageMapFileName != null)
            {
                WOResourceManager rm = WOApplication.application().resourceManager();

                // Try this first as bytesForResourceNamed throws a null pointer exception if the file cannot be found.
            java.io.InputStream resourceStream = rm.inputStreamForResourceNamed(pageMapFileName, configurationFrameworkName(), null);
            if (resourceStream != null)
            {
                byte[] bytesFromResource = rm.bytesForResourceNamed(pageMapFileName, configurationFrameworkName(), null);
                pageMap = (NSDictionary) NSPropertyListSerialization.propertyListFromData(new NSData(bytesFromResource), "US-ASCII");
                logger.info("Loaded page map " + pageMapFileName + ".plist from " + (configurationFrameworkName() == null ? "Application" : configurationFrameworkName())) ;
            }
            else
            {
                logger.fatal("Terminating: Failed to find page map " + pageMapFileName + ".plist from " + (configurationFrameworkName() == null ? "Application" : configurationFrameworkName())) ;
                terminate();
            }
        }
    }



    /**
     * Returns the name of the file that defines the page mapping, if any. This implementation uses the
     * value of PageMapFileNamePropertyKey.
     *
     * @return he name of the file that defines the page mapping or null if pages are not mapped
     *
     * @see #PageMapFileNamePropertyKey
     * @see #pageMapFileName()
     * @see #configurationFrameworkName()
     */
    public String pageMapFileName()
    {
        return properties.stringPropertyForKey(PageMapFileNamePropertyKey);
    }



    /**
     * Loads application configuration that is dependant on all the properties being loaded.  Subclasses can override this method to load additional configuration. <br>
     */
    protected void loadPropertyDependantConfiguration()
    {
        try
        {
            // get the page map, if one is specified in the config
            if (properties().hasPropertyForKey(PageMapFileNamePropertyKey))
            {
                String pageMapFileName = properties().stringPropertyForKey(PageMapFileNamePropertyKey);
                if (NSBundleAdditions.tableExistsWithName(NSBundle.mainBundle(), pageMapFileName))
                {
                    pageMap = NSBundleAdditions.tableWithName(NSBundle.mainBundle(), pageMapFileName);
                }
            }

            if (developmentMode())
            {
                // Set some default for Wonder properties
                if ( ! properties.hasPropertyForKey("er.component.clickToOpen"))
                {
                    properties.addPropertyForKey(Boolean.TRUE, "er.component.clickToOpen");
                }
                if ( ! properties.hasPropertyForKey("er.extensions.ERXApplication.developmentMode"))
                {
                    properties.addPropertyForKey(Boolean.TRUE, "er.extensions.ERXApplication.developmentMode");
                }
            }
        }
        catch (java.lang.IllegalArgumentException e)
        {
            logger.fatal("Terminating: Load property dependant configuration failed with exception: " + e.getMessage());
            terminate();
        }
    }


    /**
     * Configures all EOModels in this project, using ModelConnector, so it can be updated with the proper connection dictionary
     * (from the configuration files) when they are loaded.
     *
     * @param modelsToIgnore list of models to not connect
     */
    protected void configureDatabaseConnections(NSArray modelsToIgnore)
    {
        /** require [valid_model_list] modelsToIgnore != null;  **/

        logger.info("Configuring for a " + databaseType() + " database");
        logger.info("Connecting with " + databaseConnectionDictionary());
        prototypeSwitcher = new EOPrototypeSelector(databaseType());
        prototypeSwitcher.installModelAddedListener();

        // A bogus trace is output by the constructor of EODatabaseContext.  Ignore it.
        if (NSLog.debugLoggingAllowedForGroups(NSLog.DebugGroupMultithreading))
        {
            logger.info("***** Ignore the 'access without lock' trace below, it is not a real locking problem.");
        }

        modelConnector = new ModelConnector(databaseConnectionDictionary(), modelsToIgnore);

        /* Ensure that we have a EOJDBCPrototypes entity or none of the DB will work.  We need
         * to check this here and not earlier so that the EOPrototypeSelector and ModelConnector
         * can catch the model added notification.  Otherwise this could go in
         * perfromConfigurationDiagnostics().
         */
        if (EOModelGroup.defaultGroup().entityNamed("EOJDBCPrototypes") == null)
        {
            terminate();    // It is required do not proceed with launching the application.
            logger.fatal("No EOJDBCPrototypes entity found.  Is the Prototypes framework missing?");
        }

        /** ensure [modelConnector_no_null] modelConnector != null; **/
    }



    /**
     * Returns array of models (models for testing in this case) to not attempt to connect to.
     * This can be overriden and additional models added to the list.
     *
     * @return array of models to not attempt to connect to
     */
    public NSArray modelsToIgnore()
    {
        return new NSArray(new Object[]{"TestModel1", "TestModel2", "TestModel3", "GVCJEOFValidationTest",
                                        "GVCGenericObjectsTest"});
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the database type (e.g. DB2, Oracle, FrontBase, OpenBase, mySQL etc.) that is to be
     * used.  This must match the name as used in the EO<database name>Prototypes entity for this
     * database in the EOModel.  Remember - case counts!
     *
     * @return the database type that is to be used for this test run
     */
     public String databaseType()
    {
        return properties().stringPropertyForKey("DBType");

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the connection dictionary to use for all EOModels.  This is built from information in the configuration files.  A connection dictionary looks like this;
     * <br><br>
     * <pre>
     * connectionDictionary = {
     *          URL = "jdbc:oracle:thin:@OTTAWA:1521:GVC";
     *          driver = "";
     *          password = smaker2;
     *          plugin = "";
     *          username = smaker2;
     * };
     * </pre>
     *
     * @return the connection dictionary to use for all EOModels.
     */
    public NSDictionary databaseConnectionDictionary()
    {
        NSMutableDictionary connectionDictionary = new NSMutableDictionary();

        connectionDictionary.setObjectForKey(properties().stringPropertyForKey("JDBCUrl"), "URL");
        connectionDictionary.setObjectForKey(properties().stringPropertyForKey("JDBCDriver"), "driver");
        connectionDictionary.setObjectForKey(properties().stringPropertyForKey("DBPassword"), "password");
        connectionDictionary.setObjectForKey(properties().stringPropertyForKey("EOFPlugin"), "plugin");
        connectionDictionary.setObjectForKey(properties().stringPropertyForKey("DBUserName"), "username");

        return connectionDictionary;

        /** ensure
        [valid_result] Result != null;
        [has_url] Result.objectForKey("URL") != null;
        [has_username] Result.objectForKey("username") != null;
        [has_password] Result.objectForKey("password") != null; **/
    }



    /**
     * Perform diagnostic checks to ensure that the application configuration information is complete and reasonable.  The application is marked for temination if the configuration is not complete and reasonable.<br>
     * To add add more properties to require, override this method, but don't forget to call super at the end. Below is an example: <br>
     * <code><pre>
     * protected void performConfigurationDiagnostics()
     * {
     *     requiredProperties().addObjectsFromArray(<array of additional properties names you want to require>);
     *     super.performConfigurationDiagnostics();
     * }
     * </pre></code>
     *
     * Note: Aside from overriding this method, you can also just call requireProperty(String) anytime to require a single property.
     */
    protected void performConfigurationDiagnostics()
    {
        //add default requiredProperties
        requiredProperties().addObjectsFromArray(new NSArray(new Object[] {
                        "WOAdaptorURL",
                        SessionTimeOutInMinutesKey,
                        "SessionTerminateTimeOutInSeconds",
                        "PageCacheSize",
                        "PermanentPageCacheSize",
                        "CachingEnabled",
                        "DocumentRootPath",
                        "ErrorsTitle",
                        "DBType",
                        "JDBCUrl",
                        "DBUserName",
                        "DBPassword",
                        "JDBCDriver",
                        "EOFPlugin",
                        "DateFormat",
                        "TimeFormat",
                        "DateTimeFormat",
                        "IntegerFormat",
                        "NumberFormat",
                        "CurrencyFormat",
                        "DevelopmentMode",
                        "DefaultPageName",
                        DispatchRequestsConcurrentlyPropertyKey,
                        EditingContextClassNamePropertyKey,
                        DefaultFetchTimestampLag,
                        DebuggingEditingContextClassNamePropertyKey,
                        DebugEditingContextLockingPropertyKey,
                        LogSlowQueriesKey,
                        LogSlowQueryThesholdInMillisecondsKey,
                        LogFetchedRowsKey,
                        LogSQLSourceKey
                        }));

        Enumeration propertiesEnumerator = requiredProperties().objectEnumerator();
        while (propertiesEnumerator.hasMoreElements())
        {
            String requiredProperty = (String) propertiesEnumerator.nextElement();
            requireProperty(requiredProperty);
        }

        if (properties().booleanPropertyForKey("DevelopmentMode"))
        {
            requireProperty("DeveloperID");
            requireProperty("DeveloperPassword");
            requireProperty("DeveloperEmail");
        }
    }



    /**
     * Checks to see if the named property exists.  Marks the application for termination and prints a message if it does not.  This is for use by performConfigurationDiagnostics.
     *
     * @param propertyName the name of the property to require
     */
    protected void requireProperty(String propertyName)
    {
        /** require propertyName != null; **/

        if ( ! properties().hasPropertyForKey(propertyName))
        {
            terminate();    // It is required do not proceed with launching the application.
            logger.fatal("Fatal: " +propertyName + " is not defined in application configuration information (e.g. ApplicationName.plist etc.)");
        }
    }



    /**
     * Checks to see if the named page is mapped.  Marks the application for termination and
     * prints a message if it does not.  This is for use by performConfigurationDiagnostics.
     */
    protected void requireMappingForPage(String pageName)
    {
        if (pageMap().objectForKey(pageName) == null)
        {
            terminate();    // It is required do not proceed with launching the application.
            logger.fatal(pageName + " is not mapped in the page map file (e.g. GVCSiteMakerPageMap.plist etc.)");
        }
    }



    /**
     * Sets up the WebObjects application environment.  IMPORTANT: When overriding this method, be sure to call super before doing anything else.
     */
    protected void configureWebObjectsEnvironment()
    {
        setAllowsConcurrentRequestHandling(appProperties().booleanPropertyForKey(DispatchRequestsConcurrentlyPropertyKey));
        logger.info("Application is handling requests concurrently " + isConcurrentRequestHandlingEnabled());

        // Initialize WO Ognl
        WOOgnl.factory();

        // What a mess!  Why did we decide to rename these properties?
        String smtpHost = properties().stringPropertyForKey("SMTPHost");
        if (smtpHost == null)
        {
            smtpHost = SMTPHost();
        }
        if (smtpHost == null)
        {
            smtpHost = System.getProperty("mail.smtp.host");
        }
        if (smtpHost != null)
        {
            // OK, let's get everyone on the same page
            properties.addPropertyForKey(smtpHost, "SMTPHost");
            setSMTPHost(smtpHost);
            System.setProperty("mail.smtp.host", smtpHost);
            System.setProperty("mail.transport.protocol", "smtp");

            // Please use ThreadedMailAgent instead of this
            MailDelivery.createInstance(SMTPHost());
        }


        // Direct connect is evil
        System.setProperty("WODirectConnectEnabled", java.lang.Boolean.FALSE.toString());

        // Set the default encoding to be UTF-8
        WOMessage.setDefaultURLEncoding("UTF-8");
        WOMessage.setDefaultEncoding("UTF-8");

        // This is needed for DA user search URLs that include Unicode search values
        WORequest.setDefaultURLEncoding("UTF-8");

        setPageCacheSize(properties().intPropertyForKey("PageCacheSize"));
        setPermanentPageCacheSize(properties().intPropertyForKey("PermanentPageCacheSize"));
        setCachingEnabled(properties().booleanPropertyForKey("CachingEnabled"));

        setSessionTimeOut(new Integer(60 * properties().intPropertyForKey(SessionTimeOutInMinutesKey)));

        // Uses the standard WOAdaptorURL if the old name is not defined
        if (properties().stringPropertyForKey("CGIAdaptorURL") != null)
        {
            logger.error("**** CGIAdaptorURL is deprecated, please use WOAdaptorURL. ****");
            terminate();
        }

        // This enables direct actions, thus allowing the mainPage to be renamed. The default request
        // handler must be changed to avoid a bug which is manifested by a new session being created
        // when the request URI is of the from http://www.myserver.com/cgi-bin/WebObjects/MyApp.woa
        // even though there is a wosid cookie with a valid, unexpired session ID in the request.
        // Possibly related is that request.sessionID() returns null in this case, while works as
        // expected for URLs which have a request handler key.  The WORequest docs state that sessionID()
        // "Returns the session ID, or null if no session ID is found. This method first looks for the
        // session ID in the URL, then checks the form values, and finally checks to see if the session
        // ID is stored in a cookie."  This does not seem to be happening.
        setDefaultRequestHandler(requestHandlerForKey(directActionRequestHandlerKey()));

        // Query logging
        if (properties().booleanPropertyForKey(LogSlowQueriesKey) ||
            properties().booleanPropertyForKey(LogFetchedRowsKey) ||
            properties().booleanPropertyForKey(LogSQLSourceKey))
        {
            SQLLoggingAdaptorChannelDelegate.installDelegate(properties().booleanPropertyForKey(LogSlowQueriesKey),
                                                             properties().longPropertyForKey(LogSlowQueryThesholdInMillisecondsKey),
                                                             properties().booleanPropertyForKey(LogFetchedRowsKey),
                                                             properties().booleanPropertyForKey(LogSQLSourceKey));
        }

        // Set EditingContext snapshot timeout so that data is frequently refetched to compensate for multiple instances.
        EOEditingContext.setDefaultFetchTimestampLag( properties().intPropertyForKey(DefaultFetchTimestampLag) );
        logger.info("EC DefaultFetchTimestampLag is set to " + (EOEditingContext.defaultFetchTimestampLag()/1000) + " seconds" );

    }



    /**
     * Creates formatters for each date and number format and removes the format strings so that they do not get used by accident.
     */
    protected void createFormatters()
    {
        //date formatters
        properties().createDateFormatter("DateFormat");
        properties().createDateFormatter("TimeFormat");
        properties().createDateFormatter("DateTimeFormat");

        //number formatters
        properties().createNumberFormatter("IntegerFormat");
        properties().createNumberFormatter("NumberFormat");
        properties().createNumberFormatter("CurrencyFormat");
    }



    /**
     * WO 5.4 version.  Creates and returns a request configured to use UTF-8 for the <code>defaultFormValueEncoding()</code>.
     * This is done here as doing it in takeValueFromRequest was too late when the form used
     * <code>enctype="multipart/form-data</code>.  Multi-part form data triggers the new streaming
     * handling and changing the encoding after the stream has been read discards the form values.
     * This might also have been because some of our code peeked at the form values.
     */
    public WORequest createRequest(String aMethod, String aURL, String anHTTPVersion,
                                   Map someHeaders, NSData aContent,
                                   Map someInfo)
    {
        // Workaround for Safari on Leopard bug (post followed by redirect to GET incorrectly has content-type header).
        // The content-type header makes the WO parser only look at the content. Which is empty.
        // http://lists.macosforge.org/pipermail/webkit-unassigned/2007-November/053847.html
        // http://jira.atlassian.com/browse/JRA-13791
        if ("GET".equalsIgnoreCase(aMethod) && someHeaders != null && someHeaders.get("content-type") != null)
        {
            someHeaders.remove("content-type");
        }

        WORequest newRequest = new Request(
                aMethod,
                aURL,
                anHTTPVersion,
                someHeaders == null ? null : new NSDictionary(someHeaders),
                aContent,
                someInfo == null ? null : new NSDictionary(someInfo));
        newRequest.setDefaultFormValueEncoding("UTF-8");
        return newRequest;
    }


    /**
     * WO 5.3 and prior version Creates and returns a request configured to use UTF-8 for the <code>defaultFormValueEncoding()</code>.
     * This is done here as doing it in takeValueFromRequest was too late when the form used
     * <code>enctype="multipart/form-data</code>.  Multi-part form data triggers the new streaming
     * handling and changing the encoding after the stream has been read discards the form values.
     * This might also have been because some of our code peeked at the form values.
     */
    public WORequest createRequest(String aMethod,
                                   String aURL,
                                   String anHTTPVersion,
                                   NSDictionary someHeaders,
                                   NSData aContent,
                                   NSDictionary someInfo)
    {
        // Workaround for Safari on Leopard bug (post followed by redirect to GET incorrectly has content-type header).
        // The content-type header makes the WO parser only look at the content. Which is empty.
        // http://lists.macosforge.org/pipermail/webkit-unassigned/2007-November/053847.html
        // http://jira.atlassian.com/browse/JRA-13791
        if ("GET".equalsIgnoreCase(aMethod) && someHeaders != null && someHeaders.objectForKey("content-type") != null)
        {
            someHeaders = someHeaders.mutableClone();
            ((NSMutableDictionary)someHeaders).removeObjectForKey("content-type");
        }

        WORequest newRequest = new Request(aMethod, aURL, anHTTPVersion, someHeaders, aContent, someInfo);
        newRequest.setDefaultFormValueEncoding("UTF-8");
        return newRequest;
    }



    /**
     * When a context is created we push it into thread local storage.
     *
     * @see #applicationDidHandleRequest(NSNotification)
     * @param request the request
     * @return the newly created context
     */
    public WOContext createContextForRequest(WORequest request) {
        WOContext context = super.createContextForRequest(request);
        // We only want to push in the context the first time it is
        // created, ie we don't want to lose the current context
        // when we create a context for an error page.
        if (ERXWOContext.currentContext() == null) {
            ERXWOContext.setCurrentContext(context);
        }
        return context;
    }



    /**
     * When request is finished, we remove the context from thread local storage.
     *
     * @see #createContextForRequest(WORequest)
     * @param n notification
     */
    public void applicationDidHandleRequest(NSNotification n) {
        ERXWOContext.setCurrentContext(null);
        ERXThreadStorage.removeValueForKey(ERXWOContext.CONTEXT_DICTIONARY_KEY);
    }



    /**
     * @see Response
     * @return Response instead of WOResponse
     */
    public WOResponse createResponseInContext(WOContext context)
    {
        return new Response(context);
    }



    /**
     * Overidden to provide debugging hooks.
     */
    public WOResponse dispatchRequest(WORequest request)
    {
        /** require [valid_param] request != null; **/

        /*
         * Hack for cooking parsing bug in WO 5.2
         * WO Cookie parser actually handles empty values as long as they are not
         * the last value in the list, because often there is no trailing  semi-colon.
         * The easiest fix is as follows and results in the expected behavior:
         * CookieParser: Found a null cookie value in: 'woinst=-1; wosid=;'. Will
         * continue by setting value to the empty string.  WHEREAS, confound it,
         * I simply could not get  WOApplication.handleMalformedCookieString()
         * to correct the situation because you don't have access to the request
         * object, and modifying the mutable dictionary simply had no effect.
         * Christopher Legan, cklegan@king.net, Kiwi InterNet Group, Inc.
         */
        String cookieHeader = request.headerForKey(CookieHeaderKey);
        if (cookieHeader != null && ! cookieHeader.endsWith(";"))
        {
            request.setHeader(cookieHeader.concat(";"), CookieHeaderKey);
        }

        WOResponse response;
        boolean isntResourceRequest = request.uri().indexOf("/wr") == -1;        // Don't log image requests
        if (isntResourceRequest && rrLoopLogger.isInfoEnabled())
        {
            // Debugging code for the Request - Response loop.
            rrLoopLogger.info("=========== Cycle Start for URI " + request.uri());
            rrLoopLogger.debug("Request's cookies: " + request.headerForKey(CookieHeaderKey));
            rrLoopLogger.info("          form values: " + request.formValues());
            if (rrLoopLogger.isTraceEnabled()) rrLoopLogger.trace("Request's headers: " + request.headers());
            NSTimestamp startTime = new NSTimestamp();

            response = super.dispatchRequest(request);
            NSTimestamp stopTime = new NSTimestamp();
            long milliseconds = stopTime.getTime() - startTime.getTime();

            //* Cookie and session debugging code.
            rrLoopLogger.debug("Response cookies " + response.cookies());
            if (rrLoopLogger.isTraceEnabled()) rrLoopLogger.trace("Response headers: " + response.headers());
            rrLoopLogger.info("," + request.uri() + ", - elapsed time: ," + (milliseconds / 1000.0) );
            rrLoopLogger.info("\n");
        }
        else
        {
            response = super.dispatchRequest(request);
        }

        /*
         * From: Gary Teter <bigdog@wirehose.com>
         * Date: December 7, 2008 12:44:23 PM PST (CA)
         * To: Development WebObjects <webobjects-dev@lists.apple.com>
         * Subject: Re: [SOLVED] Baffling problem with foreign key
         *
         * I finally figured out what's going on, and I think it's a fundamental flaw in WebObjects. Fortunately Ralf's suggestion is a good-enough workaround:
         *
         *      A workaround may be to call "EOObserverCenter.notifyObserversObjectWillChange(null)" at strategic places like in Application.dispatchRequest().
         *
         * What's supposed to happen: You change an attribute on an eo, which calls willChange(), which calls EOObserverCenter.notifyObserversObjectWillChange(this).
         * The observer center notes that this was the most recently observed object for this thread, and calls editingContext.objectWillChange. The ec then grabs
         * the current snapshot of the object, and then the attribute is finally changed in the eo. Any further calls to willChange >in the current thread< on the
         * eo are ignored by the EOObserverCenter. When the ec saves changes, it calls notifyObserversObjectWillChange(null) so any future changes to the eo get
         * noticed again.
         *
         * How it fails: A request is handled by WorkerThread0. By the end of the request the eo has been modified but not saved, so the EOObserverCenter remembers
         * that WorkerThread0's most recent object is that eo. Fifteen more requests are handled by WorkerThreads 1-15 in sequence. One of these requests completes
         * the modification of the eo and calls saveChanges on the ec. At this point the ec tells the EOObserverCenter to forget about its most recent object, but
         * it's being set to null in WorkerThread14 or whatever, not WorkerThread0.
         *
         * The next request will wrap around to to be handled by WorkerThread0. This request modifies an attribute on the eo, but since the EOObserverCenter still
         *  thinks WorkerThread0 has already noticed the eo, it ignores the willChange and the ec doesn't grab a snapshot.
         *
         * Later in the processing of this request, a different object gets changed, willChange gets called and the ec grabs a snapshot of the second object. Then,
         * a change gets made to the original eo, willChange gets called, and since the EOObserverCenter was paying attention to the second object, it goes ahead
         * and notifies the ec about the first object.
         *
         * At this point the ec grabs a snapshot of the first object, but it's too late -- the object has already been modified, the ec didn't know about the
         * previous change, so when saveChanges gets called the previous changes don't get saved to the database. And now your object graph no longer matches the
         * database, and your app is borked.
         */
        EOObserverCenter.notifyObserversObjectWillChange(null);

        return response;
    }



    /**
     * This method returns the URL that is used to access the default page for this application.
     * This can be used to re-enter the application without a session.
     *
     * @return the URL that is used to access the default page for this application. This can be used to re-enter the application without a session
     */
    public String frontDoorURL()
    {
        return properties().stringPropertyForKey("FrontDoorURL");
    }



    /**
     * Convenience method to obtain the document root path
     *
     * @return the path for DocumentRoot from CustomInfo.plist
     */
    public String documentRootPath()
    {
        return properties().stringPropertyForKey("DocumentRootPath");
    }



    /**
     * The name of the page to be shown when the requested page can not be shown. This method must be overridden in the application
     * This is the default page to show for
     * <ul>
     *         <li> handlePageRestorationError</li>
     *         <li> handleSessionCreationError</li>
     *         <li> handleSessionRestorationError</li>
     *         <li> handleException</li>
     * </ul>
     *
     * @return the name of the page to display
     */
    public String defaultPageName()
    {
        return properties().stringPropertyForKey("DefaultPageName");

        /** ensure [valid_result] Result != null; [result_has_nonzero_length] Result.length() > 0; **/
    }



    /**
     * The name of the page to be shown by handlePageRestorationErrorInContext().  The default is defaultPage(). Override this method to provide different functionality in your application.
     *
     * @return the name of the defaultPage() which will be shown next
     */
    public String pageRestorationErrorPageName()
    {
        return properties().hasPropertyForKey("PageRestorationErrorPageName") ? properties().stringPropertyForKey("PageRestorationErrorPageName") : defaultPageName();

        /** ensure [valid_result] Result != null; [result_has_nonzero_length] Result.length() > 0; **/
    }



    /**
     * This method defines the response for handling backtracking beyond the page cache.
     *
     * @param aContext The context to use for obtaining the page
     * @return the response from the next page to display
     */
    public WOResponse handlePageRestorationErrorInContext(WOContext aContext)
    {
        /** require [valid_param] aContext != null; **/

        WOComponent errorPage = pageWithName(pageRestorationErrorPageName(), aContext);
        WOResponse response = null;
        if (AjaxUtils.isAjaxRequest(aContext.request()))
        {
            AjaxUtils.redirectTo(errorPage);
            response = errorPage.context().response();
            aContext.session().savePage(errorPage);
        }
        else
        {
            response = errorPage.generateResponse();
        }

        response.setStatus(500);
        return response;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overridden to log out messages when cookie parsing fails.
     */
    public NSMutableDictionary handleMalformedCookieString(RuntimeException exception,
                                                           String cookieString,
                                                           NSMutableDictionary parsedCookies)
    {
        logger.info("Error parsing cookieString: '" + cookieString + "'");
        logger.info("      exception: " + exception);

        return parsedCookies;
    }



    /**
     * Name of the page to display when an error occurred while creating a session. Override this method to provide different functionality in your application.
     *
     * @return the name of the defaultPage() which will be shown next
     */
    public String sessionCreationErrorPageName()
    {
        return properties().hasPropertyForKey("SessionCreationErrorPageName") ? properties().stringPropertyForKey("SessionCreationErrorPageName") : defaultPageName();

        /** ensure
        [valid_result] Result != null;
        [result_has_nonzero_length] Result.length() > 0; **/
    }



    /**
     * This method defines the response for handling a session creation errors.
     *
     * @param aContext The context to use for obtaining the page
     * @return the response from the next page to display
     */
    public WOResponse handleSessionCreationErrorInContext(WOContext aContext)
    {
        /** require [valid_param] aContext != null; **/

        WOComponent errorPage = pageWithName(sessionCreationErrorPageName(), aContext);
        WOResponse response = null;
        if (AjaxUtils.isAjaxRequest(aContext.request()))
        {
            AjaxUtils.redirectTo(errorPage);
            response = errorPage.context().response();
            aContext.session().savePage(errorPage);
        }
        else
        {
            response = errorPage.generateResponse();
        }
        response.setStatus(500);
        return response;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Name of the page to display when a session restoration has failed.  Override this method to provide different functionality in your application.
     *
     * @return the name of the page which will be shown when a session resoration has failed
     */
    public String sessionRestorationErrorPageName()
    {
        return properties().hasPropertyForKey("SessionRestorationErrorPageName") ? properties().stringPropertyForKey("SessionRestorationErrorPageName") : defaultPageName();

        /** ensure
        [valid_result] Result != null;
        [result_has_nonzero_length] Result.length() > 0; **/
    }



    /**
     * This method defines the response for handling an error that occurred when restoring a session
     *
     * @param aContext The context to use when creating the response.
     * @return the response
     */
    public WOResponse handleSessionRestorationErrorInContext(WOContext aContext)
    {
        /** require [valid_param] aContext != null; **/

        WOComponent errorPage = pageWithName(sessionRestorationErrorPageName(), aContext);
        WOResponse response = null;
        if (AjaxUtils.isAjaxRequest(aContext.request()))
        {
            AjaxUtils.redirectTo(errorPage);
            response = errorPage.context().response();
            aContext.session().savePage(errorPage);
        }
        else
        {
            response = errorPage.generateResponse();
        }
        response.setStatus(500);
        return response;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * This method determines the page to be shown by handleExceptioninContext. The default is to return null which results in the standard WebObjects exception page. Override this method to show a custom error page.
     *
     * @return the default page being used for handleException.
     */
    public String exceptionPageName()
    {
        return properties().stringPropertyForKey("ExceptionPageName");
    }



    /**
     * Replaces the default WO Exception page.  The page is determined from exceptionPageName.  If you implement a customized exception page, then it must implement the ExceptionPage interface Joee: test if this works
     *
     * @param anException the exception to handle
     * @param aContext the context
     * @return the response to the exception
     */
    public WOResponse handleException(Exception anException, WOContext aContext)
    {
        /** require
        [valid_anException_param] anException != null;
        [valid_aContext_param] aContext != null; **/

        WOResponse aResponse = null;

        // Handle java.lang.VirtualMachineError (probably OutOfMemory) by terminating the app abruptly.
        if ( (anException instanceof NSForwardException) &&
             (((NSForwardException) anException).originalException() instanceof VirtualMachineError) )
        {
            Runtime.getRuntime().exit(1);
        }

        // Unwrap NSForwardException to get to the actual exception
        while (anException instanceof NSForwardException)
        {
            Throwable t = ((NSForwardException)anException).originalException();
            anException = (t instanceof Exception) ? (Exception)t : new Exception(t);
        }

        try
        {
            if (exceptionPageName() != null)
            {
                WOComponent exceptionPage = pageWithName(exceptionPageName(), aContext);

                java.lang.Class exceptionPageInterFace = ExceptionPage.class;

                // Make sure the exception page conforms to the ExceptionPage interface
                if (exceptionPageInterFace.isAssignableFrom(exceptionPage.getClass()))
                {
                    NSMutableDictionary extendedUserInfo = new NSMutableDictionary();

                    if (aContext.component() != null)
                    {
                        extendedUserInfo.setObjectForKey(aContext.component().name(), "Component-Name");
                    }
                    if (aContext.page() != null)
                    {
                        extendedUserInfo.setObjectForKey(aContext.page().name(), "Page-Name");
                    }
                    if (aContext.request() != null)
                    {
                        extendedUserInfo.setObjectForKey(aContext.request(), "Request");
                    }
                    ((ExceptionPage) exceptionPage).reportException(anException, extendedUserInfo, aContext);
                }

                if (AjaxUtils.isAjaxRequest(aContext.request()))
                {
                    AjaxUtils.redirectTo(exceptionPage);
                    aResponse = exceptionPage.context().response();
                    aContext.session().savePage(exceptionPage);
                }
                else
                {
                    aResponse = exceptionPage.generateResponse();
                }
            }
            else
            {
                aResponse = super.handleException(anException, aContext);
            }
        }
        catch (Exception e)
        {
            aResponse =  super.handleException(e, aContext);
        }

        aResponse.setStatus(500);
        return aResponse;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overridden to log messages when session refusing is turned on and off.
     *
     * @param shouldRefuse - true if the application should create new sessions, false otherwise
     */
    public void refuseNewSessions(boolean shouldRefuse)
    {
        logger.info("Changing refuseNewSessions to " + shouldRefuse);
        logger.info("Active sessions: " +  activeSessionsCount());

        super.refuseNewSessions(shouldRefuse);
    }



    /**
     * Diagnostic messages only.
     */
    public void terminate()
    {
        super.terminate();
        logger.info("**** Application shut down initiated.");
    }



    /**
     * Exposes protected method.
     *
     * @see com.webobjects.appserver.WOApplication#_terminateFromMonitor()
     */
    public void _terminateFromMonitor()
    {
        super._terminateFromMonitor();
    }



    /**
     * Returns the name of the page (can be used for any WOComponent) mapped to the passed name according to pageMap(), or just returns aPageName if there is no mapping for this page.  The page map is created by the application constructor which reads the file name from the configuration file.
     *
     * @param aPageName the name of the page to return a mapped name for
     * @return the name of the WO page mapped to the passed name according to pageMap(), or aPageName if there is no mapping for this page
     */
    public String pageNameMappedToName(String aPageName)
    {
        /** require [valid_param] aPageName != null; [page_map_not_null] pageMap() != null; **/

        String mappedPageForPage = aPageName;

        if (pageMap().objectForKey(aPageName) != null)
        {
            mappedPageForPage = (String)pageMap().objectForKey(aPageName);
            logger.info("Mapped page " + aPageName + " to " + mappedPageForPage);
        }

        return mappedPageForPage;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * <p>Overridden to</p>
     * <ul>
     * <li>Disable component access</li>
     * <li>Replace null aName with <code>defaultPageName()</code></li>
     * <li>Call pageNameMappedToName(String) to check for a replacement for aName</li>
     * <li>Call securePageForPageWithName(String, WOContext) check user permissions and enforce secure (SSL) access.</li>
     * </ul>
     *
     * @param aName the name of the page to return
     * @param aContext the context to obtain the page with
     * @return appropriate page based on the requested page name in the given context
     */
    public com.webobjects.appserver.WOComponent pageWithName(String aName, WOContext aContext)
    {
        /** require [valid_aContext_param] aContext != null; **/

        // Disable component access
        if ((aContext.senderID() == null) &&  (componentRequestHandlerKey().equals(aContext.request().requestHandlerKey())))
        {
            aName = defaultPageName();
        }

        // In some cases, aName is null and WebObjects attempts to display a page named Main, which often
        // does not exist (e.g. for URLs like http://localhost/cgi-bin/WebObjects.exe/Appname.woa/wo.)
        // This check traps this and uses defaultPageName() in place of "Main".
        if (aName == null)
        {
            aName = defaultPageName();
        }

        // Map to canonical name if we have a page map
        if (hasPageMap())
        {
            aName = pageNameMappedToName(aName);
        }

        WOComponent newPage = securePageForPageWithName(aName, aContext);
        if (newPage == null)
        {
            newPage = super.pageWithName(aName, aContext);
        }

        return newPage;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * <p>Overridden to check user permissions and enforce secure (SSL) access. For example, a redirect is performed to switch to a https URI if needed.  Also, if the user lacks permissions for the requested page, a login page is shown instead.</p>
     * <p>In order to make use of this method, pages must implement some of these methods:</p> <ul>
     * <li> <code>public boolean needsSecureConnection()</code> - return <code>true</code> if the page should only be accessible via SSL. </li>
     * <li> <code>public boolean permitsAccess(WOContext aContext)</code> - return <code>true</code> if the current user (typically found in aContext.session().user()) is allowed to access this page.  The method can use any logic it wants to determine if access is granted.  If access is denied (false is returned), <code>pageWithName</code> will return the page named in <code>loginPageName</code> instead.
     * <li> <code>public String loginPageName()</code> - the name of the page to show if <code>permitsAccess</code> returns <code>false</code>.</li>. If <code>permitsAccess</code> is implemented then this method must also be implemented.</ul>
     * <p> With this scheme, if permitsAccess, loginPageName, and setNextPageName are all implemented, then users can bookmark pages that are usually behind a login screen with a URI like this: https://domain/cgi-bin/WebObjects/AppName.woa/wo/SecurePageName.wo.  If they are already logged in, they will be taken to the requested page.  If they are not logged in they will be shown a login page and then taken to the requested page after successfully logging on.</p>
     * <p> NOTE: when using this authentication, pages should ensure that they do not create instances of pages to which the user does not have access as pageWithName may return a different page than requested.  If methods are then called on the returned page, an exception may be raised.</p>
     * <p> If the page identified by <code>loginPageName</code> has implemented a  <code>setNextPageName(String aString)</code> method, then the originally requested page will be set up as the next page to display after logging on.</p>
     * <p>This method can be overidden to just return null if this functionality is not desired.</p>
     * @param aName the name of the page to return
     * @param aContext the context to obtain the page with
     * @return <code>null</code> if page named aName is OK to return as is, the requested page in the given context or an instance of the loginPage as specified in <code>loginPageNamed</code>
     */
    public com.webobjects.appserver.WOComponent securePageForPageWithName(String aName, WOContext aContext)
    {
        /** require [valid_name] aName != null;  [valid_Context] aContext != null; **/

          // Use WebObject's way of finding a class when you don't have the package qualified name.  _NSUtilities so this is subject to change.
        java.lang.Class classForPage = _NSUtilities.classWithName(aName);
        if (classForPage == null)
        {
            throw new RuntimeException("Page " + aName + " does not exist!");
        }

        Method permitsAccessMethod = null;
        try
        {
            permitsAccessMethod = classForPage.getMethod("permitsAccess", new java.lang.Class[] {WOContext.class});
        }
        catch (NoSuchMethodException e) { }

        Method loginPageNameMethod = null;
        try
        {
            loginPageNameMethod = classForPage.getMethod("loginPageName", new java.lang.Class[] {});
        }
        catch (NoSuchMethodException e) { }

        if ((permitsAccessMethod != null) && (loginPageNameMethod == null))
        {
            throw new java.lang.RuntimeException("If you implement permitsAccess() you must also implement loginPageName()");
        }

        Method needsSecureConnectionMethod = null;
        boolean isSecurePage = false;
        try
        {
            needsSecureConnectionMethod = classForPage.getMethod("needsSecureConnection", new java.lang.Class[] {});
            try
            {
                // Invoke is not capable of returning a primative type, it is always wrapped in its associated class (Boolean, Integer, etc)
                java.lang.Boolean isSecurePageAsBoolean = (java.lang.Boolean) needsSecureConnectionMethod.invoke(null, new Object[] {});
                isSecurePage = isSecurePageAsBoolean.booleanValue();
            }
            catch(Exception e)
            {
                throw new java.lang.RuntimeException("An exception (" + e + ") occurred while trying to invoke needsSecureConnection on the " + aName + " page");
            }

        }
        catch (NoSuchMethodException e)
        {
        }

        boolean doesUserHavePermission = true;
        if (permitsAccessMethod != null)
        {
            try
            {
                java.lang.Boolean doesUserHavePermissionAsBoolean = (java.lang.Boolean)permitsAccessMethod.invoke(null, new Object[] {aContext});
                doesUserHavePermission = doesUserHavePermissionAsBoolean.booleanValue();
            }
            catch (Exception e)
            {
                throw new RuntimeException("An exception (" + e + ") occurred while trying to invoke permitsAccess on the " + aName + " page");
            }
        }

        // Determine if the page is not using SSL
        boolean isNotUsingSSL = ( ! RequestUtilities.isHTTPSRequest(aContext.request()));
        WOComponent newPage = null;

        if (isSecurePage && isNotUsingSSL)
        {
            String redirectURL = "https://" + RequestUtilities.hostNameFromRequest(aContext.request()) + aContext.request().uri();
            WORedirect redirectPage = (WORedirect) super.pageWithName("WORedirect", aContext);
            redirectPage.setUrl(redirectURL);

            newPage = redirectPage;
        }
        else if ( ! doesUserHavePermission)
        {
            // We are in the correct http/https mode, but the user does not have permission for this page.  Show the login page, and optionally set the requested page as the login followup page.
            String loginPageName;
            try
            {
                loginPageName = (String) loginPageNameMethod.invoke(null, new Object[] {});
            }
            catch (Exception e)
            {
                throw new RuntimeException("An exception (" + e + ") occurred while trying to invoke loginPageName on the " + aName + " page");
            }

            newPage = this.pageWithName(loginPageName, aContext);
        }

        return newPage;
    }



    /**
     * Returns an instance of the dynamic element requested, or of another dynamic element if one has been mapped to this name.  This allows one component to be substituted for another at runtime.  This was done to support customization of SiteMaker on a page by page basis.<br><br>
     *
     * <b>Bug Alert</b>: This only works for sub-classes of WODynamicElement.  It does not work for WOComponent sub-classes, nor is pageWithName called for WOComponent sub-classes.  To get WOComponent sub-classes to work with this use them in a WOSwitchComponent like this:<br>
     * <code>
     *     SMUserInstructions: WOSwitchComponent {<br>
     *         WOComponentName = "SMUserInstructions";<br>
     *     }<br>
     * </code>
     *
     * @param aName the name of the dynamic element to return
     * @param someAssociations a dictionary of associations
     * @param anElement a template of elements
     * @param aLanguageArray NSArray containing the language preferences specified by the browser
     * @return an instance of the page requested, or of another page if one has been mapped to this name
     */
    public WOElement dynamicElementWithName(String aName,
                                            NSDictionary someAssociations,
                                            WOElement anElement,
                                            NSArray aLanguageArray)
    {
        /** require [valid_aName_param] aName != null; **/

        if (hasPageMap())
        {
            aName = pageNameMappedToName(aName);
        }
        return super.dynamicElementWithName(aName,
                                            someAssociations,
                                            anElement,
                                            aLanguageArray);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * @return the class of EOEditingContext that the application should use
     */
    public Class editingContextClass()
    {
        if (editingContextClass == null)
        {
            String ecClassNamePropertyKey;
            if (appProperties().booleanPropertyForKey(DebugEditingContextLockingPropertyKey))
            {
                ecClassNamePropertyKey = DebuggingEditingContextClassNamePropertyKey;
            }
            else
            {
                ecClassNamePropertyKey = EditingContextClassNamePropertyKey;
            }

            String ecClassName =
                appProperties().stringPropertyForKey(ecClassNamePropertyKey);

            try
            {
                editingContextClass = Class.forName(ecClassName);
                if ( ! EOEditingContext.class.isAssignableFrom(editingContextClass))
                {
                    throw new Error(
                        "The class defined by the property "
                            + ecClassNamePropertyKey
                            + " (\""
                            + ecClassName
                            + "\") did not resolve to a subclass of EOEditingContext.");
                }
            }
            catch (ClassNotFoundException e)
            {
                throw new Error(
                    "The class defined by the property "
                        + ecClassNamePropertyKey
                        + " (\""
                        + ecClassName
                        + "\") was not found.  Please ensure that it is fully qualified.");
            }
        }

        return editingContextClass;
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns a new instance of the class of EOEditingContext that the application should use.  The
     * returned instance is <b>not</b> locked.
     *
     * @return a new instance of the class of EOEditingContext that the application should use
     */
    public EOEditingContext newEditingContext()
    {
        try
        {
            return (EOEditingContext)editingContextClass().newInstance();
        }
        catch (Exception e)
        {
            throw new net.global_village.foundation.ExceptionConverter(e);
        }
        /** ensure [valid_result] Result != null;
                   [correct_class] editingContextClass().isInstance(Result); **/
    }


    /**
     * Returns a new instance of the class of EOEditingContext that the application should use.  The
     * returned instance is <b>not</b> locked.
     *
     * @param parent the parent of this editing context
     * @return a new instance of the class of EOEditingContext that the application should use
     */
    public EOEditingContext newEditingContext(EOObjectStore parent)
    {
        /** require parent != null; **/

        try
        {
            Constructor ecConstructor = editingContextClass().getConstructor(new Class[] {EOObjectStore.class});
            return (EOEditingContext)ecConstructor.newInstance(new Object[] {parent});
        }
        catch (Exception e)
        {
            throw new net.global_village.foundation.ExceptionConverter(e);
        }

        /** ensure [valid_result] Result != null;
                   [correct_class] editingContextClass().isInstance(Result); **/
    }



    /**
     * Avoid a potential bug that causes session deadlock if WOSession.sleep() throws.
     */
    public void saveSessionForContext(WOContext wocontext)
    {
        try
        {
            super.saveSessionForContext(wocontext);
        }
        catch (Throwable t)
        {
            sessionStore().checkInSessionForContext(wocontext);
        }
    }



    /**
     * Returns the currency formatter using the <code>CustomInfo.plist</code>
     *
     * @return the currency formatter
     */
    public NSNumberFormatter currencyFormatter()
    {
        return properties().numberFormatterForKey("CurrencyFormatter");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the date formatter using the <code>CustomInfo.plist</code>.
     *
     * @return the date formatter
     */
    public NSTimestampFormatter dateFormatter()
    {
        return properties().timestampFormatterForKey("DateFormatter");

        /** ensure [valid_result] Result != null; **/

    }



    /**
     * Returns the time formatter using the <code>CustomInfo.plist</code>.
     *
     * @return the time formatter
     */
    public NSTimestampFormatter timeFormatter()
    {
        return properties().timestampFormatterForKey("TimeFormatter");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the date and time formatter using the <code>CustomInfo.plist</code>.
     *
     * @return the time formatter
     */
    public NSTimestampFormatter dateTimeFormatter()
    {
        return properties().timestampFormatterForKey("DateTimeFormatter");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the integer formatter using the <code>CustomInfo.plist</code>
     *
     * @return the integer formatter
     */
    public NSNumberFormatter integerFormatter()
    {
        NSNumberFormatter nf = properties().numberFormatterForKey("IntegerFormatter");
        nf.setAllowsFloats(false);
        return nf;

        /** ensure
        [valid_result] Result != null;
        [does_not_allow_floats] ! Result.allowsFloats(); **/
    }



    /**
     * Returns the number formatter using the <code>CustomInfo.plist</code>
     *
     * @return the number formatter
     */
    public NSNumberFormatter numberFormatter()
    {
        return properties().numberFormatterForKey("NumberFormatter");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the list of property keys that are required to be in the properties list to ensure that the application configuration information is complete and reasonable.
     *
     * @return the mutable array of required property keys
     */
    protected NSMutableArray requiredProperties()
    {
        return requiredProperties;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the properies object containing the application configuration so that KVC can access this making bindings easier to write.
     *
     * @return the properies object containing the application configuration.
     */
    public ApplicationProperties properties()
    {
        return properties;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Optional name of a framework to take configuration information from.  This can be used with
     * NSBundle and WOResourceManager.  If null (the default), there is no configuration framework.
     *
     * @return null or the name of the framework to take configuration information from
     */
    public String configurationFrameworkName()
    {
        return null;
    }


    public NSDictionary pageMap()
    {
        return pageMap;
    }


    public boolean hasPageMap()
    {
        return pageMap() != null;
    }



    /**
     * For Click to Open support and generally useful.
     *
     * @see "README in WOLips framework from Project Wonder"
     * @see <a href="http://wiki.objectstyle.org/confluence/display/WOL/Click+to+Open">Click to Open Wiki page</a>
     * @return <code>true</code> if the app is in development mode
     */
    public boolean developmentMode()
    {
        if (isDevelopmentMode == null)
        {
            isDevelopmentMode = properties().hasPropertyForKey("DevelopmentMode") ? new GVCBoolean(properties().booleanPropertyForKey("DevelopmentMode")) : GVCBoolean.falseBoolean();
        }

        return isDevelopmentMode.booleanValue();
    }



    /**
     * Returns ERXMigrator instance to use for migration.  The default is an instance of the inner class
     * Migrator.
     *
     * @return ERXMigrator instance to use for migration
     */
    protected ERXMigrator migrator()
    {
        return new Migrator();
    }



    /**
     * ERXMigrator subclass that sets the lock owner automatically and uses the enclosing classes
     * newEditingContext() method to create the editing contexts to use for migration.
     */
    class Migrator extends ERXMigrator
    {

        /**
         * Constructs an ERXMigrator with appname-portNumber as the lock owner.
         */
        public Migrator()
        {
            super(name() + "-" + host() + ":" + port());
            NSLog.out.appendln("Created Migrator for " + name() + "-" + host() + ":" + port());
        }



        /**
         * @return WOApplication.this.newEditingContext()
         */
        protected EOEditingContext newEditingContext() {
            return WOApplication.this.newEditingContext();
        }

    }


    // Blatant ripoff of code added to ERXApplication until we start subclassing that for real
    // This is for ClickToDebug
    protected void _debugValueForDeclarationNamed(WOComponent component, String verb, String aDeclarationName, String aDeclarationType, String aBindingName, String anAssociationDescription, Object aValue) {
        if (aValue instanceof String) {
            StringBuffer stringbuffer = new StringBuffer(((String) aValue).length() + 2);
            stringbuffer.append('"');
            stringbuffer.append(aValue);
            stringbuffer.append('"');
            aValue = stringbuffer;
        }
        if (aDeclarationName.startsWith("_")) {
            aDeclarationName = "[inline]";
        }

        StringBuffer sb = new StringBuffer();

        //NSArray<WOComponent> componentPath = ERXWOContext._componentPath(ERXWOContext.currentContext());
        //componentPath.lastObject()
        //WOComponent lastComponent = ERXWOContext.currentContext().component();
        String lastComponentName = component.name().replaceFirst(".*\\.", "");
        sb.append(lastComponentName);

        sb.append(verb);

        if (!aDeclarationName.startsWith("_")) {
            sb.append(aDeclarationName);
            sb.append(":");
        }
        sb.append(aDeclarationType);

        sb.append(" { ");
        sb.append(aBindingName);
        sb.append("=");

        String valueStr = aValue != null ? aValue.toString() : "null";
        if (anAssociationDescription.startsWith("class ")) {
            sb.append(valueStr);
            sb.append("; }");
        }
        else {
            sb.append(anAssociationDescription);
            sb.append("; } value ");
            sb.append(valueStr);
        }

        logger.info(sb.toString());
    }

    /**
     * The set of component names that have binding debug enabled
     */
    private NSMutableSet _debugComponents = new NSMutableSet();

    /**
     * Little bit better binding debug output than the original.
     */
    public void logTakeValueForDeclarationNamed(String aDeclarationName, String aDeclarationType, String aBindingName, String anAssociationDescription, Object aValue) {
        WOComponent component = ERXWOContext.currentContext().component();
        if (component.parent() != null) {
            component = component.parent();
        }
        _debugValueForDeclarationNamed(component, " ==> ", aDeclarationName, aDeclarationType, aBindingName, anAssociationDescription, aValue);
    }

    /**
     * Little bit better binding debug output than the original.
     */
    public void logSetValueForDeclarationNamed(String aDeclarationName, String aDeclarationType, String aBindingName, String anAssociationDescription, Object aValue) {
        WOComponent component = ERXWOContext.currentContext().component();
        if (component.parent() != null) {
            component = component.parent();
        }
        _debugValueForDeclarationNamed(component, " <== ", aDeclarationName, aDeclarationType, aBindingName, anAssociationDescription, aValue);
    }

    /**
     * Turns on/off binding debugging for the given component.  Binding debugging requires using the WOOgnl
     * template parser and setting ognl.debugSupport=true.
     *
     * @param debugEnabled whether or not to enable debugging
     * @param componentName the component name to enable debugging for
     */
    public void setDebugEnabledForComponent(boolean debugEnabled, String componentName) {
        if (debugEnabled) {
            _debugComponents.addObject(componentName);
        }
        else {
            _debugComponents.removeObject(componentName);
        }
    }

    /**
     * Returns whether or not binding debugging is enabled for the given component
     *
     * @param componentName the component name
     * @return whether or not binding debugging is enabled for the given componen
     */
    public boolean debugEnabledForComponent(String componentName) {
        return _debugComponents.containsObject(componentName);
    }

    /**
     * Turns off binding debugging for all components.
     */
    public void clearDebugEnabledForAllComponents() {
        _debugComponents.removeAllObjects();
    }

}
