package net.global_village.woextensions;

import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;

import er.extensions.appserver.ajax.*;
import er.extensions.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.eofvalidation.*;
import net.global_village.foundation.*;


/**
 * <p>Extension of WOSession with localization support and managed (locked) editing contexts. There are four related areas that need localization: WOComponents, validation error messages,
 * database lookup tables (Generic Objects), and EO Based Localization</p>
 * <dl>
 *   <dt>WOComponents
 *   <dd>There are two ways to localize a WO component: either localize the entire component, or localized just the strings.
 *       To do the former, merely include a copy of the component for each language in a <Language Name>.lproj directory.
 *       You might need to do this if you have a component that needs it's structure changed when the language changes in order to handle the various
 *       language differences. If you don't need a different component for each language, there are two options.  One is to use bindings like
 *       value = @localize.KeyForString, @see localizedStringInContext(String, WOContext).  The otther is to use WOOgnl key paths and one of the
 *       <code>localizedString</code> methods from this class.  These keys will go into the value binding of <code>WOString</code>, for example, and will look like
 *       <code>~session().localizedString(\"String to localize\")</code> or
 *       <code>~session().localizedString(\"String to localize\", \"StringsFileNameWithoutExtension\", \"FrameworkNameWithoutExtension\")</code>.<br>
 *       <em>Note</em>: you will need to subclass WOApplication from this package (or initialize WOOgnl yourself) if you want to use this facility.
 *
 *   <dt>Validation Error Messages
 *   <dd>Use these as normal, except use <code>Session.localizedMessage</code> (or the WOOgnl key path <code>~session().localizedMessage(exception)</code>
 *       for components) instead of <code>EOFValidationException.getLocalizedMessage</code>.
 *       The exact same search order for non-localized validation error messages is used, except languages are searched <em>depth-first</em>.
 *       (e.g., when looking for a key, all possibilities are tested within a single langauge, and it only moves on to the the next language if it can't find one).
 *
 *   <dt>Database Lookup Tables
 *   <dd>Once again, use WOOgnl key paths, as in <code>~myGenericObject().localizedName(session().languages())</code>, or use the Java call, similar
 *       to the WOOgnl key path.
 *
 *   <dt>EO Based Localization
 *   <dd>Use localizedEOStringExists and localizedEOString as a gateway to the functions in EOValidation's LocalizationEngine.
 * </dl>
 *
 * <p>Note: this currently uses the default WO language detection. This means that locale-specific language dialects are folded into a single language,
 * i.e. EN-CA (Canadian English), EN-GB (British English), and EN-US (US English) all resolve as "English". If necessary, use the code from
 * Chapter 8 of Practical WO to detect and use locale-specific languages. This code was not added here, as sometimes one does not want to
 * have to do different translations for each language dialect.</p>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 26$
 */
public class WOSession extends ERXAjaxSession
{
    private static final long serialVersionUID = -926657844329305028L;

    /** This class manages the editing contexts vended by this session. */
    protected MultiECLockManager ecLockManager;

    /** This is used to detect when the user's language preference needs to be changed.  */
    protected String lastSeenLanguages = null;

    /** This is an optimization used to cached strings previously localized.  */
    protected NSMutableDictionary cachedLocalizedStrings = new NSMutableDictionary();


    /**
     * Returns the session being used in the current thread.  Returns null if thread not in R-R loop,
     * in R-R loop before session.awake(), or in in R-R loop after session.sleep().
     *
     * @return the session being used in the current thread, null if thread not in R-R loop
     * or before session.awake()
     */
    public static WOSession session()
    {
        return (WOSession) ERXThreadStorage.valueForKey("session");
    }



    /**
     * Sets the session being used by the current thread.  This is called by awake() and sleep().
     *
     * @param session the session being used by the current thread
     */
    public static void setSession(WOSession session)
    {
        ERXThreadStorage.takeValueForKey(session, "session");
    }



