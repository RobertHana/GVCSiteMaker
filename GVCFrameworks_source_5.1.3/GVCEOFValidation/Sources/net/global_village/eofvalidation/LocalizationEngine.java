package net.global_village.eofvalidation;

import java.util.*;

import org.apache.log4j.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * Implements search location order and key manipulation for EOFValidation used when localizing strings.  See <a href="#localizedStringFromBundle">localizedStringFromBundle</a><br>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 8$
 */
public class LocalizationEngine extends Object
{

    private static Logger logger = LoggerFactory.makeLogger();

    /**
     * Returns <code>true</code> if the given key exists in the given table in the given framework for the given language.
     *
     * @param key the key whose existance we are checking
     * @param tableName the name of the table (without the .strings extension) to search
     * @param frameworkName the name of the framework to check
     * @param languages the preferred language array
     * @return localized string on entity for propertyName and key, or null if not found.
     */
    protected static boolean resourceStringExistsForKey(String key, String tableName, String frameworkName, NSArray languages)
    {
        /** require
        [valid_bundle_param] key != null;
        [valid_entityName_param] tableName != null;
        [valid_entityName_param] frameworkName != null;
        [valid_key_param] languages != null; **/

        return WOApplication.application().resourceManager().stringForKey(key, tableName, null, frameworkName, languages) != null;
    }



    /**
     * Checks for the localized string in one bundle, implementing the order that keys are tried in, and the order that strings files are checked.  See class description for details on how the localized string is located.
     *
     * @param bundle NSBundle to look up this localized string in
     * @param entityName name of the entity to look up this localized string for (
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @param language the language to look for
     * @return localized string on entity for propertyName and key, or null if not found.
     */
    public static String localizedStringFromBundle(NSBundle bundle,
                                                   String entityName,
                                                   String propertyName,
                                                   String key,
                                                   String language)
    {
        /** require
        [valid_bundle_param] bundle != null;
        [valid_entityName_param] entityName != null;
        [valid_key_param] key != null;
        [valid_language_param] language != null; **/

        String localizedString = null;

        // Create the keys that will be searched for
        String genericMessageKey = EOFValidation.EntityGenericName + "." + key; // Entity.<key>
        String partiallyQualifiedKey = entityName + "." + key; // <entityName>.<key>
        String fullyQualifiedKey = null; // Optional <entityName>.<propertyName>.<key>
        boolean hasThreeLevelKey = (propertyName != null) && (propertyName.length() > 0);
        if (hasThreeLevelKey)
        {
            fullyQualifiedKey = entityName + "." + propertyName + "." + key;
        }

        // These are used as string resource names
        String bundleName = bundle.name();

        NSArray languages = new NSArray(language);
        WOResourceManager rm = WOApplication.application().resourceManager();

        if (hasThreeLevelKey && resourceStringExistsForKey(fullyQualifiedKey, entityName, bundleName, languages))
        {
            localizedString = rm.stringForKey(fullyQualifiedKey, entityName, null, bundleName, languages);
        }
        else if (resourceStringExistsForKey(partiallyQualifiedKey, entityName, bundleName, languages))
        {
            localizedString = rm.stringForKey(partiallyQualifiedKey, entityName, null, bundleName, languages);
        }
        else if (hasThreeLevelKey && resourceStringExistsForKey(fullyQualifiedKey, bundleName, bundleName, languages))
        {
            localizedString = rm.stringForKey(fullyQualifiedKey, bundleName, null, bundleName, languages);
        }
        else if (resourceStringExistsForKey(partiallyQualifiedKey, bundleName, bundleName, languages))
        {
            localizedString = rm.stringForKey(partiallyQualifiedKey, bundleName, null, bundleName, languages);
        }
        else if (resourceStringExistsForKey(genericMessageKey, bundleName, bundleName, languages))
        {
            localizedString = rm.stringForKey(genericMessageKey, bundleName, null, bundleName, languages);
        }

        return localizedString;
    }



    /**
     * Checks for the localized string in one bundle, implementing the order that keys are tried in, and the order that strings files are checked.  See class description for details on how the localized string is located.
     *
     * @param bundle NSBundle to look up this localized string in
     * @param entityName name of the entity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @return localized string on entity for propertyName and key, or null if not found
     * @see #localizedStringFromBundle(NSBundle, String, String, String, String)
     */
    public static String localizedStringFromBundle(NSBundle bundle,
                                                   String entityName,
                                                   String propertyName,
                                                   String key)
    {
        /** require
        [valid_bundle_param] bundle != null;
        [valid_entityName_param] entityName != null;
        [valid_key_param] key != null; **/
        return localizedStringFromBundle(bundle, entityName, propertyName, key, Locale.getDefault().getDisplayLanguage());
    }



