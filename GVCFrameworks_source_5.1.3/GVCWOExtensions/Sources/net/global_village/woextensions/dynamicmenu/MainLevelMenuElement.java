package net.global_village.woextensions.dynamicmenu;  

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * A MainLevelMenuElement is an item in a DynamicMenu at the topmost level.
   Elements at this level have more capabilities and options then elements at
   the lower levels.  These elements are also initially visible when the page is drawn.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */  
public class MainLevelMenuElement extends MenuElement implements NSKeyValueCoding
{
    protected int width;
    protected String contentAlignment;
    protected int topPosition;
    protected int leftPosition;
    protected String keyboardShortCut;
    protected SecondaryMenu subMenu;

    public static final String defaultKeyboardShortCut = ""; // empty string which is no shortcut at all
    public static final int defaultWidth = 100;


    /**
     * Designated Constructor, takes in all the must have attributes of the class
     *
     * @param aKey the key by which this menu can be accessed
     * @param aLinkHTML the HTML to use for the link, can be text or an image reference, allows for CSS and 
              HTML tags to be used
     * @param aDestination either the action to have the parent component perform or some URI relative or absolute
     * @param aFrameName the name of the frame to appear in
     * @param someAltText the alternate text, for image links and used in textual menus too
     * @param aContentAlignment either leftAlignment, rightAlignment, or centerAlignment
     * @param aKeyboardShortCut the IE specific navigational aid, a single character
     * @param aSecondaryMenu a menu to display when this element is "moused over"
     * @param isDestinationDynamic flag to differentiate static from dynamic destinations
     */
    public MainLevelMenuElement(String aKey,
                                String aDestination,
                                String aLinkHTML,
                                String aFrameName,
                                String someAltText,
                                String aContentAlignment,
                                String aKeyboardShortCut,
                                SecondaryMenu aSecondaryMenu,
                                boolean isDestinationDynamic)
    {
        super(aKey,
              aDestination,
              aLinkHTML,
              aFrameName,
              someAltText,
              isDestinationDynamic);
        /** require
        [valid_aDestination_param] aDestination != null;
        [valid_aLinkHTML_param] aLinkHTML != null;
        [valid_aFrameName_param] aFrameName != null;
        [valid_someAltText_param] someAltText != null;
        [valid_aContentAlignment_param] aContentAlignment != null;
        [content_alignment_is_valid] (aContentAlignment.equals(DynamicMenu.centerAlignment)) || (aContentAlignment.equals(DynamicMenu.rightAlignment)) || (aContentAlignment.equals(DynamicMenu.leftAlignment));
        [valid_aKeyboardShortCut_param] aKeyboardShortCut != null;
        [shortcut_is_valid] aKeyboardShortCut.length() == 1 || aKeyboardShortCut.equals(MainLevelMenuElement.defaultKeyboardShortCut); /# this last precondition caused problem for instance when unit testing.  Odd exceptions occur when it is uncommented. Added OR clause. #/ **/

        // Don't use the setters so that the invariant works...
        contentAlignment = aContentAlignment;
        keyboardShortCut = aKeyboardShortCut;

        setSubMenu(aSecondaryMenu);

        setLeftPosition(DynamicMenu.defaultOffset);
        setTopPosition(DynamicMenu.defaultOffset);
        setKeyboardShortCut(aKeyboardShortCut);
        setWidth(MainLevelMenuElement.defaultWidth);
    }


    /**
     * Provided for compatibility with previous releases: takes everything the designated constructor
     * does except the aKey param.
     *
     * @param aLinkHTML the HTML to use for the link, can be text or an image reference, allows for CSS and 
              HTML tags to be used
     * @param aDestination either the action to have the parent component perform or some URI relative or absolute
     * @param aFrameName the name of the frame to appear in
     * @param someAltText the alternate text, for image links and used in textual menus too
     * @param aContentAlignment either leftAlignment, rightAlignment, or centerAlignment
     * @param aKeyboardShortCut the IE specific navigational aid, a single character
     * @param aSecondaryMenu a menu to display when this element is "moused over"
     * @param isDestinationDynamic flag to differentiate static from dynamic destinations
     */
    public MainLevelMenuElement(String aDestination,
                                String aLinkHTML,
                                String aFrameName,
                                String someAltText,
                                String aContentAlignment,
                                String aKeyboardShortCut,
                                SecondaryMenu aSecondaryMenu,
                                boolean isDestinationDynamic)
    {
        super(aDestination,
              aLinkHTML,
              aFrameName,
              someAltText,
              isDestinationDynamic);
        /** require
        [valid_aDestination_param] aDestination != null;
        [valid_aLinkHTML_param] aLinkHTML != null;
        [valid_aFrameName_param] aFrameName != null;
        [valid_someAltText_param] someAltText != null;
        [valid_aContentAlignment_param] aContentAlignment != null;
        [content_alignment_is_valid] (aContentAlignment.equals(DynamicMenu.centerAlignment)) || (aContentAlignment.equals(DynamicMenu.rightAlignment)) || (aContentAlignment.equals(DynamicMenu.leftAlignment));
        [valid_aKeyboardShortCut_param] aKeyboardShortCut != null;
        [shortcut_is_valid] aKeyboardShortCut.length() == 1 || aKeyboardShortCut.equals(MainLevelMenuElement.defaultKeyboardShortCut); /# this last precondition caused problem for instance when unit testing.  Odd exceptions occur when it is uncommented. Added OR clause. #/ **/

        // Don't use the setters so that the invariant works...
        contentAlignment = aContentAlignment;
        keyboardShortCut = aKeyboardShortCut;

        setSubMenu(aSecondaryMenu);

        setLeftPosition(DynamicMenu.defaultOffset);
        setTopPosition(DynamicMenu.defaultOffset);
        setKeyboardShortCut(aKeyboardShortCut);
        setWidth(MainLevelMenuElement.defaultWidth);
    }