    /**
     * Method to handle terminating session upon logout (forced or requested).
     */
    public static void logout(com.webobjects.appserver.WOSession session, WOResponse response)
    {
        /** require [valid_session] session != null;  [valid_response] response != null;  **/

        // Try to avoid deadlock when multiple requests came in for a session that is getting terminated
        // Ajax components can cause this
        session.setTimeOut(WOApplication.appProperties().intPropertyForKey("SessionTerminateTimeOutInSeconds"));

        // This prevents WO from returning a cookie from the soon to be expired session
        // and instructs the browser to discard the cookie that it has for the session
        session.setStoresIDsInCookies(false);
        discardSessionCookies(response);

        session.defaultEditingContext().revert();
    }




    /**
     * Sends adds expired cookies for session ID and instance number to response.
     */
    public static void discardSessionCookies(WOResponse response)
    {
        /** require [valid_response] response != null;  **/
        WOCookie killSession = new WOCookie(WOApplication.application().sessionIdKey(), ".", "/", null, 0, true);
        response.addCookie(killSession);

        // Leave the instance number there during development for apps using rewrites etc.
        if ( ! ((WOApplication)WOApplication.application()).developmentMode())
        {
            WOCookie killInstance = new WOCookie(WOApplication.application().instanceIdKey(), ".", "/", null, 0, true);
            response.addCookie(killInstance);
        }
    }



    /**
     * Designated constructor.
     */
    public WOSession()
    {
        super();

        // This needs to happen before anyone uses the default editing context
        // setDefaultEditingContext also locks the ec
        setDefaultEditingContext(application().newEditingContext());

        NSLog.out.appendln("Creating session " + sessionID());
        NSLog.out.appendln("Current active sessions: "
                + application().activeSessionsCount());

        ecLockManager = new MultiECLockManager(application().editingContextClass());
    }



    /**
     * Overridden to lock any editing context's registered with the lock manager.
     */
    public void awake()
    {
        NSLog.out.appendln("Awake session " + sessionID());
        NSLog.out.appendln("====================================");
        try
        {
            WOSession.setSession(this);
        	ecLockManager().lock();
        }
        catch(Throwable t)
        {
    		// DANGEROUS: we *must* eat this exception or the whole app might deadlock
        	NSLog.err.appendln("********** EXCEPTION in WOSession.awake() calling ecLockManager.lock(): " + t);
        }
        super.awake();
    }



    /**
     * Overridden to provide log messages and to unlock any editing context's registered with the lock manager.
     */
    public void sleep()
    {
        // Need to check as this gets called after terminate() when logging out
        synchronized (ecLockManager)
        {
            if (ecLockManager().isLocked())
            {
            	try
            	{
            		ecLockManager().unlock();
                    WOSession.setSession(null);
            	}
            	catch (Throwable t)
            	{
            		// DANGEROUS: we *must* eat this exception or the whole app might deadlock
                	NSLog.err.appendln("********** EXCEPTION in WOSession.sleep() calling ecLockManager.unlock(): " + t);
            	}
            }
        }

        super.sleep();

        NSLog.out.appendln("sleep session " + sessionID());
        NSLog.out.appendln("====================================");
    }



    /**
     * Skips caching the page if the page or response does not want to be cached.
     *
     * @see ERXAjaxSession#savePage(com.webobjects.appserver.WOComponent)
     * @see Response#isPageCachingDisabled()
     *
     * @param page the WOComponent instance to cache for this request
     */
    public void savePage(WOComponent page)
    {
        WOResponse response = context().response();
        boolean isPageCachingDisabled = response instanceof Response && ((Response)response).isPageCachingDisabled();
        if ( ! isPageCachingDisabled)
        {
            super.savePage(page);
        }
    }



    /**
     * Method to handle terminating session upon logout (forced or requested).
     * @see #logout(WOSession, WOResponse)
     */
    public void logout(WOResponse response)
    {
        WOSession.logout(this, response);
    }



    /**
     * Unlock lock manager and diagnostic messages.
     */
    public void terminate()
    {
        // Need to make sure this is unlocked so that editing contexts nested in the defaultEditingContext
        // don't hold locks on it when it gets disposed
        synchronized (ecLockManager)
        {
            if (ecLockManager().isLocked())
            {
            	try
            	{
            		ecLockManager().unlock();
            	}
            	catch (Throwable t)
            	{
            		// DANGEROUS: we *must* eat this exception or the whole app might deadlock
                	NSLog.err.appendln("********** EXCEPTION in WOSession.terminate() calling ecLockManager.unlock(): " + t);
            	}
            }
        }

        super.terminate();

        NSLog.out.appendln("Terminated session " + sessionID());
        NSLog.out.appendln("Remaining active sessions: " + WOApplication.application().activeSessionsCount());
    }



