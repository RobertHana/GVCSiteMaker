package net.global_village.woextensions.dynamicmenu;   

import com.webobjects.appserver.*;


/**
 * A ChildMenuElement is an item in the DynamicMenu below (beside) a SubMenuElement.
   These elements are not visible when the menu is first drawn but only when a
   SubMenuElement is "moused over".
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */  
public class ChildMenuElement extends MenuElement
{


    /**
     * Designated Constructor, takes in all the attributes of the class
     *
     * @param aLinkHTML the HTML to use for the link, can be text or an image reference, allows for CSS and other HTML tags to be used
     * @param aDestination either the action to have the parent component perform or some URI relative or absolute
     * @param aFrameName the name of the frame to appear in
     * @param someAltText the alternate text, for image links and used in textual menus too
     * @param isDestinationDynamic flag to differentiate static from dynamic destinations
     */
    public ChildMenuElement(String aDestination,
                            String aLinkHTML,
                            String aFrameName,
                            String someAltText,
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
        [valid_someAltText_param] someAltText != null; **/
    }



    /**
     * Constructor that makes use of altText but also uses defaultFrameName
     *
     * @param aLinkHTML the HTML to use for the link, can be text or an image reference, allows for CSS and other HTML tags to be used
     * @param aDestination either the action to have the parent component perform or some URI relative or absolute
     * @param someAltText the alternate text, for image links and used in textual menus too
     * @param isDestinationDynamic flag to differentiate static from dynamic destinations
     */
    public ChildMenuElement(String aDestination,
                            String aLinkHTML,
                            String someAltText,
                            boolean isDestinationDynamic)
    {
        this(aDestination,
             aLinkHTML,
             MenuElement.defaultFrameName,
             someAltText,
             isDestinationDynamic);
        /** require
        [valid_aDestination_param] aDestination != null;
        [valid_aLinkHTML_param] aLinkHTML != null;
        [valid_someAltText_param] someAltText != null; **/
    }




    /**
     * Returns properly formatted String version of this object as calls to BrotherCake's Ultimate Dropdown Menu javascript.  Should look like this:<br>
     * <code>addChildmenuItem("/trance.shtml","Trance & Techno","_top","Killer tunes");</code>
     *
     * @param aContext used to generate correct URI's
     * @return String version of this object as calls to BrotherCake's javascript
     */
    public String javascript(WOContext aContext)
    {
        /** require [valid_param] aContext != null; **/

        String javascript;

        javascript = "addChildmenuItem(";

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
            + StringUtilities.quotedString(frameName())
            + ","
            + StringUtilities.quotedString(altText())
            + ");";

        return javascript;

        /** ensure [valid_result] Result != null; **/
    }



}
