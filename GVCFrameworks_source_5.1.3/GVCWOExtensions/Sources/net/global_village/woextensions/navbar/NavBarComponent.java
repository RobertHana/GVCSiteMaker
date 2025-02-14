package net.global_village.woextensions.navbar;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import net.global_village.woextensions.dynamicmenu.*;



/**
 * <p>A nav bar component.  Currently only supports 2 levels.  To use, add the component to
 * your page and set the two attributes: set <code>menu</code> to an instance of a dynamic menu
 * whose keys are the names of pages to go to when that menu item is clicked; set
 * <code>selectedMenu</code> to the key path of the selected menu item.</p>
 *
 * @author Copyright (c) 2006  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 1$
 */
public class NavBarComponent extends net.global_village.woextensions.WOComponent
{
    private static final long serialVersionUID = -1992819457504164741L;

    public MenuElement aMenuElement;


    /**
     * Designated constructor.
     */
    public NavBarComponent(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * This method is overridden so variables are not synchronized with bindings.
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }


    /**
     * Overriden to make this a stateless component.
     *
     * @return true
     */
    public boolean isStateless()
    {
        return true;
    }



    /**
     * Returns <code>true</code> iff the current menu element is selected.
     *
     * @return <code>true</code> iff the current menu element is selected
     */
    public boolean isSelected()
    {
        return selectedMenuArray().contains(aMenuElement);
    }



    /**
     * Returns the menu to display in this component.
     *
     * @return the menu to display
     */
    public DynamicMenu menu()
    {
        return (DynamicMenu)valueForBinding("menu");
    }



    /**
     * Returns <code>true</code> iff there is a submenu for the selected menu.
     *
     * @return <code>true</code> iff there is a submenu for the selected menu
     */
    public boolean hasSubMenu()
    {
        NSArray keys = NSArray.componentsSeparatedByString(selectedMenuString(), ".");
        return keys.count() > 1;
    }



    /**
     * Returns the submenu to display in this component.
     *
     * @return the submenu to display
     */
    public SecondaryMenu subMenu()
    {
        NSArray keys = NSArray.componentsSeparatedByString(selectedMenuString(), ".");

        String aKey = (String)keys.objectAtIndex(0);
        MainLevelMenuElement mainLevelMenuElement = (MainLevelMenuElement)menu().valueForKey(aKey);

        aKey = (String)keys.objectAtIndex(1);
        return mainLevelMenuElement.subMenu();
    }



    /**
     * Returns the CSS id for the nav bar.
     *
     * @return the CSS id for the nav bar
     */
    public String cssID()
    {
        return (String)valueForBinding("cssID") == null ? "navbar" : (String)valueForBinding("cssID");
    }



    /**
     * Returns the CSS class for selected menu elements.
     *
     * @return the CSS class for selected menu elements
     */
    public String selectedMenuCSSClass()
    {
        return (String)valueForBinding("selectedMenuCSSClass") == null ? "selectedMenuCSSClass" : (String)valueForBinding("selectedMenuCSSClass");
    }


    /**
     * Returns the CSS class for submenu elements.
     *
     * @return the CSS class for submenu elements
     */
    public String subMenuCSSClass()
    {
        return (String)valueForBinding("subMenuCSSClass") == null ? "subMenuCSSClass" : (String)valueForBinding("subMenuCSSClass");
    }


    /**
     * Returns the CSS class for selected submenu elements.
     *
     * @return the CSS class for selected submenu elements
     */
    public String selectedSubMenuCSSClass()
    {
        return (String)valueForBinding("selectedSubMenuCSSClass") == null ? "selectedSubMenuCSSClass" : (String)valueForBinding("selectedSubMenuCSSClass");
    }



    /**
     * Returns the selected menu key path.
     *
     * @return the selected menu key path
     */
    public String selectedMenuString()
    {
        return (String)valueForBinding("selectedMenu");
    }



    /**
     * Returns the selected menu.
     *
     * @return the selected menu
     */
    public MenuElement selectedMenu()
    {
        return (MenuElement)selectedMenuArray().objectAtIndex(0);
    }



    /**
     * Returns the selected submenu.
     *
     * @return the selected submenu
     */
    public SubMenuElement selectedSubMenu()
    {
        return (SubMenuElement)selectedMenuArray().objectAtIndex(1);
    }



    /**
     * Returns the selected submenu and all its parent menus.
     *
     * @return the selected submenu
     */
    public NSArray selectedMenuArray()
    {
        NSMutableArray selectedMenuArray = new NSMutableArray();
        NSArray keys = NSArray.componentsSeparatedByString(selectedMenuString(), ".");
        /** check [selected_menu_keypath_not_null] keys.count() > 0; **/

        String aKey = (String)keys.objectAtIndex(0);
        MainLevelMenuElement mainLevelMenuElement = (MainLevelMenuElement)menu().valueForKey(aKey);
        selectedMenuArray.addObject(mainLevelMenuElement);

        if (keys.count() > 1)
        {
            aKey = (String)keys.objectAtIndex(1);
            SubMenuElement subMenuElement = (SubMenuElement)mainLevelMenuElement.subMenu().valueForKey(aKey);
            selectedMenuArray.addObject(subMenuElement);
        }

        return selectedMenuArray;
    }



}
