package net.global_village.woextensions;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;

import er.extensions.components.*;

import net.global_village.eofvalidation.*;
import net.global_village.foundation.*;


/**
 * Contains additions: <br>
 * <ul><li>nicely cast accessor methods</li>
 * <li>enhanced binding access and checking</li>
 * <li>information related to the associations for the bindings, rather than the value of the bindings themselves.  <i>Most of these methods access private data and thus are subject to breaking in the future</i></li>
 * <li>access to the contents of the .api file associated with this component</li>
 * <li>assistance with formatting HTML for output</li>
 * <li>access to web application resources from a <code>WOComponent</code></li>
 * <li>method to prevent client caching</li>
 * <li>public NSMutableDictionary instance variable as alternative to individual instance variables</li>
 * <li>binding localization: @localize.key in a binding looks up key and returns the localized version</li>
 * <li>binding localization: @@localize.key in a binding looks up value for key on this component and returns the localized value</li>
 * </ul>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 14$
 */
public class WOComponent extends ERXComponent
{
    protected static final Integer trueInteger = new Integer(1);

    /**
     * Convenience for rapid prototyping or for bindings that are never referenced in code.
     * This allows you to use dynamic.variableName in bindings instead of declaring specific instance variables.
     */
    public NSMutableDictionary dynamic = new NSMutableDictionary();


