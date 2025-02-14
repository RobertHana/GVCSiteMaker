// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.appserver;

import java.net.*;
import java.util.*;

import net.global_village.foundation.*;
import net.global_village.jmail.*;
import net.global_village.kerberos.*;
import net.global_village.virtualtables.*;
import net.global_village.woextensions.*;
import net.global_village.woextensions.WOApplication;
import net.global_village.woextensions.tests.*;

import org.xml.sax.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.mixedmedia.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.appserver.xml.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.eof.qualifiers.*;


/**
 * Common Application functionality for all GVCSiteMaker applications.  Currently, this provides application properties (configuration information), configuration diagnostics, page mapping, database initialization, error handling, and debugging functions.
 */
public class SMApplication extends net.global_village.woextensions.WOApplication
{
    public static final String EventLoggingKey = "EventLogging";
    public static final String EventLogPasswordKey = "EventLogPassword";

    public static final String UsesExternalAuthenticationKey = "UsesExternalAuthentication";
    public static final String ExternalAuthenticationUserHeaderKey = "ExternalAuthenticationUserHeader";
    public static final String ExternalAuthenticationCookieKey = "ExternalAuthenticationCookie";
    public static final String ExternalAuthenticationLogoutURLKey = "ExternalAuthenticationLogoutURL";

    public static final String UserCreationSubjectKey = "UserCreationSubject";
    public static final String UserCreationMessageKey = "UserCreationMessage";

    public static final String MixedMediaMappingModelFile = "MixedMediaXMLModel.txt";	// Name of Mapping Model for Mixed Media XML Configuration files
    public static final String MixedMediaXMLDirectory = "MixedMediaXML";  // Directory containing Mixed Media XML Configuration files

    public static final NSArray eoModelsToIgnore = new NSArray(new String[] {"GVCGenericObjectsTest", "UMTestLDAP", "GVCJEOFValidationTest", "TestModel1", "TestModel2", "TestModel3", "TestUMLDAP"});

    protected SMEOUtils entityTranslator;                      // Supports custom sub-classes in GVCSMCustom
    protected boolean shouldCleanupPreviousRequest = true;     // For reclaiming memory from large uploads
    protected NSArray mixedMediaContentConfigurations;         //List of all MixedMedia content types uploaded from XML files


    /**
     * Convenience method to return the Product Name as this is needed so often.
     *
     * @return the ProductName configuration property.
     */
    public static String productName()
    {
        return ((SMApplication) WOApplication.application()).properties().stringPropertyForKey("ProductName");
    }