    /**
     * Returns a new peer editing context that is registered with this session's lock manager.  All
     * editing contexts should be created with this method or it's parent version, below.  The
     * returned editing context is locked if this is called during the Request-Response loop.
     *
     * @return a new editing context that is registered with this session's lock manager
     */
    public EOEditingContext newEditingContext()
    {
        return ecLockManager().newEditingContext();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a new child editing context that is registered with this session's lock manager.  All
     * editing contexts should be created with this method or it's no-parent version, above.  The
     * returned editing context is locked if this is called during the Request-Response loop.
     *
     * @param parentEC the new editing context's parent editing context
     * @return a new editing context that is registered with this session's lock manager
     */
    public EOEditingContext newEditingContext(EOObjectStore parentEC)
    {
        /** require [valid_param] parentEC != null; **/
        return ecLockManager().newEditingContext(parentEC);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Registers the given EC in the lock manager.  Useful for custom-created ECs.
     *
     * @param anEC the editing context to register
     */
    public void registerEditingContext(EOEditingContext anEC)
    {
        /** require [valid_param] anEC != null; **/
        ecLockManager().registerEditingContext(anEC);
    }


    /**
     * Unregisters the given EC from the lock manager.  Useful for custom-created ECs.
     *
     * @param anEC the editing context to unregister
     */
    public void unregisterEditingContext(EOEditingContext anEC)
    {
        /** require [valid_param] anEC != null; **/
        ecLockManager().unregisterEditingContext(anEC);
    }



    /**
     * Return the object managing editing context locking on this session's behalf.
     *
     * @return the object managing editing context locking on this session's behalf
     */
    protected MultiECLockManager ecLockManager()
    {
        return ecLockManager;
    }



    /**
     * Despite the name, this is actually the path used in the cookies for the session ID and instance number.
     * Returning just the root '/' ensures that the cookies are always sent for any request to the domain.
     * This was needed so that they are sent with URLs which will be rewritten.
     * Note that super.domainForIDCookies() would return: /cgi-bin/WebObjects/AppName.woa
     *
     * @return "/"
     */
    public String domainForIDCookies()
    {
        return "/";
    }



    /**
     * Overidden to use RequestUtilities.browserLanguages so that the list of languages
     * can include country specific differences.
     */
    public NSArray languages()
    {
        if(context() != null)
        {
            String languagesFromBrowswer = RequestUtilities.rawBrowserLanguages(context().request());
            if ((lastSeenLanguages == null) || ( ! lastSeenLanguages.equals(languagesFromBrowswer)))
            {
                lastSeenLanguages = languagesFromBrowswer;
                cachedLocalizedStrings = new NSMutableDictionary();
                setLanguages(RequestUtilities.browserLanguages(context().request()));
                NSLog.debug.appendln("Set session languages to " + super.languages());
            }
        }

        return super.languages();
    }



    /**
     * Returns a localized error message from on the passed exception.
     *
     * @param exception the exception to localize
     * @return a localized error message from on the passed exception.
     */
    public String localizedMessage(Throwable exception)
    {
        /** require [valid_param] exception != null; **/

        String localizedMessage;

        if (exception instanceof EOFValidationException)
        {
            EOFValidationException validationException = (EOFValidationException)exception;
            localizedMessage = validationException.getLocalizedMessage(languages());
        }
        else
        {
            // It is not one of our exceptions, so there is not much we can do.
            localizedMessage = exception.getLocalizedMessage();
        }

        return localizedMessage;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the given string, localized in the session's language order. If the strings table for the current locale does not contain the given string, the next locale is tried, and so on. If no locale's contain the given string, the string itself is returned.
     *
     * @param string the string to localize
     * @param table the name of the table to look for in the framework
     * @param framework the name of the framework to look in
     * @return the given string, localized in the session's language order
     */
    public String localizedString(String string, String table, String framework)
    {
        /** require [valid_param] string != null; **/
        return WOApplication.application().resourceManager().stringForKey(string, table, string, framework, languages());
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the given string, localized in the session's language order. If the strings table for the current locale does not contain the given string, the next locale is tried, and so on. If no locale's contain the given string, the string itself is returned. Looks for a strings file in the main bundle, in a file named "<Main Bundle Name>.strings".
     *
     * @param string the string to localize
     * @return the given string, localized in the session's language order
     */
    public String localizedString(String string)
    {
        /** require [valid_param] string != null; **/
        return localizedString(string, NSBundle.mainBundle().name(), NSBundle.mainBundle().name());
        /** ensure [valid_result] Result != null; **/
    }




    /**
     * Returns the given string, localized in the session's language order. The string is sought in several places:<br/>
     * <ol>
     * <li>As ComponentName.string (e.g. SearchForm.firstName) in PageName.strings (e.g. SearchPage.strings) in the configuration framework's bundle.</li>
     * <li>As string (e.g. firstName) in ComponentName.strings (e.g. SearchForm.strings) in the configuration framework's bundle.</li>
     * <li>As ComponentName.string in PageName.strings in the main bundle.</li>
     * <li>As ComponentName.string in PageName.strings in the page's bundle.</li>
     * <li>As string in ComponentName.strings in the main bundle.</li>
     * <li>As string in ComponentName.strings in the component's bundle.</li>
     * </ol>
     * If no locale's contain the given string, the string itself is returned.
     *
     * @param string the string to localize
     * @param context WOContext indicating current page and component
     * @return the given string, localized in the session's language order
     */
    public String localizedStringInContext(String string, WOContext context)
    {
        /** require [valid_string] string != null;
                    [valid_context] context != null;
                    [known_page] context.page() != null;  **/

        String pageName;
        String componentName;
        String componentQualifiedName;

        boolean isLocalizingForPage;

        // When the component is the top level page, we can avoid some redundant searches
        if (context.page() == context.component())
        {
            pageName = componentName = ClassAdditions.unqualifiedClassName(context.page().getClass());
            componentQualifiedName = string;
            isLocalizingForPage = true;
        }
        else
        {
            pageName = ClassAdditions.unqualifiedClassName(context.page().getClass());
            componentName = ClassAdditions.unqualifiedClassName(context.component().getClass());
            componentQualifiedName = componentName + "." + string;
            isLocalizingForPage = false;
        }

        String uniqueKey = pageName + "." + componentQualifiedName;

        // Use previously cached value if found
        String localizedString = (String) cachedLocalizedStrings().objectForKey(uniqueKey);
        if (localizedString != null)
        {
        	return localizedString;
        }

        WOResourceManager localizer = WOApplication.application().resourceManager();

        // First, check the configuration framework if there is one:
        String configurationFrameworkName = application().configurationFrameworkName();
        if (configurationFrameworkName != null)
        {
             localizedString = localizer.stringForKey(componentQualifiedName, pageName, null, configurationFrameworkName, languages());

            if (localizedString == null && ! isLocalizingForPage)
            {
                localizedString = localizer.stringForKey(string, componentName, null, configurationFrameworkName, languages());
            }
        }

        // The second place to look is in the Application
        if (localizedString == null)
        {
            localizedString = localizer.stringForKey(componentQualifiedName, pageName, null, null, languages());
        }

        // Then check the page's bundle if not the application
        NSBundle mainBundle = NSBundle.mainBundle();
        if (localizedString == null)
        {
            NSBundle pageBundle = NSBundleAdditions.bundleForClass(context.page().getClass());
            if ( ! pageBundle.equals(mainBundle))
            {
                localizedString = localizer.stringForKey(componentQualifiedName, pageName, null, pageBundle.name(), languages());
            }
        }

        // Next, look for an application wide definition for the component
        if (localizedString == null && ! isLocalizingForPage)
        {
            localizedString = localizer.stringForKey(string, componentName, null, null, languages());
        }

        // Finally check the components's bundle the component is not the page and not in the application bundle
        if (localizedString == null && ! isLocalizingForPage)
        {
           NSBundle componentBundle = NSBundleAdditions.bundleForClass(context.component().getClass());
           if ( ! componentBundle.equals(mainBundle)) {
                 localizedString = localizer.stringForKey(string, componentName, null, componentBundle.name(), languages());
            }
        }

        // Handle the string not being found
        if (localizedString == null)
        {
            localizedString = string;

            // Not the most beautiful piece of code, but useful when debugging
            NSLog.out.appendln("Unable to localize String " + string + " for languages " + languages());
            NSLog.out.appendln("              pageName " + pageName);
            NSLog.out.appendln("         componentName " + componentName);
            NSLog.out.appendln("componentQualifiedName " + componentQualifiedName);
            if (configurationFrameworkName != null) NSLog.out.appendln("Checked for " + componentQualifiedName + " in table " + pageName + ".strings in framework " + configurationFrameworkName);
            if ((configurationFrameworkName != null) && ! isLocalizingForPage) NSLog.out.appendln("Checked for " + string + " in table " + componentName + ".strings in framework " + configurationFrameworkName);
            NSLog.out.appendln("Checked for " + componentQualifiedName + " in table " + pageName + ".strings in app ");
            if ( ! NSBundleAdditions.bundleForClass(context.page().getClass()).equals(mainBundle)) NSLog.out.appendln("Checked for " + componentQualifiedName + " in table " + pageName + ".strings in framework " + NSBundleAdditions.bundleForClass(context.page().getClass()).name());
            if (! isLocalizingForPage) NSLog.out.appendln("Checked for " + string + " in table " + componentName + ".strings in app " );
            if ((! isLocalizingForPage) && ( ! NSBundleAdditions.bundleForClass(context.component().getClass()).equals(mainBundle))) NSLog.out.appendln("Checking for " + string + " in table " + componentName + ".strings in framework " + NSBundleAdditions.bundleForClass(context.component().getClass()).name());
        }

        // Cache return value for optimization
        cachedLocalizedStrings().setObjectForKey(localizedString, uniqueKey);

        return localizedString;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Cover method for the primary localization existance method for EOFValidation.
     *
     * @param entityName name of the entity to lookup
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @return <code>true</code> if there is a localized string in any of the string tables for any of the languages. Languages are searched depth first.
     */
    public boolean localizedEOStringExists(String entityName, String propertyName, String key)
    {
        /** require
        [valid_entity_param] entityName != null;
        [valid_key_param] (key != null)  && (key.length() > 0);   **/

        return LocalizationEngine.localizedStringExists(entityName, propertyName, key, languages());
    }



    /**
     * Cover method for the primary localization existance method for EOFValidation.
     *
     * @param entity EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @return <code>true</code> if there is a localized string in any of the string tables for any of the languages. Languages are searched depth first.
     */
    public boolean localizedEOStringExists(EOEntity entity, String propertyName, String key)
    {
        /** require
        [valid_entity_param] entity != null;
        [valid_key_param] (key != null)  && (key.length() > 0);          **/

        return LocalizationEngine.localizedStringExists(entity.name(), propertyName, key, languages());
    }



    /**
     * Cover method for the primary localization method for EOFValidation.
     *
     * @param entityName name of the entity to lookup
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @return localized string on entity for propertyName and key
     */
    public String localizedEOString(String entityName, String propertyName, String key)
    {
        /** require
        [valid_entityName_param] entityName != null;
        [valid_key_param] (key != null)  && (key.length() > 0);
        [localization_exists] localizedEOStringExists(entityName, propertyName, key); **/

        return LocalizationEngine.localizedString(entityName, propertyName, key, languages());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Cover method for the primary localization method for EOFValidation.
     *
     * @param entity EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @return localized string on entity for propertyName and key
     */
    public String localizedEOString(EOEntity entity, String propertyName, String key)
    {
        /** require
        [valid_entity_param] entity != null;
        [valid_key_param] (key != null)  && (key.length() > 0);
        [localization_exists] localizedEOStringExists(entity.name(), propertyName, key); **/

        return LocalizationEngine.localizedString(entity.name(), propertyName, key, languages());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * @return dictionary of cached localization keyed on page name and localization key
     */
    protected NSMutableDictionary cachedLocalizedStrings()
    {
    	return cachedLocalizedStrings;
    }



    public WOApplication application()
    {
        return (WOApplication)WOApplication.application();
    }

}