    /**
     * Designated constuctor.
     */
    public WOComponent(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Resets <code>dynamic</code> in stateless sub-classes.
     */
    public void reset()
    {
        super.reset();
        dynamic = new NSMutableDictionary();
    }



    /**
     * Returns <code>true</code> if the named binding is present and evaluates to a non-null value
     *
     * @param bindingName the name of the binding to be evaluated
     * @return the boolean result of evaluating the passed binding
     */
    public boolean hasNonNullBindingFor(String bindingName)
    {
        /** require [valid_param] bindingName != null; **/

        return (hasBinding(bindingName) && (valueForBinding(bindingName) != null));
    }



    /**
     * Returns the boolean result of evaluating what <code>aBinding</code> is bound to.  If <code>aBinding</code> is not bound to anything, returns the default value.
     *
     * @param aBinding the name of the binding to be evaluated
     * @param defaultValue the defaultValue
     * @return the boolean result of evaluating the passed binding
     */
     public boolean booleanValueForBinding(String aBinding, boolean defaultValue)
     {
        /** require [valid_aBinding_param] aBinding != null; **/

         boolean booleanValueForBinding;

         if (hasBinding(aBinding))
         {
             Object valueForBinding = valueForBinding(aBinding);

             if (valueForBinding instanceof java.lang.Boolean)
             {
                 booleanValueForBinding = ((java.lang.Boolean)valueForBinding).booleanValue();
             }
             else if (valueForBinding instanceof java.lang.Integer)
             {
                 booleanValueForBinding = valueForBinding.equals(trueInteger);
             }
             else
             {
                 throw new RuntimeException("Can not convert value.  booleanValueForBinding called for binding '" +
                                            aBinding + "' which is bound to an object of class " +
                                            valueForBinding.getClass() + " with a value of " +
                                            valueForBinding);
             }
         }
         else
         {
             booleanValueForBinding = defaultValue;
         }

         return booleanValueForBinding;
     }



     /**
      * Returns the array of bindings which have values bound to them.  Each element in this array has a corresponding entry in associations.
      *
      * @return the array of keys
      * @deprecated Use bindingKeys()
      */
     public NSArray keys()
     {
        return bindingKeys();

        /** ensure
        [valid_result] Result != null;
        [same_as_bindingKeys] Result.equals(bindingKeys());
        [correct_count] Result.count() == associations().count(); **/
     }



     /**
      * Returns the array of the values bound to bindings.  Each element in this array has a corresponding entry in keys.
      *
      * @return the array of associations
      */
     public NSArray associations()
     {
         return _keyAssociations.allValues();

        /** ensure
        [valid_result] Result != null;
        [correct_count] Result.count() == keys().count(); **/
     }



    /**
     * Returns the <code>WOAssociation</code> for this binding, or nil if it is not bound.
     *
     * @param bindingName the binding name to be evaluated
     * @return the <code>WOAssociation</code> for this binding, or nil if it is not bound.
     */
    public WOAssociation associationForBinding(String bindingName)
    {
        /** require [valid_param] bindingName != null; **/

        WOAssociation associationForBinding = null;

        if (hasBinding(bindingName))
        {
            int bindingIndex = bindingKeys().indexOfObject(bindingName);
            associationForBinding = (WOAssociation) associations().objectAtIndex(bindingIndex);
        }

        return associationForBinding;
    }



    /**
     * Returns the string for this binding, as represented in the WOD file, rather than the value it is bound to.  For example if the WOD file has value=message; this method returns "message" rather than the value that message returns when used as a key in <code>valueForKey</code>.  <b>WARNING:</b> This method is implemented using object rape of <code>WOComponent</code> and may break in the future.
     *
     * @param bindingName the binding name to be evaluated
     * @return the string for this binding
     */
    public String keyPathForBinding(String bindingName)
    {
        /** require
        [valid_param] bindingName != null;
        [has_binding] hasBinding(bindingName);
        [binding_is_settable] associationForBinding(bindingName).isValueSettable(); **/

        WOAssociation association = associationForBinding(bindingName);
        return association.keyPath();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if the named binding is bound to an action WITHOUT invoking the action.  <code>hasBinding()</code> invokes the action so is often not suitable.
     *
     * @param actionBindingName	name of the action
     * @return boolean result of comparison
     */
    public boolean hasBindingForAction(String actionBindingName)
    {
        /** require [valid_param] actionBindingName != null; **/

        return keys().containsObject(actionBindingName);
    }



    /**
     * Returns the contents of the api file as a dictionary.  Returns an empty dictionary if the file can not be located.
     *
     * @return dictionary created from the api
     */
    public NSDictionary apiDictionary()
    {
        NSBundle ourBundle = NSBundle.bundleForClass(getClass());
        String apiFileName = name() + ".api";
        String apiAsString;

        if (ourBundle.isFramework())
        {
           apiAsString = ResourceManagerAdditions.stringFromResource(apiFileName, ourBundle.name());
        }
        else
        {
            // Special case for resources directly in application
            apiAsString = ResourceManagerAdditions.stringFromResource(apiFileName, null);
        }

        NSDictionary apiDictionary = (NSDictionary) NSPropertyListSerialization.propertyListFromString(apiAsString);

        return apiDictionary;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the array of of required bindings as determined by the Required=(); entry in the api file.
     *
     * @return array of required bindings
     */
    public NSArray requiredAPIBindings()
    {
       return (NSArray) apiDictionary().objectForKey("Required");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the array of of optional bindings as determined by the Optional entry in the api file.
     *
     * @return array of optional bindings
     */
    public NSArray optionalAPIBindings()
    {
        return (NSArray) apiDictionary().objectForKey("Optional");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Return all bindings, Optional and Required, from in the api file.
     *
     * @return array of all bindings in api dictionary
     */
    public NSArray allAPIBindings()
    {
        return requiredAPIBindings().arrayByAddingObjectsFromArray(optionalAPIBindings());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the list of this component's bindings which are not listed in the api file.  The user has added these in WOBuilder.
     *
     * @return user added bindings
     */
    public NSArray userDefinedBindings()
    {
        NSMutableArray allBindings = new NSMutableArray(keys());
        allBindings.removeObjectsInArray(allAPIBindings());

        return allBindings;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * This method supports creating HTML key-value pairs (width="150") from bindings.  Given a key and a selector, returns a string formatted by <code>htmlKeyValueFormat()</code> where the key is <code>aKey</code> and the value is the return value of <code>aSelector</code>.  <code>aSelector</code> should indicate a method that returns the value of the relevant binding.  If the return value of <code>aSelector</code> is nil or an empty string, this method returns an empty string.
     *
     * @param aKey the key to look up
     * @param withSelector the selector to use
     * @return the HTML key value pair
     * @throws RunTimeException if invoke() fails
     */
    public String htmlValue(String aKey, NSSelector withSelector)
    {
        /** require
        [valid_aKey_param] aKey != null;
        [key_is_nonzero_length] aKey.length() > 0;
        [valid_withSelector_param] withSelector != null;
        [selector_is_implemented] withSelector.implementedByObject(this); **/

        String htmlKeyValue = "";
        String value;

        try
        {
            value = (String) withSelector.invoke(this);
        }
        // I previously caught all these but switched to style used in other classes of catching generic excecption...
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e.getMessage());
        }
        catch (java.lang.reflect.InvocationTargetException e)
        {
            throw new RuntimeException(e.getMessage());
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e.getMessage());
        }

        if ( ! ((value == null) || (value.length() == 0)) )
        {
            htmlKeyValue = aKey + "=" + "\"" + value + "\"";
        }

        return htmlKeyValue;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * This method supports creating HTML &ltparam ...&gt elements for an &ltobject ...&gt element (i.e. &ltparam name=movie value="sunset.swf"&gt). Given a key and a selector, returns a string formatted where the key is <code>aKey</code> and the value is the return value of <code>aSelector</code>.  <code>aSelector</code> should indicate a method that returns the value of the relevant binding.  If the return value of <code>aSelector</code> is nil or an empty string, this method returns an empty string.
     *
     * @param aKey the key to look up
     * @param withSelector the selector to use
     * @return the Object Parameter
     * @throws RunTimeException if invoke() fails
     */
    public String objectParameter(String aKey, NSSelector withSelector)
    {
        /** require
        [valid_aKey_param] aKey != null;
        [key_is_nonzero_length] aKey.length() > 0;
        [valid_withSelector_param] withSelector != null;
        [selector_is_implemented] withSelector.implementedByObject(this); **/

        String objectParameter = "";
        String value;

        try
        {
            value = (String) withSelector.invoke(this);
        }
        // I previously caught all these but switched to style used in other classes of catching generic excecption...
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e.getMessage());
        }
        catch (java.lang.reflect.InvocationTargetException e)
        {
            throw new RuntimeException(e.getMessage());
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e.getMessage());
        }

        if ( ! ((value == null) || (value.length() == 0)))
        {
            objectParameter = "<param name=" + aKey + " value=\"" + value + "\">\n";
        }

        return objectParameter;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Return the URL for the named resource if it is to be dynamically served by the WebObjects application, or simply returns <code>aResouceName</code> if the web server will serve it.  If <code>aResouceName</code> contains either of the directory delimiters "\" or "/", it is considered to be served by the web server and returned un-altered.
     *
     * @param aResourceName the resource you want the URL for
     * @return the resulting URL
     */
    public String urlForResource(String aResourceName)
    {
        /** require [valid_param] aResourceName != null; **/

        // You can also get back an error string in this format
        ///ERROR/NOT_FOUND/app=JavaTestProject/filename=Main.html
        // where JavaTestProject is your project and Main.html is the resource you were trying to find which doesn't exist.
        // bad data containing / or \ will be echo'ed back to the screen.
        String urlForResource;

        char urlCharOne = '\\';
        char urlCharTwo = '/';

        if ((aResourceName.indexOf(urlCharOne) == -1) && (aResourceName.indexOf(urlCharTwo) == -1))
        {
            WOResourceManager resourceManager = application().resourceManager();

            urlForResource = resourceManager.urlForResourceNamed(aResourceName, null, session().languages(), context().request());
        }
        else
        {
            urlForResource = aResourceName;
        }

        return urlForResource;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Call this to prevent this page from being cached on the client.  This will (or should) force the browser to request a new page when the back / forward arrows or refresh buttons are used.  WO 4.5 provides <code>disableClientCaching()</code> which should be used in preference to this method when developing under WO 4.5 or greater.  Client caching is controlled by setting these headers: pragma,  expires, and cache-control.  See MS KB Article Q199805 for a discussion of client caching and IE 5.  <b>Does not work in IE currently!</b>
     */
    public void preventClientCaching()
    {
        /** require [context_has_response] context().response() != null; **/

        // Works in Netscape does not work in IE 5.0
        WOResponse response = context().response();

        response.setHeader("no-cache", "pragma");
        response.setHeader("no-cache", "cache-control");
        // The expires header has no effect on IE though both should work, but
        // Netscape definitely needs the expires HTTP header set.
        response.setHeader("Thu, 01 01 1998 00:00:01 GMT", "expires");
        //   response.setHeader("-1", "expires");
        // can use expires = -1 ?? Works in Netscape was suppose to work in IE
    }



    /**
     * Returns the top-level ("page") component.  For page components, returns <code>this</code>.
     *
     * @return the top-level component
     */
    public com.webobjects.appserver.WOComponent topLevelComponent()
    {
        return context().page();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * @param key the key of the string to localize
     * @return locallized string for key
     */
    public String localizedStringForKey(String key)
    {
        /** require [valid_key] key != null; **/
        return ((WOSession)session()).localizedStringInContext(key, context());
    }




    /**
     * Convenience method to access the editing context for this component.
     *
     * @return this implementation returns session().defaultEditingContext()
     */
    public EOEditingContext editingContext()
    {
        return session().defaultEditingContext();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Overridden to provide special handling for keypaths starting with "@localize" and "@@localize".
     */
    public Object valueForKeyPath(String keyPath)
    {

        if (keyPath.startsWith("@@localize"))
        {
            return localizedStringForKey((String)valueForKeyPath(keyPath.substring(11)));
        }

        if (keyPath.startsWith("@localize"))
        {
            return localizedStringForKey(keyPath.substring(10));
        }

        if (keyPath.startsWith("@propertyNamed"))
        {
            NSArray keyPathComponents = StringAdditions.keyPathComponents(keyPath);
            EOEntity entity = EOUtilities.entityNamed(editingContext(), (String)keyPathComponents.objectAtIndex(1));
            EOProperty property = EOEntityAdditions.propertyWithName(entity, (String)keyPathComponents.lastObject());
            reportError(property == null, "Binding does not lead to property: " + keyPath);
            return property;
        }

        return super.valueForKeyPath(keyPath);
    }



    /**
     * Error reporting function.  If <code>condition</code>, throws a RuntimeException with
     * message and the trail of components from this one up to the page.
     */
    public void reportError(boolean condition, String message)
    {
        /** require [valid_message] message != null;  **/

        if (condition)
        {
            StringBuffer componentTrail = new StringBuffer(name());
            com.webobjects.appserver.WOComponent parent = this;
            while (parent != context().page())
            {
                parent = parent.parent();
                componentTrail.append(" -> ");
                componentTrail.append(parent.name());
            }

            throw new RuntimeException(message + " at " + componentTrail.toString());
        }
    }



    /**
     * Allows a component to "inherit" the template (.html and .wod files) from another component.
     * <p>Usage:</p>
     * <pre>
     * public WOElement template() {
     *     return inheritTemplateFrom("AddAddress", "CoreUI");
     * }
     * </pre>
     * This very simple implementation does have some limitations:
     * <ol>
     * <li>It can't he used to inherit the template of another component inheriting a template.</li>
     * <li>It can't handle having two components with the same name in different packages</li>
     * <li>It does not use WO template caching</li>
     * </ol>
     *
     * @see com.webobjects.appserver.WOComponent#template()
     *
     * @param componentName the name of the component whose template will be inherited
     * @param frameworkName name of the framework the component is in, or null if in the application
     * @return the template form the indicated component
     */
    public WOElement inheritTemplateFrom(String componentName, String frameworkName)
    {
        /** require [valid_componentName] componentName != null;  **/
        String componentPath = componentName + ".wo/" + componentName + ".";
        String htmlString = ResourceManagerAdditions.stringFromResource(componentPath + "html", null, session().languages());
        String wodString = ResourceManagerAdditions.stringFromResource(componentPath + "wod", null, session().languages());
        return WOComponent.templateWithHTMLString(htmlString, wodString, session().languages());
        /** ensure [valid_Result] Result != null;  **/
    }


}