    /**
     * Primary localization method for EOFValidation.
     * <p>The string to localize is indicated by giving the entity, an optional property name, and a key to uniquely identify the localized string.  These three items are used to form a variety of keys which are tried in order from the most specific to the least specific.  The keys, and the order they are tried are as follows:
     * <p>
     * <TABLE BORDER="1">
     *   <TR>
     *     <TD><b>Type</b></TD><TD><b>Format</b></TD><TD><b>Example</b></TD>
     *   </TR>
     *   <TR>
     *     <TD>Fully qualified</TD><TD>&lt;entityName&gt;.&lt;propertyName&gt;.&lt;key&gt;</TD><TD>Customer.firstName.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>Partially qualified</TD><TD>&lt;entityName&gt;.&lt;key&gt;</TD><TD>Customer.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>Generic</TD><TD>&lt;Entity&gt;.&lt;key&gt;</TD><TD>Entity.nullNotAllowed</TD>
     *   </TR>
     * </TABLE>
     * The fully qualified key is not used if the property name is null or an empty string.
     * <p>These keys are looked up in a series of bundles as follows:
     * <ul>
     * <li>The main bundle.  This allows the application to have the ultimate say in localization.
     * <li>The bundle that this entity is implemented in.  This allows domain specific defaults to be recorded and allows sub-classes to override the localized strings of super classes.
     * <li>The bundle that this entity's super entity (note <b>not</b> super class) is implemented in.  This is repeated until there is no super entity.
     * </ul>
     * <p>Within each bundle, two string resources are checked:
     * <ul>
     * <li>First, the entity level resource named <b>&lt;entityName&gt;.strings</b> (e.g. Customer.strings) which is searched for the fully and partially qualified keys
     * <li>Then, the bundle level resource named <b>&lt;bundleName&gt;.strings</b> (e.g. StoreEO.strings) which is searched for the fully qualified, partially qualified, and generic keys
     * </ul>
     * <p>If this still fails to locate a localized string, the Generic key is searched for in the GVCEOFValidation bundle, in the GVCEOFValidation.strings resource.
     * <br>
     * <p>An example will be useful in understanding the full search order.  Here are three components of a mythical eCommerce store site:
     * <ul>
     * <li><b>GenericObjects</b> - a framework that implements generic base classes for common entities.  We are interested in <I>GenericPerson</I>, and in particular their attribute <I>firstName</I>.
     * <li><b>StoreEO</b> - a framework that implements the enterprise and domain objects that we will use to implement the store.  <I>Customer</I> is implemented here as a sub-class of <I>GenericPerson</I>.
     * <li><b>Store</b> - a WebObjects application that implments the UI part of our little on-line store.
     * </ul>
     * <p>Now imagine that while registering on the site, that a customer forgets to type in their first name.  This is a required field in the EOModel in StoreEO and so EOF flags this as a validation error.  GVCEOFValidation then steps in to provide a localized message.  Here is the order the search would occur in if there were no matches.  A match at any point stops the search.
     * <p>
     * <DIV ALIGN="CENTER"><TABLE BORDER="1">
     *   <TR>
     *     <TD><b>Bundle Examined</b></TD><TD WIDTH="120"><b>Strings File</b></TD><TD><b>Key Used</b></TD>
     *   </TR>
     *   <TR>
     *     <TD>Store.woa</TD>  <TD>Customer.strings</TD>  <TD>Customer.firstName.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>Store.woa</TD>  <TD>Customer.strings</TD>  <TD>Customer.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>Store.woa</TD>  <TD>Store.strings</TD>  <TD>Customer.firstName.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>Store.woa</TD>  <TD>Store.strings</TD>  <TD>Customer.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>Store.woa</TD>  <TD>Store.strings</TD>  <TD>Entity.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>StoreEO.framework</TD>  <TD>Customer.strings</TD>  <TD>Customer.firstName.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>StoreEO.framework</TD>  <TD>Customer.strings</TD>  <TD>Customer.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>StoreEO.framework</TD>  <TD>StoreEO.strings</TD>  <TD>Customer.firstName.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>StoreEO.framework</TD>  <TD>StoreEO.strings</TD>  <TD>Customer.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>StoreEO.framework</TD>  <TD>StoreEO.strings</TD>  <TD>Entity.nullNotAllowed</TD>
     *   </TR>

     *   <TR>
     *     <TD>GenericObjects.framework</TD>  <TD>GenericPerson.strings</TD>  <TD>GenericPerson.firstName.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>GenericObjects.framework</TD>  <TD>GenericPerson.strings</TD>  <TD>GenericPerson.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>GenericObjects.framework</TD>  <TD>GenericObjects.strings</TD>  <TD>GenericPerson.firstName.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>GenericObjects.framework</TD>  <TD>GenericObjects.strings</TD>  <TD>GenericPerson.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>GenericObjects.framework</TD>  <TD>GenericObjects.strings</TD>  <TD>Entity.nullNotAllowed</TD>
     *   </TR>
     *   <TR>
     *     <TD>GVCEOFValidation.framework</TD>  <TD>GVCEOFValidation.strings</TD>  <TD>Entity.nullNotAllowed</TD>
     *   </TR>
     * </TABLE></DIV>
     *
     * @param entityName name of the EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @param language the language to use when looking up the string
     * @return localized string on entity for propertyName and key
     */
    protected static String findLocalizedString(String entityName, String propertyName, String key, String language)
    {
        /** require
        [valid_entityName_param] entityName != null;
        [valid_key_param] key != null;
        [valid_language_param] language != null;
        [key_not_empty] key.length() > 0;   **/

        // The main bundle is considered the "most derived" source and is hence the first place we look
        if (logger.isTraceEnabled()) logger.trace("Three level key [" + entityName + "." + propertyName + "." + key + "]" );
        String localizedString = localizedStringFromBundle(NSBundle.mainBundle(),
                                                           entityName,
                                                           propertyName,
                                                           key,
                                                           language);
        if (logger.isTraceEnabled()) logger.trace("Result from main bundle: " + localizedString);

        // If the localized string is not in the mainBundle() then look on the entity and march up the inheritance chain.
        EOEntity currentEntity = EOModelGroup.defaultGroup().entityNamed(entityName);
        NSBundle bundle;
        while ((localizedString == null) && (currentEntity != null && ! currentEntity.className().equals("EOGenericRecord")))
        {
            bundle = NSBundle.bundleForClass(EOEntityAdditions.safeClassForName(currentEntity, currentEntity.className()));
            if (bundle == null)
            {
                throw new IllegalStateException("Check classpath: can't locate bundle for " + currentEntity.className());
            }

            localizedString = localizedStringFromBundle(bundle,
                                                        currentEntity.name(),
                                                        propertyName,
                                                        key,
                                                        language);
            if (logger.isTraceEnabled()) logger.trace("Result from " + bundle.name() + " bundle: " + localizedString);

            currentEntity = currentEntity.parentEntity();
        }

        // We have still not found a match, check the defaults in this framework.
        if (localizedString == null)
        {
            bundle = NSBundle.bundleForClass(LocalizationEngine.class);
            if (bundle == null)
            {
                throw new IllegalStateException("Check classpath: can't locate bundle for " + LocalizationEngine.class.getCanonicalName());
            }

            localizedString = localizedStringFromBundle(bundle, entityName, propertyName, key, language);
        }

        return localizedString;
    }


