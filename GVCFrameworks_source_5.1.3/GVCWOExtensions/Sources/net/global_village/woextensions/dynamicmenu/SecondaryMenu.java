package net.global_village.woextensions.dynamicmenu;   

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * A subclass of DynamicMenu which represents menus which appear when MenuElements are "moused over".
   If <code>isSubMenu()</code> is true this menu is a SecondaryMenu of a MainLevelMenuElement if <code>isChildMenu()</code> is true this
   menu is a SecondaryMenu of a SubMenuElement.  It is intended that SecondaryMenu only be used as a submenu or a child menu.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */  
public class SecondaryMenu extends DynamicMenu
{
    // the results of setting menuAlignment to DynamicMenu.leftAlignment and DynamicMenu.rightAlignment seems the opposite behaviour to what I'd expect...
    protected int menuWidth;
    protected String menuContentAlignment;
    protected int verticalOffset;
    protected int horizontalOffset;
    protected boolean isSubMenu;  //defaults to true
    protected String menuAlignment;

    public static final int childMenuType = 1;
    public static final int subMenuType = 2;

    
    /**
     * Designated constructor.
     *
     * @param aMenuWidth the width of the secondary menu
     * @param aMenuContentAlignment the alignment of the content inside the menu
     * @param aVerticalOffset the vertical offset of the menu
     * @param aHorizontalOffset the horizontal offset of the menu
     * @param menuElements mutable array of MenuElements
     * @param menuType either true for Sub Menu or false for Child Menu
     * @param aMenuAlignment the aligment of the menu relative to it's parent element
     */
    public SecondaryMenu(int aMenuWidth,
                         String aMenuAlignment,
                         String aMenuContentAlignment,                     
                         int aVerticalOffset,
                         int aHorizontalOffset,
                         NSMutableArray menuElements,
                         int menuType)
    {
        super(menuElements);
        /** require
        [valid_aMenuContentAlignment_param] aMenuContentAlignment != null;
        [valid_menu_content_alignment] aMenuContentAlignment.equals(DynamicMenu.centerAlignment) || aMenuContentAlignment.equals(DynamicMenu.rightAlignment) || aMenuContentAlignment.equals(DynamicMenu.leftAlignment);
        [valid_menuElements_param] menuElements != null;
        [valid_aMenuAlignment_param] aMenuAlignment != null;
        [valid_menu_alignment] aMenuAlignment.equals(DynamicMenu.leftAlignment) || aMenuAlignment.equals(DynamicMenu.rightAlignment);
        [valid_menu_type] menuType == SecondaryMenu.childMenuType || menuType == SecondaryMenu.subMenuType; **/

        // Don't use the setters so that the invariant works...
        menuContentAlignment = aMenuContentAlignment;

        setMenuWidth(aMenuWidth);
        setVerticalOffset(aVerticalOffset);
        setHorizontalOffset(aHorizontalOffset);
        if (menuType == SecondaryMenu.subMenuType)
        {
            setIsSubMenu(true);
        }
        else
        {
            setIsSubMenu(false);
        }

        setMenuAlignment(aMenuAlignment);
    }
    

    
    /**
     * Alternate constructor, only takes in elements uses defaults for rest.  It defaults to creating a sub menu.
     *
     * @param menuElements mutable array of MenuElements
     */
    public SecondaryMenu(NSMutableArray menuElements)
    {
        this(DynamicMenu.defaultWidth,
             DynamicMenu.defaultMenuAlignment,
             DynamicMenu.defaultContentAlignment,
             DynamicMenu.defaultOffset,
             DynamicMenu.defaultOffset,
             menuElements,
             SecondaryMenu.subMenuType);
        /** require [valid_param] menuElements != null; **/
    }



    /**
     * Alternate constructor, uses empty array of elements and all defaults
     */
    public SecondaryMenu()
    {
        this(DynamicMenu.defaultWidth,
             DynamicMenu.defaultMenuAlignment,
             DynamicMenu.defaultContentAlignment,
             DynamicMenu.defaultOffset,
             DynamicMenu.defaultOffset,
             new NSMutableArray(),
             SecondaryMenu.subMenuType);
    }



    /**
     * Returns properly formatted String version of this object as calls to BrotherCake's Ultimate
     Dropdown Menu javascript.  If isSubMenu returns this:<br>
     defineSubmenuProperties(250,"right","center",7,-4);  <br>
     else it is a childMenu and looks like this: <br>
     defineChildmenuProperties(142,"left","left",-5,4);
     *
     * @param aContext used to generate correct URI's
     * @return String version of this object as calls to BrotherCake's javascript
     */
    public String javascript(WOContext aContext)
    {
        /** require [valid_param] aContext != null; **/

        String javascript;

        if (isSubMenu)
        {
            javascript = "defineSubmenuProperties(";      
        }
        else
        {
            javascript = "defineChildmenuProperties(";
        }

        javascript = javascript
            + menuWidth
            + ","
            + StringUtilities.quotedString(menuAlignment())
            + ","
            + StringUtilities.quotedString(menuContentAlignment())
            + ","
            + verticalOffset
            + ","
            + horizontalOffset
            + ");"
            + DynamicMenu.newLineString;

        javascript = javascript + javascriptForMenuElements(aContext);

        return javascript;

        /** ensure [valid_result] Result != null; **/
    }



    /**
      * Only allows valid alignments to be set.
      *
      * @param newAlignment
      */
    public synchronized void setMenuAlignment(String newAlignment)
    {
        /** require
        [valid_param] newAlignment != null;
        [valid_alignment] newAlignment.equals(DynamicMenu.leftAlignment) || newAlignment.equals(DynamicMenu.rightAlignment); **/

         menuAlignment = newAlignment;
     }



    /**
     * Only allows valid alignments to be set.
     *
     * @param newAlignment
     */
    public synchronized void setMenuContentAlignment(String newAlignment)
    {
        /** require
        [valid_param] newAlignment != null;
        [valid_alignment] newAlignment.equals(DynamicMenu.centerAlignment) || newAlignment.equals(DynamicMenu.rightAlignment) || newAlignment.equals(DynamicMenu.leftAlignment); **/

        menuContentAlignment = newAlignment;
    }



    /**
     * Returns true if ! isSubMenu
     */
    public synchronized boolean isChildMenu()
    {
        return ! isSubMenu();
    }



    // generic getters and setters
    public synchronized int menuWidth()
    {
        return menuWidth;
    }

    public synchronized void setMenuWidth(int newWidth)
    {
        menuWidth = newWidth;
    }

    public synchronized String menuContentAlignment()
    {
        return menuContentAlignment;
    }
    
    public synchronized int verticalOffset()
    {
        return verticalOffset;
    }

    public synchronized void setVerticalOffset(int newOffset)
    {
        verticalOffset = newOffset;
    }

    public synchronized int horizontalOffset()
    {
        return horizontalOffset;
    }

    public synchronized void setHorizontalOffset(int newOffset)
    {
        horizontalOffset = newOffset;
    }

    public synchronized boolean isSubMenu()
    {
        return isSubMenu;
    }

    public synchronized void setIsSubMenu(boolean newValue)
    {
        isSubMenu = newValue;
    }


    public synchronized String menuAlignment()
    {
        return menuAlignment;
    }



    /** invariant [has_menu_content_alignment] menuContentAlignment != null; **/



}