    /**
     * Alternate constructor just provides destination, linkHTML, altText, and subMenuElements uses defaults for the rest
     *
     * @param aLinkHTML the HTML to use for the link, can be text or an image reference, allows for CSS and other
              HTML tags to be used
     * @param aDestination either the action to have the parent component perform or some URI relative or absolute
     * @param aFrameName the frame to have destination appear in
     * @param someAltText the alternate text, for image links and used in textual menus too
     * @param aSecondaryMenu a menu to display when this element is "moused over"
     * @param isDestinationDynamic flag to differentiate static from dynamic destinations
     */
    public MainLevelMenuElement(String aDestination,
                                String aLinkHTML,
                                String aFrameName,
                                String someAltText,
                                SecondaryMenu aSecondaryMenu,
                                boolean isDestinationDynamic)
    {
        this(isDestinationDynamic ? aDestination : null,
             aDestination,
             aLinkHTML,
             aFrameName,
             someAltText,
             DynamicMenu.defaultContentAlignment,
             MainLevelMenuElement.defaultKeyboardShortCut,
             aSecondaryMenu,
             isDestinationDynamic);
        /** require
        [valid_aDestination_param] aDestination != null;
        [valid_aLinkHTML_param] aLinkHTML != null;
        [valid_aFrameName_param] aFrameName != null;
        [valid_someAltText_param] someAltText != null; **/
    }



    /**
     * Alternate Constructor uses defaults for content alignment, keyboard shortcut and assumes
     * there is no secondary menu.
     *
     * @param aKey the key by which the menu can be accessed
     * @param aLinkHTML the HTML to use for the link, can be text or an image reference, allows for CSS and other
              HTML tags to be used
     * @param aDestination either the action to have the parent component perform or some URI relative or absolute
     * @param aFrameName the name of the frame to appear in
     * @param someAltText the alternate text, for image links and used in textual menus too
     * @param isDestinationDynamic flag to differentiate static from dynamic destinations
     */
    public MainLevelMenuElement(String aKey,
                                String aDestination,
                                String aLinkHTML,
                                String aFrameName,
                                String someAltText,
                                boolean isDestinationDynamic)
    {
        this(aKey,
             aDestination,
             aLinkHTML,
             aFrameName,
             someAltText,
             DynamicMenu.defaultContentAlignment,
             MainLevelMenuElement.defaultKeyboardShortCut,
             null,
             isDestinationDynamic);
        /** require
        [valid_aDestination_param] aDestination != null;
        [valid_aLinkHTML_param] aLinkHTML != null;
        [valid_aFrameName_param] aFrameName != null;
        [valid_someAltText_param] someAltText != null; **/
    }

    