    /**
     * Returns <code>findLocalizedString(String, String, String, Locale.getDefault().getDisplayLanguage())</code>.
     *
     * @param entityName name of the EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @return localized string on entity for propertyName and key
     * @see #findLocalizedString(String, String, String, String)
     */
    protected static String findLocalizedString(String entityName, String propertyName, String key)
    {
        /** require
        [valid_entityName_param] entityName != null;
        [valid_key_param] key != null;
        [key_not_empty] key.length() > 0;   **/
        return findLocalizedString(entityName, propertyName, key, Locale.getDefault().getDisplayLanguage());
    }



    /**
     * Returns <code>true</code> if a localized string for the given params exists.
     *
     * @param entityName EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @param language the language to use when looking for the string
     * @return <code>true</code> if a localized string exists for these params
     */
    public static boolean localizedStringExists(String entityName, String propertyName, String key, String language)
    {
        /** require
        [valid_entityName_param] entityName != null;
        [valid_key_param] key != null;
        [valid_language_param] language != null;
        [key_not_empty] key.length() > 0;   **/

        return findLocalizedString(entityName, propertyName, key, language) != null;
    }


    /**
     * Returns <code>true</code> if a localized string for the given params exists.
     *
     * @param entityName EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @return <code>true</code> if a localized string exists for these params
     * @see #localizedStringExists(String, String, String, String)
     */
    public static boolean localizedStringExists(String entityName, String propertyName, String key)
    {
        /** require
        [valid_entityName_param] entityName != null;
        [valid_key_param] key != null;
        [key_not_empty] key.length() > 0;   **/


        return localizedStringExists(entityName, propertyName, key, Locale.getDefault().getDisplayLanguage());
    }


