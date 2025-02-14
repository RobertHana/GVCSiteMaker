package net.global_village.woextensions.dynamicmenu;  

import com.webobjects.appserver.*;
 

/**
 * Super class for menu elements.  A MenuElement is one item in the DynamicMenu.
 * A MenuElement is capeable of producing a representation of itself as javascript function calls.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public abstract class MenuElement extends Object
{
    /** Key for key path access of menus. */
    protected String key;

    protected String uri;
    protected String actionName;
    protected String linkHTML;
    protected String frameName;
    protected String altText;

    public static final String defaultFrameName = "_self";


    /**
     * Designated constructor.
     *
     * @param aLinkHTML the HTML to use for the link, can be text or an image reference, allows for CSS and 
              HTML tags to be used
     * @param aDestination either the action to have the parent component perform or some URI relative or absolute
     * @param aFrameName the name of the frame to appear in
     * @param someAltText the alternate text, for image links and used in textual menus too
     * @param isDestinationDynamic flag to differentiate static from dynamic destinations
     */
    public MenuElement(String aKey,
                       String aDestination,
                       String aLinkHTML,
                       String aFrameName,
                       String someAltText,
                       boolean isDestinationDynamic)
    {
        super();
        /** require
        [valid_aDestination_param] aDestination != null;
        [valid_aLinkHTML_param] aLinkHTML != null;
        [valid_aFrameName_param] aFrameName != null;
        [valid_someAltText_param] someAltText != null; **/

        // setters not used due to being checked in invariant due to bug in preprocessor
        key = aKey;

        if (isDestinationDynamic)
        {
            actionName = aDestination;
        }
        else
        {
            uri = aDestination;
        }
        linkHTML = aLinkHTML;
        frameName = aFrameName;
        altText = someAltText;
    }



    /**
     * Construct with all attributes but the <code>key</code> attribute, which is set to aDestination
     * if the destination is dynamic, <code>null</code> otherwise.
     *
     * @param aLinkHTML the HTML to use for the link, can be text or an image reference, allows for CSS and 
              HTML tags to be used
     * @param aDestination either the action to have the parent component perform or some URI relative or absolute
     * @param aFrameName the name of the frame to appear in
     * @param someAltText the alternate text, for image links and used in textual menus too
     * @param isDestinationDynamic flag to differentiate static from dynamic destinations
     */
    public MenuElement(String aDestination,
                       String aLinkHTML,
                       String aFrameName,
                       String someAltText,
                       boolean isDestinationDynamic)
    {
        this(isDestinationDynamic ? aDestination : null,
             aDestination,
             aLinkHTML,
             aFrameName,
             someAltText,
             isDestinationDynamic);
        /** require
        [valid_aDestination_param] aDestination != null;
        [valid_aLinkHTML_param] aLinkHTML != null;
        [valid_aFrameName_param] aFrameName != null;
        [valid_someAltText_param] someAltText != null; **/
    }



    /**
     * Constructor that makes use of altText but uses the defaultFrameName (see
     * constants, above) and the default key (see previous constructor).
     *
     * @param aLinkHTML the HTML to use for the link, can be text or an image reference, allows for CSS and 
              HTML tags to be used
     * @param aDestination either the action to have the parent component perform or some URI relative or absolute
     * @param someAltText the alternate text, for image links and used in textual menus too
     * @param isDestinationDynamic flag to differentiate static from dynamic destinations
     */
    public MenuElement(String aDestination,
                       String aLinkHTML,
                       String someAltText,
                       boolean isDestinationDynamic)
    {
        this(isDestinationDynamic ? aDestination : null,
             aDestination,
             aLinkHTML,
             defaultFrameName,
             someAltText,
             isDestinationDynamic);
        /** require
        [valid_aDestination_param] aDestination != null;
        [valid_aLinkHTML_param] aLinkHTML != null;
        [valid_someAltText_param] someAltText != null; **/
    }


    
    /**
     * Constructor that just takes in the destination and link HTML, uses aDestination for altText,
     * defaultFrameName for frameName and the default key (see previous constructor)
     *
     * @param aLinkHTML the HTML to use for the link, can be text or an image reference, allows for CSS and other
              HTML tags to be used
     * @param aDestination either the action to have the parent component perform or some URI relative or absolute
     * @param isDestinationDynamic flag to differentiate static from dynamic destinations
     */
    public MenuElement(String aDestination,
                       String aLinkHTML,
                       boolean isDestinationDynamic)
    {
        this(isDestinationDynamic ? aDestination : null,
             aDestination,
             aLinkHTML,
             defaultFrameName,
             aDestination,
             isDestinationDynamic);
        /** require
        [valid_aDestination_param] aDestination != null;
        [valid_aLinkHTML_param] aLinkHTML != null; **/
    }


    
    /**
     * Returns true if destination, actionName linkHTML, frameName, and altText are equal
     *
     * @param anotherElement other element to test for equality with
     * @return true if all four attributes are equal
     */
    public synchronized boolean equals(MenuElement anotherElement)
    {
        /** require [valid_param] anotherElement != null; **/

        boolean result = false;

        if ((uri().equals(anotherElement.uri()))
             && (actionName().equals(anotherElement.actionName()))
             && (linkHTML().equals(anotherElement.linkHTML()))
             && (frameName().equals(anotherElement.frameName()))
             && (altText().equals(anotherElement.altText())))
        {
            result = true;
        }
        
        return result;
    }


    
    /**
     * Returns properly formatted String version of this object as calls to BrotherCake's Ultimate
       Dropdown Menu javascript
     *
     * @param aContext used to generate correct URI's
     * @return String version of this object as calls to BrotherCake's javascript
     *
     * Jass can't handle contracts on abstract methods. Add this to your subclass: require aContext != null ensure Result != null
     */
    abstract public String javascript(WOContext aContext);


    
   /**
    * Returns actionName() as valid uri in aContext
    *
    * @param aContext the context for the URI
    * @return properly formatted URI
    */
    public String actionNameAsURI(WOContext aContext)
    {
        /** require [valid_param] aContext != null; **/

        String actionNameAsURI;

        String actionURI = aContext.componentActionURL();
        // append the actual actionName to the end of URI
        actionNameAsURI = actionURI + "." + actionName();

        return actionNameAsURI;

        /** ensure [valid_result] Result != null; **/
    }


    
    /**
     * Returns true when actionName is not null
     *
     * @return true when actionName is not null
     */
    public boolean isDestinationDynamic()
    {
        return actionName() != null;
    }



    // getters and setters below
    public synchronized String key()
    {
        return key;
    }

    public synchronized void setKey(String newKey)
    {
        key = newKey;
    }

    public synchronized String uri()
    {
        return uri;
    }

    public synchronized void setURI(String newURI)
    {
        uri = newURI;
    }

    public synchronized String actionName()
    {
        return actionName;
    }

    public synchronized void setActionName(String newActionName)
    {
        actionName = newActionName;
    }

    public synchronized String linkHTML()
    {
        return linkHTML;
    }

    public synchronized void setLinkHTML(String newLinkHTML)
    {
        linkHTML = newLinkHTML;
    }

    public synchronized String frameName()
    {
        return frameName;
    }

    public synchronized void setFrameName(String newFrameName)
    {
        frameName = newFrameName;
    }

    public synchronized String altText()
    {
        return altText;
    }

    public synchronized void setAltText(String newAltText)
    {
        altText = newAltText;
    }



    /** invariant
    [has_uri_or_action] (uri != null) || (actionName != null);
    [has_link_html] linkHTML != null;
    [has_frame_name] frameName != null;
    [has_alt_text] altText != null; **/



}