    /**
     * Alternate Constructor uses defaults for content alignment, keyboard shortcut and assumes there is no secondary menu.
     *
     * @param aLinkHTML the HTML to use for the link, can be text or an image reference, allows for CSS and other
              HTML tags to be used
     * @param aDestination either the action to have the parent component perform or some URI relative or absolute
     * @param aFrameName the name of the frame to appear in
     * @param someAltText the alternate text, for image links and used in textual menus too
     * @param isDestinationDynamic flag to differentiate static from dynamic destinations
     */
    public MainLevelMenuElement(String aDestination,
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
             DynamicMenu.defaultContentAlignment,
             MainLevelMenuElement.defaultKeyboardShortCut,
             null,
             isDestinationDynamic);
        /** require
        [valid_aDestination_param] aDestination != null;
        [valid_aLinkHTML_param] aLinkHTML != null;
        [valid_aFrameName_param] aFrameName != null;
        [valid_someAltText_param] someAltText != null; **/
    }

    

    /**
     * Alternate constructor just provides destination, linkHTML, and altText, uses empty array for SubMenuElements
       and the defaults for the rest
     *
     * @param aLinkHTML the HTML to use for the link, can be text or an image reference, allows for CSS and other
              HTML tags to be used
     * @param aDestination either the action to have the parent component perform or some URI relative or absolute
     * @param someAltText the alternate text, for image links and used in textual menus too
     * @param isDestinationDynamic flag to differentiate static from dynamic destinations
     */
    public MainLevelMenuElement(String aDestination,
                                String aLinkHTML,
                                String someAltText,
                                boolean isDestinationDynamic)
    {
        this(isDestinationDynamic ? aDestination : null,
             aDestination,
             aLinkHTML,
             MenuElement.defaultFrameName,
             someAltText,
             DynamicMenu.defaultContentAlignment,
             MainLevelMenuElement.defaultKeyboardShortCut,
             null,
             isDestinationDynamic);
        /** require
        [valid_aDestination_param] aDestination != null;
        [valid_aLinkHTML_param] aLinkHTML != null;
        [valid_someAltText_param] someAltText != null; **/
    }


    
    /**
     * Returns true if subMenu exists
     */
    public synchronized boolean hasSubMenu()
    {
        return subMenu != null;
    }



    /**
     * Returns properly formatted String version of this object as calls to BrotherCake's Ultimate
       Dropdown Menu javascript<br>
       example: addMainItem("http://www.domain.com/","Home",100,"left","_top","Home page",0,0,"h");
     *
     * @param aContext used to generate correct URI's
     * @return String version of this object as calls to BrotherCake's javascript
     */
    public String javascript(WOContext aContext)
    {
        /** require [valid_param] aContext != null; **/

        String javascript;

        javascript = "addMainItem(";

        if (isDestinationDynamic())
        {
            javascript = javascript + StringUtilities.quotedString(actionNameAsURI(aContext));
        }
        else
        {
            javascript = javascript + StringUtilities.quotedString(uri());
        }
        
        javascript = javascript 
                      + ","
                      + StringUtilities.quotedString(linkHTML())
                      + ","
                      + width()
                      + ","
                      + StringUtilities.quotedString(contentAlignment())
                      + ","
                      + StringUtilities.quotedString(frameName())
                      + ","
                      + StringUtilities.quotedString(altText())
                      + ","
                      + topPosition()
                      + ","
                      + leftPosition()
                      + ","
                      + StringUtilities.quotedString(keyboardShortCut())
                      + ");";

        if (hasSubMenu())
        {
            javascript = javascript + DynamicMenu.newLineString + subMenu().javascript(aContext) + DynamicMenu.newLineString;
        }
        
        return javascript;

        /** ensure [valid_result] Result != null; **/
    }

    

    /**
     * Only allows valid alignments to be set.
     *
     * @param newAlignment the alignment
     */
    public synchronized void setContentAlignment(String newAlignment)
    {
        /** require
        [valid_newAlignment_param] newAlignment != null;
        [alignment_is_valid] (newAlignment.equals(DynamicMenu.centerAlignment)) || (newAlignment.equals(DynamicMenu.rightAlignment) ) || (newAlignment.equals(DynamicMenu.leftAlignment)); **/

        contentAlignment = newAlignment;
    }



    /**
     * Conformance to NSKeyValueCoding. This method first checks itself for a matching key.
     * If one does not exist, it looks in the submenu for a menu element that has a
     * key that matches.
     *
     * @param aKey the key to look up
     * @return the value of the key
     */
    public Object valueForKey(String aKey)
    {
        try
        {
            return NSKeyValueCoding.DefaultImplementation.valueForKey(this, aKey);
        }
        catch (NSKeyValueCoding.UnknownKeyException e)
        {
            return subMenu().valueForKey(aKey);
        }
    }


    /**
     * Conformance to NSKeyValueCoding. This method first checks itself for a matching key.
     * If one does not exist, it looks in the elements array for a menu element that has a
     * key that matches.
     *
     * @param value the value to set
     * @param aKey the key to look up
     */
    public void takeValueForKey(Object value, String aKey)
    {
        NSKeyValueCoding.DefaultImplementation.takeValueForKey(this, value, aKey);
    }



    // generic setters and getters below
    public synchronized int width()
    {
        return width;
    }

    public synchronized void setWidth(int newWidth)
    {
        width = newWidth;
    } 

    public synchronized String contentAlignment()
    {
        return contentAlignment;
    }

    public synchronized int topPosition()
    {
        return topPosition;
    }

    public synchronized void setTopPosition(int newOffset)
    {
        topPosition = newOffset;
    }

    public synchronized int leftPosition()
    {
        return leftPosition;
    }

    public synchronized void setLeftPosition(int newOffset)
    {
        leftPosition = newOffset;
    }

    public synchronized String keyboardShortCut()
    {
        return keyboardShortCut;
    }

    public synchronized void setKeyboardShortCut(String newShortCut)
    {
        keyboardShortCut = newShortCut;
    }

    public synchronized SecondaryMenu subMenu()
    {
        return subMenu;
    }

    public synchronized void setSubMenu(SecondaryMenu newSecondaryMenu)
    {
        subMenu = newSecondaryMenu;
    }



    /** invariant
    [has_content_alignment] contentAlignment != null;
    [has_keyboard_shortcut] keyboardShortCut != null; **/



}