    /**
     * Returns <code>true</code> if a localized string for the given params exists.
     *
     * @param entity EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @param language the language to use when looking for the string
     * @return <code>true</code> if a localized string exists for these params
     * @see #localizedStringExists(String, String, String, String)
     */
    public static boolean localizedStringExists(EOEntity entity, String propertyName, String key, String language)
    {
        /** require
        [valid_entity_param] entity != null;
        [valid_key_param] key != null;
        [valid_language_param] language != null;
        [key_not_empty] key.length() > 0;   **/

        return localizedStringExists(entity.name(), propertyName, key, language);
    }


    /**
     * Returns <code>true</code> if a localized string for the given params exists.
     *
     * @param entity EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @return <code>true</code> if a localized string exists for these params
     * @see #localizedStringExists(String, String, String, String)
     */
    public static boolean localizedStringExists(EOEntity entity, String propertyName, String key)
    {
        /** require
        [valid_entity_param] entity != null;
        [valid_key_param] key != null;
        [key_not_empty] key.length() > 0;   **/

        return localizedStringExists(entity.name(), propertyName, key, Locale.getDefault().getDisplayLanguage());
    }



    /**
     * Primary localization method for EOFValidation. See #findLocalizedString(String, String, String, String) for more info.
     *
     * @param entityName EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @param language the language to use when looking up the string
     * @return localized string on entity for propertyName and key
     * @see #findLocalizedString(String, String, String, String)
     */
    public static String localizedString(String entityName, String propertyName, String key, String language)
    {
        /** require
        [valid_entityName_param] entityName != null;
        [valid_key_param] key != null;
        [valid_language_param] language != null;
        [localized_string_exists] localizedStringExists(entityName, propertyName, key, language);
        [key_not_empty] key.length() > 0;   **/

        return findLocalizedString(entityName, propertyName, key, language);

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Cover method for the primary localization method for EOFValidation.
     *
     * @param entityName EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @return localized string on entity for propertyName and key
     * @see #localizedString(String, String, String, String)
     */
    public static String localizedString(String entityName, String propertyName, String key)
    {
        /** require
        [valid_entityName_param] entityName != null;
        [valid_key_param] key != null;
        [localized_string_exists] localizedStringExists(entityName, propertyName, key, Locale.getDefault().getDisplayLanguage());
        [key_not_empty] key.length() > 0;   **/
        return localizedString(entityName, propertyName, key, Locale.getDefault().getDisplayLanguage());

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Cover method for the primary localization method for EOFValidation (<a href="#localizedString(String, String, String, String)">localizedString(String, String, String, String)</a>).
     *
     * @param entity EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @param language the language to use when looking up the string
     * @return localized string on entity for propertyName and key
     * @see #localizedString(String, String, String, String)
     */
    public static String localizedString(EOEntity entity, String propertyName, String key, String language)
    {
        /** require
        [valid_entity_param] entity != null;
        [valid_key_param] key != null;
        [valid_language_param] language != null;
        [localized_string_exists] localizedStringExists(entity, propertyName, key, language);
        [key_not_empty] key.length() > 0;   **/
        return localizedString(entity.name(), propertyName, key, language);

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Cover method for the primary localization method for EOFValidation (<a href="#localizedString(String, String, String, String)">localizedString(String, String, String, String)</a>).
     *
     * @param entity EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @return localized string on entity for propertyName and key
     * @see #localizedString(String, String, String, String)
     */
    public static String localizedString(EOEntity entity, String propertyName, String key)
    {
        /** require
        [valid_entity_param] entity != null;
        [valid_key_param] key != null;
        [localized_string_exists] localizedStringExists(entity, propertyName, key, Locale.getDefault().getDisplayLanguage());
        [key_not_empty] key.length() > 0;   **/

        return localizedString(entity.name(), propertyName, key, Locale.getDefault().getDisplayLanguage());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if there is a localized string in any of the string tables for any of the languages. Languages are searched depth first.
     *
     * @param entityName the name of the entity to lookup
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @param languages an array of languages to lookup in language order
     * @return <code>true</code> if there is a localized string in any of the string tables for any of the languages. Languages are searched depth first.
     */
    protected static String findLocalizedString(String entityName, String propertyName, String key, NSArray languages)
    {
        /** require
        [valid_entity_param] entityName != null;
        [valid_key_param] key != null;
        [valid_languages_param] languages != null;
        [one_or_more_languages]languages.count() > 0;
        [key_not_empty] key.length() > 0;   **/

        Enumeration languagesEnumerator = languages.objectEnumerator();
        while (languagesEnumerator.hasMoreElements())
        {
            String language = (String)languagesEnumerator.nextElement();
            if (localizedStringExists(entityName, propertyName, key, language))
            {
                return localizedString(entityName, propertyName, key, language);
            }
        }
        return null;
    }


    /**
     * Cover method for the primary localization existance method for EOFValidation (<a href="#localizedStringExists(String, String, String, String)">localizedStringExists(String, String, String, String)</a>).
     *
     * @param entityName name of the entity to lookup
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @param languages an array of languages to lookup in language order
     * @return <code>true</code> if there is a localized string in any of the string tables for any of the languages. Languages are searched depth first.
     * @see #findLocalizedString(String, String, String, NSArray)
     * @see #localizedStringExists(String, String, String, String)
     */
    public static boolean localizedStringExists(String entityName, String propertyName, String key, NSArray languages)
    {
        /** require
        [valid_entity_param] entityName != null;
        [valid_key_param] key != null;
        [valid_languages_param] languages != null;
        [one_or_more_languages]languages.count() > 0;
        [key_not_empty] key.length() > 0;   **/

        return findLocalizedString(entityName, propertyName, key, languages) != null;
    }


    /**
     * Cover method for the primary localization existance method for EOFValidation (<a href="#localizedStringExists(String, String, String, String)">localizedStringExists(String, String, String, String)</a>).
     *
     * @param entity EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @param languages an array of languages to lookup in language order
     * @return <code>true</code> if there is a localized string in any of the string tables for any of the languages. Languages are searched depth first.
     * @see #findLocalizedString(String, String, String, NSArray)
     * @see #localizedStringExists(String, String, String, String)
     */
    public static boolean localizedStringExists(EOEntity entity, String propertyName, String key, NSArray languages)
    {
        /** require
        [valid_entity_param] entity != null;
        [valid_key_param] key != null;
        [valid_languages_param] languages != null;
        [one_or_more_languages]languages.count() > 0;
        [key_not_empty] key.length() > 0;   **/

        return findLocalizedString(entity.name(), propertyName, key, languages) != null;
    }


    /**
     * Cover method for the primary localization method for EOFValidation (<a href="#localizedString(String, String, String, String)">localizedString(String, String, String, String)</a>).
     *
     * @param entityName name of the entity to lookup
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @param languages an array of languages to lookup in language order
     * @return localized string on entity for propertyName and key
     * @see #findLocalizedString(String, String, String, NSArray)
     * @see #localizedString(String, String, String, String)
     */
    public static String localizedString(String entityName, String propertyName, String key, NSArray languages)
    {
        /** require
        [valid_entity_param] entityName != null;
        [valid_key_param] key != null;
        [valid_languages_param] languages != null;
        [has_language] languages.count() > 0;
        [localization_exists] localizedStringExists(entityName, propertyName, key, languages);
        [key_not_empty] key.length() > 0;   **/

        return findLocalizedString(entityName, propertyName, key, languages);

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Cover method for the primary localization method for EOFValidation (<a href="#localizedString(String, String, String, String)">localizedString(String, String, String, String)</a>).
     *
     * @param entity EOEntity to look up this localized string for
     * @param propertyName property name to look up this localized string for.  Ignored if it is null or an empty string
     * @param key key to look up this localized string for
     * @param languages an array of languages to lookup in language order
     * @return localized string on entity for propertyName and key
     * @see #findLocalizedString(String, String, String, NSArray)
     * @see #localizedString(String, String, String, NSArray)
     * @see #localizedString(String, String, String, String)
     */
    public static String localizedString(EOEntity entity, String propertyName, String key, NSArray languages)
    {
        /** require
        [valid_entity_param] entity != null;
        [valid_key_param] key != null;
        [valid_languages_param] languages != null;
        [has_language] languages.count() > 0;
        [localization_exists] localizedStringExists(entity.name(), propertyName, key, languages);
        [key_not_empty] key.length() > 0;   **/

        return findLocalizedString(entity.name(), propertyName, key, languages);

        /** ensure [valid_result] Result != null; **/
    }



}