    /**
     * Returns all MixedMedia content types uploaded from XML files.
     *
     * @return all MixedMedia content types uploaded from XML files.
     */
    public static NSArray mixedMediaContentConfigurations()
    {
        return ((SMApplication) WOApplication.application()).mixedMediaContentConfigurations;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the application cast to this application subclass.
     *
     * @return the application cast to this application subclass
     */
    public static SMApplication smApplication()
    {
        return (SMApplication)Application.application();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Designated constructor.
     */
    public SMApplication()
    {
        super();
    }



    /**
     * Loads the application configuration information and page map, configures the debugging and WebObjects environment, and performs diagnostic checks.  This can be called again to reload the information.
     */
    public void initializeApplicationConfiguration()
    {
        // Don't call super.initializeApplicationConfiguration() - we still do it a little differently

        // Redirect debug output NSLog.out.  Note that DebugOut has not been initialized with the OutputPath yet.
        DebugOut.setOut(((NSLog.PrintStreamLogger)NSLog.out).printStream());

        loadConfigurationFiles();

        // Dummy value for superclass.   GVC.SiteMaker uses PublicUserSessionTimeOutInMinutes
        properties().addPropertyForKey("1", SessionTimeOutInMinutesKey);

        if ( ! isTerminating())
        {
            DebugOut.println(1, "Loading page map ...");
            loadPageMap();

            performConfigurationDiagnostics();

            configureDatabaseConnections(eoModelsToIgnore);
            configureDebuggingEnvironment();

            configureWebObjectsEnvironment();

            //Load MixedMedia XML Configuration Files
            loadMixedMediaConfigurationFiles();

            if (isTerminating())
            {
                return;
            }

            // Log all fetched data for really down deep debugging
            // Ideally this would be in performConfigurationDiagnostics(), however it needs to come
            // after the models have been connected.  Or, better, installed as the result of catching
            // a notification.
            if (properties().booleanPropertyForKey("LogFetchedRows"))
            {
                DebugOut.println(1,"Enabling Fetched row logging debugging");
                EOEditingContext ec = newEditingContext();
                ec.lock();
                try
                {
                    EODatabaseDataSource dataSource = new EODatabaseDataSource ( ec, "Website" );
                    EODatabaseContext databaseContext = dataSource.databaseContext();
                    databaseContext.lock();
                    try
                    {
                        EODatabaseChannel dbChannel = databaseContext.availableChannel();

                        if ( dbChannel != null )
                        {
                            EOAdaptorChannel adChannel = dbChannel.adaptorChannel();
                            adChannel.setDelegate(this);
                        }
                    }
                    finally
                    {
                        databaseContext.unlock();
                    }
                }
                finally
                {
                    ec.unlock();
                    ec.dispose();
                }
            }

            // Make GVC.SiteMaker specific updates to the EOModels.
            configureEOModels();

            // Initialize VirtualTable engine
            NSArray additionalEntities = new NSArray(new EOEntity[] {EOModelGroup.defaultGroup().entityNamed("SMVirtualTable"), EOModelGroup.defaultGroup().entityNamed("VirtualSiteFileField"), EOModelGroup.defaultGroup().entityNamed("VirtualUserField")});
            int pksToCache = 0;
            if (isUsingIntegerPKs())
            {
                pksToCache = 100;
            }
            VirtualTable.initializeVirtualTables(pksToCache, additionalEntities);

            configureEmailEnvironment();

            // Create formatters for all the date formats to work around a bug in WO 5.1 where there is no time zone set on a formatter by default.
            createFormatters();

            //Load links from Configuration File
            properties().addPropertyForKey(RTESelectableConfigLink.rteSelectableConfigLinks(properties().arrayPropertyForKey("RTEAdditionalLinks")), "RTESelectableConfigLinks");

            // Create Kerberos Authenticator
            try
            {
                DebugOut.println(1, "Creating Kerberos Authenticator...");
                if (properties().booleanPropertyForKey("HasInternalUsers") && properties().hasPropertyForKey("KerberosServer"))
                {
                    KerberosAuthenticator.createInstance(properties().stringPropertyForKey("KerberosServer"),
                                                         properties().stringPropertyForKey("KerberosRealm"));

                    // Try to authenticate a dummy user as the first request always takes a long time.  This makes the delay happen during application startup instead of bocking requests.
                    DebugOut.println(1, "Authenticating Dummy User with Kerberos...");
                    User.canAuthenticateInternalUser("dummy", "gvc");
                    DebugOut.println(1, "...authenticated Dummy User with Kerberos!");
                }
            }
            catch (Exception e)
            {
                terminate();
                DebugOut.println(1, "Failed to create KerberosAuthenticator because: " + e);
            }
        }


        // configure ValidDomains: make sure it contains at least the configured DomainName
        if ( ! isTerminating())
        {
            NSMutableArray validDomains;
            if (properties().hasPropertyForKey("ValidDomains"))
            {
                validDomains = properties().arrayPropertyForKey("ValidDomains").mutableClone();
                validDomains.addObject(properties().stringPropertyForKey("DomainName"));
            }
            else
            {
                validDomains = new NSMutableArray(properties().stringPropertyForKey("DomainName"));
            }
            properties().removePropertyForKey("ValidDomains");
            properties().addPropertyForKey(validDomains, "ValidDomains");
        }


        // Now that everything is set up, check that the database is bootstrapped
        try
        {
            if ( ! isTerminating())
            {
                DebugOut.println(1, "Application Configuration Initialized");
                DebugOut.println(1, "Checking database to see if bootstrapping is needed...");

                bootstrapDatabase();

                if ( ! isTerminating())
                {
                    // Configure to optionally use custom classes from GVCSMCustom in place of the ones in GVCSMCore.  SMEOUtils implements the EOModelGroup delegate method classForObjectWithGlobalID(EOEntity, EOGlobalID) to accomplish this.
                    DebugOut.println(1, "Installing EntityTranslator...");
                    entityTranslator = new SMEOUtils();
                    EOModelGroup.defaultGroup().setDelegate(entityTranslator);
                    DebugOut.println(1, "EntityTranslator installed, performing database diagnostics...");
                    perfromDatabaseDiagnostics();

                    if ( ! isTerminating())
                    {
                        DebugOut.println(1, "Welcome to " + name() + "!");
                    }
                    else
                    {
                        DebugOut.println(1, "\n*** SHUTDOWN *** Application shutting down due to database configuration issues above.\n");
                    }
                }
                else
                {
                    DebugOut.println(1, "\n*** SHUTDOWN *** Bootstrap completed.  Application now shutting down.\n");
                }
            }
            else
            {
                DebugOut.println(1, "\n*** SHUTDOWN *** Application shutting down due to configuration issues above.\n");
            }
        }
        catch (Throwable t)
        {
            System.out.println("Application constructor threw " + t.getMessage());
            t.printStackTrace();
            terminate();
        }
    }



    /**
     * Returns the URL to the log4j config file in GVCSMCustom or the default
     * one in GVCWOExtensions of there is no custom one.
     *
     * @return URL to log4j configuration
     */
    protected URL log4jConfigUrl()
    {
        URL configURL = resourceManager().pathURLForResourceNamed("log4j.xml", "GVCSMCustom", null);
        return configURL == null ? super.log4jConfigUrl() : configURL;
        /** ensure [result_not_null] Result != null; **/
    }



     /**
       * Loads the application configuration information.
       */
     protected void loadConfigurationFiles()
     {
         // Note that DebugOut has not been initialized with the OutputPath yet.  It just goes to System.out.

         // Load configuration information.
         DebugOut.setApplicationName(name());
         DebugOut.println(1, "Loading properties...");

         try
         {
             // This establishes a hierarchy of property definitions.  Properties loaded later override properties loaded earlier.  The properties in GVCSiteMakerCustom.plist override properties defined in GVCSMCore.  Properties defined in GVCSiteMaker.plist and GVCSiteMakerDebug.plist should be considered defaults.
             properties = new ApplicationProperties();
             properties.addPropertiesFromFile("GVCSiteMaker.plist", "GVCSMCore");
             properties.addPropertiesFromFile("GVCSiteMakerDebug.plist", "GVCSMCore");
             properties.addPropertiesFromFile("GVCSiteMakerCustom.plist", "GVCSMCustom");

             // Use properties.addPropertiesFromFile(String plist, String framework) if there are other configuration files to load.

             // Load the WOD file to use with PageScaffold.java.
             loadFileAsProperty("PageScaffold.wod", "PageScaffold.wod");

             // Load TinyMCE config files
             loadFileAsProperty("tinymce-init-configappearance.js", "tinymce-init-configappearance.js");
             loadFileAsProperty("tinymce-init-inlinemixedmedia.js", "tinymce-init-inlinemixedmedia.js");
             loadFileAsProperty("tinymce-init-inlinetext.js", "tinymce-init-inlinetext.js");

             // Load the Data Access Section default templates.  The default search mode template is generated.
             loadFileAsProperty("DA_AddModeDefaultTemplate.html", "DA_addModeDefaultTemplate");
             loadFileAsProperty("DA_SingleModeDefaultTemplate.html", "DA_singleModeDefaultTemplate");
             loadFileAsProperty("DA_ListModeDefaultTemplate.html", "DA_listModeDefaultTemplate");
             loadFileAsProperty("DA_SearchModeDefaultQueryBuilderTemplate.html", "DA_searchModeDefaultqueryBuilderTemplate");
             loadFileAsProperty("DA_SearchModeDefaultSearchAllTemplate.html", "DA_searchModeDefaultsearchAllTemplate");
             loadFileAsProperty("DA_SearchModeDefaultSimpleSearchTemplate.html", "DA_searchModeDefaultsimpleSearchTemplate");
             loadFileAsProperty("DA_ImportModeDefaultTemplate.html", "DA_importModeDefaultTemplate");
             loadFileAsProperty("DA_ImportModeCustomTemplate.html", "DA_ImportModeCustomTemplate");

             // Load the bindings for the Website Creation Message
             loadFileAsProperty("WebsiteCreationMessage.wod", "WebsiteCreationMessage.wod");

             // Load the bindings for the User Creation Message
             loadFileAsProperty("UserCreationMessage.html", "UserCreationMessage");
             loadFileAsProperty("UserCreationMessage.wod", "UserCreationMessage.wod");


             // Load the bindings for the DA Notifications
             loadFileAsProperty("DANotificationMessage.wod", "DANotificationMessage.wod");

             // Load the HTML for the DA Calculated column instructions
             loadFileAsProperty("DACalculatedColumnMessage.html", "DACalculatedColumnMessage");


             // Load the Data Access Notification default templates.
             loadFileAsProperty("DANotificationMessageDefaultCreationTemplate.html",
                                "DANotificationMessageDefaultCreationTemplate");
             loadFileAsProperty("DANotificationMessageDefaultModificationTemplate.html",
                                "DANotificationMessageDefaultModificationTemplate");

             // Load the bindings for the Queued Tasks
             loadFileAsProperty("QueuedTaskMessage.wod", "QueuedTaskMessage.wod");

             // Load the Queued Task default templates.
             loadFileAsProperty("QueuedTaskMessageSuccessTemplate.html",
                                "QueuedTaskMessageSuccessTemplate");
             loadFileAsProperty("QueuedTaskMessageFailureTemplate.html",
                                "QueuedTaskMessageFailureTemplate");

             DebugOut.println(1,"Done.");
         }
         catch (java.lang.IllegalArgumentException e)
         {
             DebugOut.println(1, "Terminating: Load properties failed with exception: " + e.getMessage());
             terminate();
         }
     }



    /**
     * Loads the contents of the named file into the system properties with the key propertyName.  If this is present in GVCSMCustom, that version is used.  If it is not present, the default version in GVCSMCore is used.  This allows a customized version of the file to be used.
     *
     * @param fileName - the name of the file to load into the system properties
     * @param propertyName - the name of the property to make the file contents available under.
     */
    public void loadFileAsProperty(String fileName, String propertyName)
    {
        /** require [has_fileName] fileName != null;  [has_propertyName] propertyName != null;  **/
        DebugOut.println(1, "Loading " + fileName);
        String fileContents = ResourceManagerAdditions.stringFromResource(fileName, "GVCSMCustom");
        if (fileContents == null)
        {
            fileContents = ResourceManagerAdditions.stringFromResource(fileName, "GVCSMCore");
        }

        if (fileContents != null)
        {
            DebugOut.println(1, "Loaded " + fileName);
            properties().addPropertyForKey(fileContents, propertyName);
        }
        else
        {
            throw new RuntimeException("File " + fileName + " was not found in application configuration.");
        }
    }



    /**
     * Perform diagnostic checks to ensure that the application configuration information is complete and reasonable.  The application is marked for temination if the configuration is not complete and reasonable.
     */
    protected void performConfigurationDiagnostics()
    {
        // Connection dictionary stuff
        requireProperty("JDBCUrl");
        requireProperty("DBUserName");
        requireProperty("DBPassword");
        requireProperty("JDBCDriver");
        requireProperty("EOFPlugin");
		requireProperty("DBType");

        // Internal Users and Kerberos stuff
        requireProperty("HasInternalUsers");

        // Validate Kerberos related items
        if (properties().booleanPropertyForKey("HasInternalUsers"))
        {
            requireProperty("InternalUserDesignatorName");
            requireProperty("InternalUserSuffix");

            // Kerberos is only one way of handling internal users.  The first property is optional,
            // the latter two mandatory if it is present.
            if (properties().hasPropertyForKey("KerberosServer"))
            {
                requireProperty("KerberosRealm");
                requireProperty("DebugKerberos");


                // Validate KerberosServer address
                try
                {
                    java.net.InetAddress.getByName(properties().stringPropertyForKey("KerberosServer"));
                }
                catch (java.net.UnknownHostException e)
                {
                    terminate();    // It is required do not proceed with launching the application.
                    DebugOut.println(1, "KerberosServer '" + properties().stringPropertyForKey("KerberosServer")
                                    + "' cannot be located.");
                }

                // There is no possible validation for realm.
            }

        }

        requireProperty(UsesExternalAuthenticationKey);
        if (properties().booleanPropertyForKey(UsesExternalAuthenticationKey))
        {
            requireProperty(ExternalAuthenticationUserHeaderKey);
            // These are optional:
            // requireProperty(ExternalAuthenticationCookieKey);
            // requireProperty(ExternalAuthenticationLogoutURLKey);
        }

        // Public stuff
        requireProperty("PublicUserID");
        requireProperty("CanEditUserID");

        // Mail configuration
        requireProperty("SMTPHost");
        requireProperty("FromAddress");
        requireProperty("MailSenderQueueSize");
        requireProperty("WebsiteCreationSubject");
        requireProperty("WebsiteCreationMessage.wod");
        requireProperty("UserCreationSubject");
        requireProperty(UserCreationMessageKey + ".wod");
        requireProperty(UserCreationMessageKey);
        UserCreationMessage.validateTemplate(null);

        // Domain and URL configuration
        requireProperty("InsecureProtocol");
        requireProperty("SecureProtocol");
        requireProperty("DomainName");
        requireProperty("AdaptorPath");
        requireProperty("ApplicationName");
        requireProperty("AdministrationName");
        requireProperty("RestrictedSiteIDs");
        requireProperty("DirectActionFragment");

        // Uploaded Files config
        requireProperty("FilePath");
        requireProperty("UploadDirectory");
        requireProperty("WebsiteFileUsageWarningLevelPercent");

        // General config
        requireProperty("FilePasswordExpirationTimeInDays");
        requireProperty("DefaultQuotaInMegabytes");
        requireProperty("PasswordChars");
        requireProperty("HitsDisplayedPerPageInDirectory");
        requireProperty("NumberOfVisitedSitesToList");
        requireProperty("ImageMimeTypes");
        requireProperty("SectionMimeTypes");
        requireProperty("URLPrefixes");
        requireProperty("FileBufferMegaBytes");
        requireProperty("ProductName");
        requireProperty("InstallationName");
        requireProperty("ShowPoweredByLink");
        requireProperty("PoweredByLink");
        requireProperty("ShowSectionSourceNotice");
        requireProperty("ConvertCarriageReturnsToLineBreaks");
        requireProperty("EditThisPageText");
        requireProperty("EditThisPageTitle");
        requireProperty("EditDifferentVersionText");
        requireProperty("IMSLTIRemoteParticipationEnabled");

        // Formatters
        requireProperty("LongDateFormat");
        requireProperty("StandardDateFormat");
        requireProperty("CompressedDateFormat");
        requireProperty("DateAndTimeFormat");
        requireProperty("InputDateFormat");
        requireProperty("DefaultInputTime");
        requireProperty("InputDateFormatHint");
        requireProperty("InputDateOnlyFormat");
        requireProperty("InputDateOnlyFormatHint");
        requireProperty("StandardNumberFormat");
        requireProperty("InputNumberFormat");

        // Styles and components
        requireProperty("FontFaceList");
        requireProperty("FontSizeList");
        requireProperty("FallbackFontFaceList");
        requireProperty("PageScaffold.wod");
        requireProperty("StyleSheets");

        // Messages
        requireProperty("LoginPageMessage");
        requireProperty("UserIDNotEmailAddressMessage");
        requireProperty("ChangePasswordPageMessage");
        requireProperty("CantChangeInternalUserPasswordMessage");
        requireProperty("CantSendInternalUserPasswordMessage");
        requireProperty("SendPasswordPageMessage");
        requireProperty("IE5UploadFailureMessage");

        // Data Access Section
        requireProperty("DataAccessReservedNames");
        requireProperty("DataAccessReservedPrefixes");
        requireProperty("DataAccessReservedSuffixes");
        requireProperty("DA_addModeDefaultTemplate");
        requireProperty("DA_singleModeDefaultTemplate");
        requireProperty("DA_listModeDefaultTemplate");
        requireProperty("DA_searchModeDefaultqueryBuilderTemplate");
        requireProperty("DA_searchModeDefaultsearchAllTemplate");
        requireProperty("DA_searchModeDefaultsimpleSearchTemplate");
        requireProperty("DataAccessDateFormats");
        requireProperty("DataAccessNumberFormats");
        requireProperty("DataAccessListSearchResultGroupSizes");
        requireProperty("DataAccessMaximumNumberOfFieldsInTable");
        requireProperty("DataAccessMaximumNumberOfFieldsInImport");

        // Mixed Media Section
        requireProperty("DefaultMixedMediaLayoutName");
        requireProperty("ConnectionRequestTimeOut");

        // Queued Task Configuration
        requireProperty("TaskQueueTimerInterval");

        // Help and support
        requireProperty("SupportURL");
        requireProperty("PageClassToHelpURLMap");

        // Appserver stuff
        requireProperty("DefaultFetchTimestampLag");
        requireProperty("AuthenticatedUserSessionTimeOutInMinutes");
        requireProperty("PublicUserSessionTimeOutInMinutes");
        requireProperty("SessionTerminateTimeOutInSeconds");
        requireProperty("PageCacheSize");
        requireProperty("CachingEnabled");
        requireProperty("DirectConnectEnabled");
        requireProperty("EditableDAModesUseSessionsTimestamp");
        requireProperty(EditingContextClassNamePropertyKey);
        requireProperty(DispatchRequestsConcurrentlyPropertyKey);


        // WYSIWYG Editing
        requireProperty("WYSIWYGEditorEnabled");
        if (properties().booleanPropertyForKey("WYSIWYGEditorEnabled"))
        {
            requireProperty("WYSIWYGInstallDirectory");
            requireProperty("RichTextEditor");
            requireProperty("RichTextEditorInstructions");
            requireProperty("CheckBrowserCompliance");
            if (properties().booleanPropertyForKey("CheckBrowserCompliance"))
            {
                requireProperty("CompliantBrowsers");
            }

            requireProperty("URLLinkScript");
            requireProperty("ImageListVariableName");
            requireProperty("LinkListVariableName");
            //requireProperty("RTEAdditionalLinks");
        }
        requireProperty("tinymce-init-configappearance.js");
        requireProperty("tinymce-init-inlinemixedmedia.js");
        requireProperty("tinymce-init-inlinetext.js");

        // Versioning
        requireProperty("VersioningEnabled");
        requireProperty("AutoVersioningEnabled");

        requireProperty("RequireHTTPSAccessForPublicSections");
        if (properties().booleanPropertyForKey("RequireHTTPSAccessForPublicSections"))
        {
            requireProperty("RedirectPath");
        }

        // Debug items
        requireProperty("HandleInvalidCookies");
        requireProperty("DebugRequestResponseLoop");
        requireProperty("LogFetchedRows");
        requireProperty("EOAdaptorDebugging");
        requireProperty("DebugLevel");
        requireProperty("DebugLDAP");
        requireProperty(DebugEditingContextLockingPropertyKey);
        requireProperty(DebuggingEditingContextClassNamePropertyKey);
        requireProperty(EventLoggingKey);
        requireProperty(EventLogPasswordKey);

        requireProperty("DevelopmentMode");
        if (properties().booleanPropertyForKey("DevelopmentMode"))
        {
            requireProperty("DeveloperID");
            requireProperty("DeveloperPassword");
        }

        // Required page mappings.  These pages do not actually exist and a mapping is mandatory
        requireMappingForPage("LoginPage");
        requireMappingForPage("LogoutPage");
    }



    /**
     * Perform diagnostic checks to ensure that the application configuration information matches what is in the database.  The application is marked for temination if it does not.
     */
    protected void perfromDatabaseDiagnostics()
    {
        EOEditingContext ec = newEditingContext();
        ec.lock();
        try
        {
            PublicGroup.group(ec);
            InternalUsersGroup.group(ec);
        }
        catch (RuntimeException e)
        {
            DebugOut.println(1, "Can not locate Public Group in database!");
            DebugOut.println(1, "Can not locate Internal Users Group in database!");
            terminate();

        }
        finally
        {
            ec.unlock();
            ec.dispose();
        }
    }



    /**
     * Sets up the debugging environment.
     */
    protected void configureDebuggingEnvironment()
    {
        // This does not appear to have any affect, but may in some later version.
        if (properties().booleanPropertyForKey("DebugRequestResponseLoop"))
        {
            NSLog.allowDebugLoggingForGroups(NSLog.DebugGroupRequestHandling);
        }

        if (SMApplication.appProperties().booleanPropertyForKey(DebugEditingContextLockingPropertyKey))
        {
            NSLog.allowDebugLoggingForGroups(NSLog.DebugGroupMultithreading);
        }

        int debugLevel = properties().intPropertyForKey("DebugLevel");
        DebugOut.println(1,"Setting DebugLevel to " + debugLevel);
        DebugOut.setDebugLevel(debugLevel);

        // Set an appropriate mode.  VERBOSE and PONTIFICAL are too expensive for production, TERSE is not detailed enough (no timestamp).
        // debugLevel == 1      -> TERSE
        // debugLevel == 2..10  -> NSLOG
        // debugLevel == 11..29 -> VERBOSE
        // debugLevel == > 29   -> PONTIFICAL
        int debugMode;
        if (debugLevel > 29)
        {
            debugMode = DebugOut.PONTIFICAL;
            NSLog.debug.setAllowedDebugLevel(NSLog.DebugLevelDetailed);
            DebugOut.println(1,"Setting DebugMode to PONTIFICAL");
        }
        else if (debugLevel > 10)
        {
            debugMode = DebugOut.VERBOSE;
            DebugOut.println(1,"Setting DebugMode to VERBOSE");
        }
        else if (debugLevel == 1)
        {
            debugMode = DebugOut.TERSE;
            DebugOut.println(1,"Setting DebugMode to TERSE");
        }
        else
        {
            debugMode = DebugOut.NSLOG;
            DebugOut.println(1,"Setting DebugMode to NSLOG");
        }

        DebugOut.setMode(debugMode);

        // SQL logging
        if (properties().booleanPropertyForKey("EOAdaptorDebugging"))
        {
            NSLog.allowDebugLoggingForGroups(NSLog.DebugGroupSQLGeneration
                                             | NSLog.DebugGroupDatabaseAccess
                                             | NSLog.DebugGroupEnterpriseObjects);
            DebugOut.println(1,"Enabling EOAdaptor debugging");
        }

        // Log Kerberos activity
        if (properties().booleanPropertyForKey("HasInternalUsers") && properties().hasPropertyForKey("KerberosServer"))
        {
            KerberosAuthenticator.setDebugMode(properties().booleanPropertyForKey("DebugKerberos"));
        }

        // Log LDAP activity
        LDAPBranch.setDebugMode(properties().booleanPropertyForKey("DebugLDAP"));

        // Event logging
        if (properties().booleanPropertyForKey(EventLoggingKey))
        {
            EOEventCenter.setPassword(properties.stringPropertyForKey(EventLogPasswordKey));
            System.setProperty("EOEventLogging", "true");
        }
    }



    /**
     * Sets up the WebObjects application environment.
     */
    protected void configureWebObjectsEnvironment()
    {
        super.configureWebObjectsEnvironment();

        // It is very important that comments are included as they wrap CSS and JavaScript that we need.
        setIncludeCommentsInResponses(true);

        // Don't enable caching in development mode so that rapid turnaround works
        if (properties().booleanPropertyForKey("DevelopmentMode"))
        {
            setCachingEnabled(false);
        }

        setCGIAdaptorURL(SMActionURLFactory.insecureProtocol() + SMActionURLFactory.domainName() + SMActionURLFactory.adaptorPath());

        // Set EditingContext snapshot timeout so that data is frequently refetched to compensate for multiple instances.
        EOEditingContext.setDefaultFetchTimestampLag( properties().intPropertyForKey("DefaultFetchTimestampLag") );
        DebugOut.println( 1, "EO Snapshot timeout is set to " + (EOEditingContext.defaultFetchTimestampLag()/1000) + " seconds" );

        setSessionTimeOut(new Integer(60 * properties().intPropertyForKey("PublicUserSessionTimeOutInMinutes")));
        DebugOut.println(18,"Public user TimeOut is " + sessionTimeOut() + " seconds.");

        // Revert to 5.1 form parsing behaviour: if this is not set (or set to false), then any file upload _must_ be the last input on a form
        // Commented out in 5.2.2 as this bug appears to be fixed
        //System.setProperty("WOUseLegacyMultipartParser", "YES");

        // add support for the InOr qualifer which will hopefully fix a FB bug where many OR's in a query takes forever to process
        EOQualifierSQLGeneration.Support.setSupportForClass(new ERXInOrQualifierSupport(), EOOrQualifier._CLASS);

        /*
         These properties specify the default connect and read timeout (resp.) for
         the protocol handler used by java.net.URLConnection.

         sun.net.client.defaultConnectTimeout specifies the timeout (in milliseconds) to
         establish the connection to the host. For example for http connections it is the
         timeout when establishing the connection to the http server. For ftp connection
         it is the timeout when establishing the connection to ftp servers.

         sun.net.client.defaultReadTimeout specifies the timeout (in milliseconds) when
         reading from input stream when a connection is established to a resource.

         Reference http://java.sun.com/j2se/1.4.2/docs/guide/net/properties.html

         The defaults are -1 (a.k.a. forever).  Set these to a more reasonable setting to
         prevent the app from blocking on URL validation or information pulled from slow
         external sources.
        */
        String timeoutInMilliseconds = new Integer(properties.intPropertyForKey("ConnectionRequestTimeOut") * 1000).toString();
        System.getProperties().setProperty("sun.net.client.defaultConnectTimeout", timeoutInMilliseconds);
        System.getProperties().setProperty("sun.net.client.defaultReadTimeout", timeoutInMilliseconds);

        /** ensure [has_ec_class] editingContextClass() != null;  **/
    }



    /**
     * Sets up email.
     */
    protected void configureEmailEnvironment()
    {
        ThreadedMailAgent.createInstance(properties().stringPropertyForKey("SMTPHost"), properties().intPropertyForKey("MailSenderQueueSize"));
    }



    /**
     * Makes in memory changes to the EOModel for GVC.SiteMaker compatiability.
     */
    protected void configureEOModels()
    {
        // Add backpointing relationships from net.global_village.virtualtables.Table and net.global_village.virtualtables.VirtualTable to Website.  This is needed for two reasons.
        //
        // 1. When fetching databaseTables() on Website, this exception is thrown
        //		* Error:   java.lang.IllegalStateException<br>
        //		* Reason:  sqlStringForKeyValueQualifier: attempt to generate SQL for com.webobjects.eocontrol.EOKeyValueQualifier
        //				  (_eofInv_WebsiteTable_table.website.websitePKey = 1000001) failed because attribute identified by key
        //				  '_eofInv_WebsiteTable_table.website.websitePKey' was not reachable from from entity 'SMVirtualTable'
        //    and also for Table and VirtualTable
        //
        // 2. When attaching a new SMVirtualTable to a Website, the backpointing relationship (newTable.websites()) is null until after the new object is saved.  This makes validation impossible.  The relationships are not fully set, even after calling editingContext().processRecentChanges();
        EOEntity tableEntity = EOModelGroup.defaultGroup().entityNamed("Table");
        EOEntity websiteTablesEntity = EOModelGroup.defaultGroup().entityNamed("WebsiteTable");
        EOEntity virtualTableEntity = EOModelGroup.defaultGroup().entityNamed("VirtualTable");

        // Add websiteTables from Table <--> WebsiteTable
        EORelationship websiteTables = new EORelationship();
        websiteTables.setName("websiteTables");
        tableEntity.addRelationship(websiteTables);
        EOJoin join = new EOJoin(tableEntity.attributeNamed("tableID"), websiteTablesEntity.attributeNamed("tableID"));
        websiteTables.addJoin(join);
        websiteTables.setJoinSemantic(EORelationship.InnerJoin);
        websiteTables.setToMany(true);

        // Add flattened websites from Table <--> Website
        EORelationship websites = new EORelationship();
        websites.setName("websites");
        tableEntity.addRelationship(websites);
        websites.setDefinition("websiteTables.website");
        websites.setJoinSemantic(EORelationship.InnerJoin);
        websites.setDeleteRule(EOClassDescription.DeleteRuleNullify);

        // Add websiteTables from VirtualTable <--> WebsiteTable
        websiteTables = new EORelationship();
        websiteTables.setName("websiteTables");
        virtualTableEntity.addRelationship(websiteTables);
        join = new EOJoin(virtualTableEntity.attributeNamed("tableID"), websiteTablesEntity.attributeNamed("tableID"));
        websiteTables.addJoin(join);
        websiteTables.setJoinSemantic(EORelationship.InnerJoin);
        websiteTables.setToMany(true);

        // Add flattened websites from VirtualTable <--> Website
        websites = new EORelationship();
        websites.setName("websites");
        virtualTableEntity.addRelationship(websites);
        websites.setDefinition("websiteTables.website");
        websites.setJoinSemantic(EORelationship.InnerJoin);
        websites.setDeleteRule(EOClassDescription.DeleteRuleNullify);

        //
        // Relationships to support Data Access Notifications
        //

        // Now add backpointing relationships to handle delete rules between Column and the DataAccess page component.
        EOEntity columnEntity = EOModelGroup.defaultGroup().entityNamed("Column");
        EOEntity dataAccessColumnNotificationEntity = EOModelGroup.defaultGroup().entityNamed("DataAccessColumnNotification");
        EOEntity virtualColumnEntity = EOModelGroup.defaultGroup().entityNamed("VirtualColumn");
        EOEntity virtualLookupColumnEntity = EOModelGroup.defaultGroup().entityNamed("VirtualLookupColumn");
        EOEntity virtualUserColumnEntity = EOModelGroup.defaultGroup().entityNamed("VirtualUserColumn");

        // 1A. Add non-class property dataAccessNotifications from Column <--> DataAccessColumnNotification
        EORelationship dataAccessNotifications = new EORelationship();
        dataAccessNotifications.setName("columnDataAccessNotifications");
        columnEntity.addRelationship(dataAccessNotifications);
        // Remove as class property
        NSMutableArray classProperties = new NSMutableArray(columnEntity.classProperties());
        classProperties.removeObject(dataAccessNotifications);
        columnEntity.setClassProperties(classProperties);
        EOJoin columnDataAccessJoin = new EOJoin(columnEntity.attributeNamed("columnID"), dataAccessColumnNotificationEntity.attributeNamed("columnId"));
        dataAccessNotifications.addJoin(columnDataAccessJoin);
        dataAccessNotifications.setJoinSemantic(EORelationship.InnerJoin);
        dataAccessNotifications.setToMany(true);
        dataAccessNotifications.setDeleteRule(EOClassDescription.DeleteRuleCascade);
        dataAccessNotifications.setPropagatesPrimaryKey(true);

        // 1B. Add flattened dataAccessNotifications from Column <--> DataAccess
        dataAccessNotifications = new EORelationship();
        dataAccessNotifications.setName("dataAccessNotifications");
        columnEntity.addRelationship(dataAccessNotifications);
        dataAccessNotifications.setDefinition("columnDataAccessNotifications.dataAccess");
        dataAccessNotifications.setJoinSemantic(EORelationship.InnerJoin);
        dataAccessNotifications.setDeleteRule(EOClassDescription.DeleteRuleNullify);

        // 2A. Add non-class property dataAccessNotifications from VirtualColumn <--> DataAccessColumnNotification
        dataAccessNotifications = new EORelationship();
        dataAccessNotifications.setName("columnDataAccessNotifications");
        virtualColumnEntity.addRelationship(dataAccessNotifications);
        // Remove as class property
        classProperties = new NSMutableArray(virtualColumnEntity.classProperties());
        classProperties.removeObject(dataAccessNotifications);
        virtualColumnEntity.setClassProperties(classProperties);
        classProperties = new NSMutableArray(virtualColumnEntity.classProperties());
        classProperties.removeObject(dataAccessNotifications);
        virtualColumnEntity.setClassProperties(classProperties);
        columnDataAccessJoin = new EOJoin(virtualColumnEntity.attributeNamed("columnID"), dataAccessColumnNotificationEntity.attributeNamed("columnId"));
        dataAccessNotifications.addJoin(columnDataAccessJoin);
        dataAccessNotifications.setJoinSemantic(EORelationship.InnerJoin);
        dataAccessNotifications.setToMany(true);
        dataAccessNotifications.setDeleteRule(EOClassDescription.DeleteRuleCascade);
        dataAccessNotifications.setPropagatesPrimaryKey(true);

        // 2B. Add flattened dataAccessNotifications from VirtualColumn <--> DataAccess
        dataAccessNotifications = new EORelationship();
        dataAccessNotifications.setName("dataAccessNotifications");
        virtualColumnEntity.addRelationship(dataAccessNotifications);
        dataAccessNotifications.setDefinition("columnDataAccessNotifications.dataAccess");
        dataAccessNotifications.setJoinSemantic(EORelationship.InnerJoin);
        dataAccessNotifications.setDeleteRule(EOClassDescription.DeleteRuleNullify);

        // 3A. Add non-class property dataAccessNotifications from VirtualLookupColumn <--> DataAccessColumnNotification
        dataAccessNotifications = new EORelationship();
        dataAccessNotifications.setName("columnDataAccessNotifications");
        virtualLookupColumnEntity.addRelationship(dataAccessNotifications);
        // Remove as class property
        classProperties = new NSMutableArray(virtualLookupColumnEntity.classProperties());
        classProperties.removeObject(dataAccessNotifications);
        virtualLookupColumnEntity.setClassProperties(classProperties);
        columnDataAccessJoin = new EOJoin(virtualLookupColumnEntity.attributeNamed("columnID"), dataAccessColumnNotificationEntity.attributeNamed("columnId"));
        dataAccessNotifications.addJoin(columnDataAccessJoin);
        dataAccessNotifications.setJoinSemantic(EORelationship.InnerJoin);
        dataAccessNotifications.setToMany(true);
        dataAccessNotifications.setDeleteRule(EOClassDescription.DeleteRuleCascade);
        dataAccessNotifications.setPropagatesPrimaryKey(true);

        // 3B. Add flattened dataAccessNotifications from VirtualLookupColumn <--> DataAccess
        dataAccessNotifications = new EORelationship();
        dataAccessNotifications.setName("dataAccessNotifications");
        virtualLookupColumnEntity.addRelationship(dataAccessNotifications);
        dataAccessNotifications.setDefinition("columnDataAccessNotifications.dataAccess");
        dataAccessNotifications.setJoinSemantic(EORelationship.InnerJoin);
        dataAccessNotifications.setDeleteRule(EOClassDescription.DeleteRuleNullify);

        // 4A. Add non-class property dataAccessNotifications from VirtualUserColumn <--> DataAccessColumnNotification
        dataAccessNotifications = new EORelationship();
        dataAccessNotifications.setName("columnDataAccessNotifications");
        virtualUserColumnEntity.addRelationship(dataAccessNotifications);
        // Remove as class property
        classProperties = new NSMutableArray(virtualUserColumnEntity.classProperties());
        classProperties.removeObject(dataAccessNotifications);
        virtualUserColumnEntity.setClassProperties(classProperties);
        columnDataAccessJoin = new EOJoin(virtualUserColumnEntity.attributeNamed("columnID"), dataAccessColumnNotificationEntity.attributeNamed("columnId"));
        dataAccessNotifications.addJoin(columnDataAccessJoin);
        dataAccessNotifications.setJoinSemantic(EORelationship.InnerJoin);
        dataAccessNotifications.setToMany(true);
        dataAccessNotifications.setDeleteRule(EOClassDescription.DeleteRuleCascade);
        dataAccessNotifications.setPropagatesPrimaryKey(true);

        // 4B. Add flattened dataAccessNotifications from VirtualUserColumn <--> DataAccess
        dataAccessNotifications = new EORelationship();
        dataAccessNotifications.setName("dataAccessNotifications");
        virtualUserColumnEntity.addRelationship(dataAccessNotifications);
        dataAccessNotifications.setDefinition("columnDataAccessNotifications.dataAccess");
        dataAccessNotifications.setJoinSemantic(EORelationship.InnerJoin);
        dataAccessNotifications.setDeleteRule(EOClassDescription.DeleteRuleNullify);
    }



    /**
     * Creates formatters for each date and number format and removes the format strings so that they do not get used by accident.
     */
    protected void createFormatters()
    {
        properties().createDateFormatter("LongDateFormat");
        properties().createDateFormatter("StandardDateFormat");
        properties().createDateFormatter("CompressedDateFormat");
        properties().createDateFormatter("DateAndTimeFormat");

        // The date with optional time input format needs to be handled specially as it uses a
        // non-standard formatter
        NSTimestampFormatter dateFormatter = new OptionalTimeDateFormatter(
            properties().stringPropertyForKey("InputDateFormat"),
            properties().stringPropertyForKey("DefaultInputTime"));
        properties().addPropertyForKey(dateFormatter, "InputDateFormatter");
        properties().removePropertyForKey("InputDateFormat");

        // The date only input format needs to be handled specially as it uses a
        // non-standard formatter
        dateFormatter = new StrictDateFormatter(properties().stringPropertyForKey("InputDateOnlyFormat"));
        properties().addPropertyForKey(dateFormatter, "InputDateOnlyFormatter");
        properties().removePropertyForKey("InputDateOnlyFormat");

        properties().createNumberFormatter("StandardNumberFormat");
        properties().createNumberFormatter("InputNumberFormat");

        // Create formatters for DataAccess columns.  These formatters are registered under the format patterns for easy retrieval.
        Enumeration formatEnumeration = properties().arrayPropertyForKey("DataAccessDateFormats").objectEnumerator();
        while (formatEnumeration.hasMoreElements())
        {
            String formatPattern = (String)formatEnumeration.nextElement();
            dateFormatter = new NSTimestampFormatter(formatPattern);
            properties().addPropertyForKey(dateFormatter, formatPattern);
        }

        formatEnumeration = properties().arrayPropertyForKey("DataAccessNumberFormats").objectEnumerator();
        while (formatEnumeration.hasMoreElements())
        {
            String formatPattern = (String)formatEnumeration.nextElement();
            NSNumberFormatter numberFormatter = new NSNumberFormatter(formatPattern);
            properties().addPropertyForKey(numberFormatter, formatPattern);
        }

    }



    /**
     * This is called if no data is found in the database.  It inserts some basic data and
     * terminates the application.  It will run normall on the next run.
     */
    protected void bootstrapDatabase()
    {
        EOEditingContext ec = newEditingContext();
        ec.lock();
        try
        {
            // A propertly intialized database will have at least one of each.
            boolean hasUnits = (SMEOUtils.countOfEntityNamed("OrgUnit", ec) > 0);
            boolean hasUsers = (SMEOUtils.countOfEntityNamed("User", ec) > 0);
            boolean hasGroups = (SMEOUtils.countOfEntityNamed("Group", ec) > 0);

            if (hasUnits && hasUsers && hasGroups)
            {
                DebugOut.println(1, "Database already initialized");
            }
            else if (hasUnits || hasUsers || hasGroups)
            {
                DebugOut.println(1, "Can not start.  Database has inconsistent data for Websites, Units, Users, or Groups.  Contact support.");
                terminate();
            }
            else
            {
                DebugOut.println(1,"Database is empty, proceeding with DB initialization...");

                // Load the Bootstrap items and ensure they are present.  If any of these items are missing the application is terminated.
                DebugOut.println(1,"Loading bootstrap information.");
                properties().addPropertiesFromFile("GVCSiteMakerBootstrap.plist", "GVCSMCore");
                properties().addPropertiesFromFile("GVCSiteMakerBootstrap.plist", "GVCSMCustom");
                requireProperty("AdminSiteName");
                requireProperty("AdminID");
                requireProperty("AdminPassword");
                requireProperty("RootUnitName");
                requireProperty("InternalUsersGroupName");
                requireProperty("PublicGroupName");
                requireProperty("SiteStyles");
                requireProperty("DefaultStyle");
                requireProperty("SectionTypes");
                requireProperty("ColumnTypes");
                requireProperty("TaskStatuses");

                // Validate entries for the Website section style templates.
                if ( ! isTerminating())
                {
                    if ( ! (properties().propertyForKey("SiteStyles") instanceof NSDictionary))
                    {
                        DebugOut.println(1, "siteStyles property is not a properly formed dictionary, it is a " + properties().propertyForKey("SiteStyles").getClass());
                        terminate();
                    }
                    else if ( ((NSDictionary)properties().propertyForKey("SiteStyles")).count() == 0)
                    {
                        DebugOut.println(1, "siteStyles has not styles defined");
                        terminate();
                    }
                    else if ( ((NSDictionary)properties().propertyForKey("SiteStyles")).objectForKey(properties().stringPropertyForKey("DefaultStyle")) == null)
                    {
                        DebugOut.println(1, "defaultStyle '" + properties().stringPropertyForKey("DefaultStyle") + "' was not found in siteStyles");
                        terminate();
                    }
                    else
                    {
                        NSDictionary styleDictionary = ((NSDictionary)properties().propertyForKey("SiteStyles"));
                        Enumeration styleEnumeration = styleDictionary.keyEnumerator();
                        while (styleEnumeration.hasMoreElements())
                        {
                            String styleName = (String)styleEnumeration.nextElement();
                            String templateContents = ResourceManagerAdditions.stringFromResource("StyleTemplates/" + styleName + ".html", "GVCSMCustom");
                            if (templateContents == null)
                            {
                                DebugOut.println(1, "Can not load style 'StyleTemplates/" + styleName + ".html' for style named " + styleName);
                                terminate();
                                break;
                            }
                        }
                    }
                }
                else
                {
                    DebugOut.println(1, "Bootstrap properties not properly defined.  Bootstrap aborting...");
                }

                if ( ! isTerminating())
                {
                    // This initializes the DB with users, accounts, and the templates
                    InitDatabase.initDatabase(ec);

                    // When initializing on FrontBase subsequent fetches can produce an "unable to increment snapshot" error.   Terminate the application to clean this up.  (This hack seemed like a better solution than trying to determine why this happens.  - ch).
                    DebugOut.println(1,"DB initialization complete, shutting down...");
                    terminate();
                }
            }
        }
        finally
        {
            ec.unlock();
            ec.dispose();
        }
    }



    /**
     * @return the URL to the main page of this installation
     */
    public String mainPageURL()
    {
        return SMActionURLFactory.mainPageURL();
    }



    /**
     * Catching all other exceptions.
     */
    public WOResponse handleException( java.lang.Exception anException, WOContext aContext )
    {
        WOResponse exceptionPage =  null;

        try
        {
            // Handle java.lang.VirtualMachineError (probably OutOfMemory) by terminating the app abruptly.
            if ( (anException instanceof NSForwardException) &&
                 (((NSForwardException) anException).originalException() instanceof VirtualMachineError) )
            {
                Runtime.getRuntime().exit(1);
            }

            DebugOut.println(0, "Handle Exception:" + anException.toString() );
            DebugOut.println(0, ErrorLogger.getStackTrace(anException));


            // These two exceptions seems to indicate that a VirtualColumnType has all null values.
            // This has been a particular problem at UM and seems isolated to ColumnType.  To attempt
            // recovery we invalidate all objects so that all are refreshed.
            if ( (anException instanceof java.lang.NullPointerException) ||
                 (anException instanceof java.lang.IllegalArgumentException) )
            {
                DebugOut.println(0, "Dumping object cache to recover..." );
                EOObjectStoreCoordinator.defaultCoordinator().lock();
                try
                {
                    EOObjectStoreCoordinator.defaultCoordinator().invalidateAllObjects();
                }
                finally
                {
                    EOObjectStoreCoordinator.defaultCoordinator().unlock();
                }
            }

            if ( ! properties().booleanPropertyForKey( "DevelopmentMode" ) )
            {
                exceptionPage = ErrorLogger.handleException( anException, aContext );
            }
        }
        catch (Throwable t)
        {
            // Our exception handler raised an exception!
            DebugOut.println(0, "Handling failed with Exception:" + t.toString() );
            DebugOut.println(0, ErrorLogger.getStackTrace(t));
        }

        // Handle development mode and exceptions raised by our custom error handler.  super must be WOApplication for this to work.
        if (exceptionPage == null)
        {
            exceptionPage = super.handleException(anException, aContext);
        }

        return exceptionPage;
    }



    /**
     * For when a user backtracks too far.
     */
    public WOResponse handlePageRestorationErrorInContext( WOContext aContext ) {
        // Don't do this if we're working on the app.
        if( properties().booleanPropertyForKey( "DevelopmentMode" ) ) {
            return super.handlePageRestorationErrorInContext (aContext);
        }

        return ErrorLogger.handlePageRestorationErrorInContext( aContext );
    }



    /**
     * For when a session times out.
     */
    public WOResponse handleSessionRestorationErrorInContext (WOContext aContext) {
        // Don't do this if we're working on the app.
        if( properties().booleanPropertyForKey( "DevelopmentMode" ) ) {
            return super.handleSessionRestorationErrorInContext (aContext);
        }

        return ErrorLogger.handleSessionRestorationErrorInContext( aContext );
    }



    /**
     * Overridden to return the standard error page and log the problem.
     */
    public WOResponse handleActionRequestError(WORequest aRequest,
                                               Exception exception,
                                               String reason,
                                               WORequestHandler aHandler,
                                               String actionClassName,
                                               String actionName,
                                               Class actionClass,
                                               WOAction actionInstance)
    {
        WOResponse exceptionPage;

        if ( ! properties().booleanPropertyForKey( "DevelopmentMode" ) )
        {
            WOContext context = null;
            if (actionInstance != null)
            {
                context = actionInstance.context();
            }

            exceptionPage = ErrorLogger.handleException( exception, context);
        }
        else
        {
            exceptionPage = super.handleActionRequestError(aRequest, exception,
            reason, aHandler, actionClassName, actionName, actionClass, actionInstance);
        }

        return exceptionPage;
    }



    /**
     * Delegate on EOAdaptorChannel that gets called after a row is fetched.  This is used to log the row contents during debugging.
     */
    public void adaptorChannelDidFetchRow(EOAdaptorChannel object, NSMutableDictionary row)
    {
        DebugOut.println(50, "Fetched row " + row);
    }



    /**
     * Returns the prefix for PageMap.list for this application.  We cannot use name() as this is whatever is setup in Monitor and we don't want to go around renaming config files when the app name in Monitor changes.
     *
     * @return the prefix for PageMap.list for this application.
     */
    public String pageMapPrefix()
    {
        throw new RuntimeException("Forgot to override pageMapPrefix() in Application");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the name of the file that defines the page mapping, pageMapPrefix() + "PageMap.plist".
     *
     * @return the name of the file that defines the page mapping
     */
    public String pageMapFileName()
    {
        return pageMapPrefix() + "PageMap.plist";
    }



    /**
     * Overidden to provide debugging hooks and invalid cookie repair.
     */
    public WOResponse dispatchRequest(WORequest request)
    {
        // If this does not happen first time through and after each large upload, about 30 MB of memory just "goes missing" and is never recovered!
        // I'd love to see an explanation of that.  I can see that more would get collected here are there is no longer a reference to the last request.
        // However if this is delayed until after the next request is dispatched the memory is never recovered.  It must be done here.
        // This also requires setPageRefreshOnBacktrackEnabled(false) for this to work,
        // otherwise the upload is cached in the session and all the garbage collecting in the world won't do any good.
        if (shouldCleanupPreviousRequest)
        {
            shouldCleanupPreviousRequest = false;
            DebugOut.println(10, "Collecting garbage before dispatching request");
            Runtime.getRuntime().gc();
        }

        WOResponse response = super.dispatchRequest(request);

        // If the last request was large force garbage collection and flag it to be done again before the next request is processed.
        if (request.content().length() > 1000000)
        {
            DebugOut.println(10, "Collecting garbage after dispatching request");
            shouldCleanupPreviousRequest = true;
            Runtime.getRuntime().gc();
        }

        return response;
    }




    /**
     * If the woadaptor sees a session ID / instance number in a request it will send the request to an instance even if that instance is refusing new requests and the session has expired (or was from a previous run of that instance).  This is because the adaptor has no way of knowing if the session has expired.  This results in new sessions getting created when Refuse New Sessions is On.  We can see this in the application log: <br><br>
     * <pre>
     * [2002-08-26 16:36:52 EDT] <WorkerThread16> <WOApplication> !!! _initializeSessionInContext: called with refuseNewSessions set !!!
     * </pre><br><br>
     * This can result in the application taking a long time to shut down as new session can be created as long as all users do not close their browsers.  This method handles this situation by expiring the wosid and woinst cookies and then having the browser reload the page.  When the request is made the second time it will not have any session ID or instance number and so will be directed to a non-refusing instance, if any.<br>
     * <br>
     * <b>NOTE</b>: This ONLY works if the sessionID is stored in cookies and not in the URL!  It can be extended to handle this.
     *
     * @return WOResponse with expired wosid and woinst cookies and a content of a 1 second meta refresh so that the URL is re-requested.
     */
    protected WOResponse responseForRefusedRequest()
    {
        DebugOut.println(1, "Creating response...");
        WOResponse result = new WOResponse();

        // Create wosid and woinst cookies in the far past so that they will be removed from the user's browser.
        NSTimestamp dateInPast = (new NSTimestamp()).timestampByAddingGregorianUnits(-10, 0, 0, 0, 0, 0);
        WOCookie wosidCookie = new WOCookie("wosid", "refused", "/", null, dateInPast, false);
        WOCookie woinstCookie = new WOCookie("woinst", "-1", "/", null, dateInPast, false);

        // Have to do this instead of calling addCookie() as cookies() is not copied into the headers outside of the R-R loop.
        NSMutableArray killedCookies = new NSMutableArray(2);
        killedCookies.addObject(wosidCookie.headerString());
        killedCookies.addObject(woinstCookie.headerString());
        result.setHeaders(killedCookies, "set-cookie");

        // Have to use a meta refresh instead of a redirect as NS 4.7 chokes (just sits spinning) when redirected to the same URL that it had requested.  Other browsers do not exhibit this problem.
        result.setContent("<html><head><meta HTTP-EQUIV=\"Refresh\" CONTENT=\"1\"></head><body></body></html>");
        result.setHeader("text/html", "content-type");
        result.setStatus(300);
        result.disableClientCaching();
        result.setHTTPVersion("HTTP/1.1");

        return result;
    }



    /**
     * @return the name of the framework used to customize and configure the applications
     */
    public String configurationFrameworkName()
    {
        return "GVCSMCustom";
    }



    /**
     * Loads MixedMedia Content Configuration from XML files
     */
    protected void loadMixedMediaConfigurationFiles()
    {
        try
        {
            //Get mapping file to be used for decoding
            DebugOut.println(1, "Loading Mixed Media Mapping from GVCSMCore file " + MixedMediaMappingModelFile);
            String modelFile = resourceManager().pathURLForResourceNamed(MixedMediaMappingModelFile, "GVCSMCore",  null).toString();
            WOXMLDecoder decoder = WOXMLDecoder.decoderWithMapping(modelFile);

            NSMutableDictionary configurations = mixedMediaConfigurationFiles(NSBundle.bundleForName("GVCSMCore"), MixedMediaXMLDirectory, decoder);
            configurations.addEntriesFromDictionary(mixedMediaConfigurationFiles(NSBundle.bundleForName("GVCSMCustom"), MixedMediaXMLDirectory, decoder));
            mixedMediaContentConfigurations = configurations.allValues();
        }
        catch (Exception ex) {
            terminate();
            DebugOut.println(1, "Error Loading Mixed Media Mapping: " + ex);
            ex.printStackTrace();
        }

        /** ensure [mixedMediaContentConfigurations_is_not_null] mixedMediaContentConfigurations != null; **/
    }



    /**
     * Returns the mixed media configuration files from bundle and directory
     *
     * @param bundle the NSBundle to take resources from
     * @param directory name of the directory in bundle to look for XML resources
     * @param decoder WOXMLDecoder used to decode a Mixed Media XML definition
     * @return NSDictionary keyed on title of Mixed Media definitions from the indicated bundle and directory
     */
    protected NSMutableDictionary mixedMediaConfigurationFiles(NSBundle bundle, String directory, WOXMLDecoder decoder)
    {
        /** require [valid_bundle] bundle != null;
                    [valid_directory] directory != null;
                    [valid_decoder] decoder != null;
         **/
        NSMutableDictionary configurations = new NSMutableDictionary();

        try
        {
            DebugOut.println(1, "Loading Mixed Media XML definitions from " + bundle.name() + "/" + directory);
            Enumeration xmlFileResourcePathsEnumerator = bundle.resourcePathsForResources("xml", "MixedMediaXML").objectEnumerator();
            while(xmlFileResourcePathsEnumerator.hasMoreElements())
            {
                String resourcePath = (String) xmlFileResourcePathsEnumerator.nextElement();
                DebugOut.println(1,"Decoding XML file: " + resourcePath);
                InputSource inputSource = new InputSource(bundle.inputStreamForResourcePath(resourcePath));
                MixedMediaContentConfiguration decodedConfig = (MixedMediaContentConfiguration) decoder.decodeRootObject(inputSource);
                DebugOut.println(1,"Loaded MixedMedia configuration file for" + decodedConfig.title());
                decodedConfig.validateTemplate();
                configurations.setObjectForKey(decodedConfig, decodedConfig.title());
            }
        }
        catch (Exception ex) {
            terminate();
            DebugOut.println(1, "Error decoding XML file: " + ex);
            ex.printStackTrace();
        }

        return configurations;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns true iff the passed in URL has a domain that exists in the list of valid domains.
     *
     * @param url the URL to test
     * @return true iff the passed in URL is valid
     */
    public boolean urlHasValidDomain(String url)
    {
        /** require [valid_url] url != null; **/
        Enumeration validDomainEnumerator = properties().arrayPropertyForKey("ValidDomains").objectEnumerator();
        while (validDomainEnumerator.hasMoreElements())
        {
            String validDomain = (String)validDomainEnumerator.nextElement();
            if ((url.startsWith("http://" + validDomain)) || (url.startsWith("https://" + validDomain)))
            {
                return true;
            }
        }
        return false;
    }



    /**
     * Returns true iff this application is the admin application.
     *
     * @return true iff this application is the admin application
     */
    public boolean isAdminApplication()
    {
        return getClass().getName().equals("com.gvcsitemaker.admin.appserver.Application");
    }



    /**
     * Returns true iff this application is using integer PKs (as opposed to byte(24))
     *
     * @return true iff this application is using integer PKs
     */
    public boolean isUsingIntegerPKs()
    {
        return ! EOModelGroup.defaultGroup().modelNamed("VirtualTables").entityNamed("VirtualRow").attributeNamed("virtualRowID").className().equals("com.webobjects.foundation.NSData");
    }



}
