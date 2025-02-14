package net.global_village.woextensions.dynamicmenu;   

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

/**
 * A mutable ordered list of MenuElements as well as orientation information and common methods.   A DynamicMenu is capeable of producings a representation of
 itself as javascript function calls.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 *
 */  
public class DynamicMenu extends Object implements NSKeyValueCoding
{
    protected NSMutableArray elements;

    public static final String centerAlignment = "center";
    public static final String leftAlignment = "left";
    public static final String rightAlignment = "right";
    public static final String defaultContentAlignment = centerAlignment;
    public static final String defaultMenuAlignment = leftAlignment;
    public static final int defaultWidth = 100;
    public static final int defaultOffset = 0;

    public static final String javaScriptOpenTag = "<script language=\"javascript1.2\" type=\"text/javascript\">";
    public static final String javaScriptCloseTag = "</script>";
    public static final String htmlCommentOpenTag = "<!--";
    public static final String htmlCommentCloseTag = "//-->";
    protected static final char newLineChar[] = { '\r' };  // worked better then \n in HoursTracker
    public static final String newLineString = new String(newLineChar);


    /**
     * Designated constructor.
     *
     * @param menuElements mutable array of MenuElements
     */
    public DynamicMenu(NSMutableArray menuElements)
    {
        super();
        /** require [valid_param] menuElements != null; **/

        // Don't use the setters so that the invariant works...
        elements = menuElements;
    }



    /**
     * Alternate constructor, uses empty array of elements and all defaults
     */
    public DynamicMenu()
    {
        this(new NSMutableArray());
    }



    /**
     * Adds anElement to the menu.
     *
     * @param anElement the element to add
     */
    public synchronized void addElement(MenuElement anElement)
    {
        /** require [valid_param] anElement != null; **/

        elements().addObject(anElement);

        /** ensure [element_added] hasElement(anElement); **/
    }



    /**
     * Removes anElement from the menu.
     *
     * @param anElement the element to remove
     */
    public synchronized void removeElement(MenuElement anElement)
    {
        /** require [valid_param] anElement != null; **/

        elements().removeObject(anElement);

        /** ensure [element_removed] ! hasElement(anElement); **/
    }



    /**
     * Returns true if anElement is in the menu.
     *
     * @param anElement the element to check for it's inclusion in the menu
     * @return true if anElement is in the menu
     */
    public synchronized boolean hasElement(MenuElement anElement)
    {
        /** require [valid_param] anElement != null; **/

        return elements().containsObject(anElement);
    }



    /**
     * Returns properly formatted String version of this object as calls to BrotherCake's Ultimate
       Dropdown Menu javascript
     *
     * @param aContext used to generate correct URI's
     * @return String version of this object as calls to BrotherCake's javascript
     */
    public String javascript(WOContext aContext)
    {
        /** require [valid_param] aContext != null; **/

        String javascript;

        javascript = DynamicMenu.javaScriptOpenTag
                     + DynamicMenu.newLineString
                     + DynamicMenu.htmlCommentOpenTag
                     + DynamicMenu.newLineString;

        javascript = javascript + javascriptForMenuElements(aContext);

        javascript = javascript
                     + DynamicMenu.htmlCommentCloseTag
                     + DynamicMenu.newLineString
                     + DynamicMenu.javaScriptCloseTag;
        
        return javascript;

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns javascript for all elements of the menu.
     *
     * @param aContext used to generate correct URI's
     * @return javascript for all elements of the menu
     */
    public String javascriptForMenuElements(WOContext aContext)
    {
        /** require [valid_param] aContext != null; **/

        String javascript = new String();

        java.util.Enumeration elementEnumerator = elements().objectEnumerator();

        while (elementEnumerator.hasMoreElements())
        {
            MenuElement anElement = (MenuElement) elementEnumerator.nextElement();

            javascript = javascript + anElement.javascript(aContext) + DynamicMenu.newLineString;
        }

        return javascript;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Conformance to NSKeyValueCoding. This method first checks itself for a matching key.
     * If one does not exist, it looks in the elements array for a menu element that has a
     * key that matches.
     *
     * @param key the key to look up
     * @return the value of the key
     */
    public Object valueForKey(String key)
    {
        try
        {
            return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
        }
        catch (NSKeyValueCoding.UnknownKeyException e)
        {
            NSDictionary elementDictionary = new NSDictionary(elements(), (NSArray)elements().valueForKey("key"));
            return elementDictionary.valueForKey(key);
        }
    }


    /**
     * Conformance to NSKeyValueCoding. This method first checks itself for a matching key.
     * If one does not exist, it looks in the elements array for a menu element that has a
     * key that matches.
     *
     * @param value the value to set
     * @param key the key to look up
     */
    public void takeValueForKey(Object value, String key)
    {
        NSKeyValueCoding.DefaultImplementation.takeValueForKey(this, value, key);
    }



    //generic getters and setters
    public synchronized NSMutableArray elements()
    {
        return elements;
    }

    public synchronized void setElements(NSMutableArray newArray)
    {
        elements = newArray;
    }



    /** invariant [has_elements] elements != null; **/

}
