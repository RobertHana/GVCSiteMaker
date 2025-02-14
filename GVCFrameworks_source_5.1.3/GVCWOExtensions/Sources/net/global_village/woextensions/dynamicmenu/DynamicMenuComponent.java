package net.global_village.woextensions.dynamicmenu;

import com.webobjects.appserver.*;



/**
 * This WOComponent must be included at the bottom of your page just above the &lt;/body&gt; tag. The DynamicMenu will not function without this WOComponent and several external javascripts located in the resources directory under the documentRoot.  This component must be passed in a DynamicMenu object out of which it builds the menu.  The primary function of this component is to load the needed .js files and generate the javascript needed.  This menu and all LAYER based menus when rendered on Windows over SELECT and INPUT elements will look odd.  Only Netscape 6.0 currently doesn't have this bug.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 *
 */
public class DynamicMenuComponent extends net.global_village.woextensions.WOComponent
{


    /**
     * Designated constructor.
     */
    public DynamicMenuComponent(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Overriden to add javascript to response
     */
    public void appendToResponse( WOResponse aResponse, WOContext aContext)
    {
        /** require [valid_aResponse_param] aResponse != null; [valid_aContext_param] aContext != null; **/

        aResponse.appendContentString(generatedJavascript(aContext));

        super.appendToResponse(aResponse, aContext); // super called after so as to tag on javascript first...
    }



    /**
     * Overriden to determine which action to preform.
     */
    public WOActionResults invokeAction(WORequest aRequest, WOContext aContext)
    {
        /** require [valid_aContext_param] aContext != null; **/

        // new tokenless version of method
        WOElement result = null;
        
        String senderID = aContext.senderID();
        String elementID = aContext.elementID();
        int lastSlashIndex = senderID.lastIndexOf("/");
        int lastPeriodIndex = senderID.lastIndexOf(".");
        String afterSlash;
        String senderIDWithoutActionName;
        
        if (lastSlashIndex >= 0)
        {
            afterSlash = senderID.substring(lastSlashIndex + 1);
            senderIDWithoutActionName = senderID.substring(0, lastSlashIndex);
        }
        else
        {
            afterSlash = senderID;
            senderIDWithoutActionName = senderID.substring(0, lastPeriodIndex);
        } 
        String actionKey = afterSlash.substring(lastPeriodIndex + 1);

        if (elementID.equals(senderIDWithoutActionName))
        {
            result = (WOComponent)performParentAction(actionKey);
        }

        return (WOActionResults) result;   
    }



    /**
     * This method produces a string with all the necessary Javascript to create a Dynamic Menu.  It is called once per page draw.  Caching has no effect as it is a completely new version of the component.
     *
     * @param aContext used to generate correct URI's
     * @return all the necessary Javascript to create a Dynamic Menu
     */
    public String generatedJavascript(WOContext aContext)
    {
        /** require [valid_param] aContext != null; **/

        String javascript;

        String requiredHTMLCommentOne = "<!-- ULTIMATE DROPDOWN MENU v3.2.2 by Brothercake -->";  
        String requiredHTMLCommentTwo = "<!-- http://www.brothercake.com/dropdown/ -->";
        // type added to script calls as it makes HTML more standards compliant
        String snifferJavascriptCall = "<script language=\"javascript\" src=\"/resources/sniffer.js\" type=\"text/javascript\"></script>";
        String lookAndFeelJavascriptCall = "<script language=\"javascript1.2\" src=\"/resources/lookAndFeel.js\" type=\"text/javascript\"></script>";
        String styleJavascriptCall = "<script language=\"javascript1.2\" src=\"/resources/style.js\" type=\"text/javascript\"></script>";
        String dynamicMenuJavascript = menu().javascript(aContext);
        String menuJavascriptCall = "<script language=\"javascript1.2\" src=\"/resources/menu.js\" type=\"text/javascript\"></script>";

        javascript = requiredHTMLCommentOne
            + DynamicMenu.newLineString
            + requiredHTMLCommentTwo
            + DynamicMenu.newLineString
            + snifferJavascriptCall
            + DynamicMenu.newLineString
            + lookAndFeelJavascriptCall
            + DynamicMenu.newLineString
            + styleJavascriptCall
            + DynamicMenu.newLineString
            + dynamicMenuJavascript
            + DynamicMenu.newLineString
            + menuJavascriptCall;

        return javascript;

        /** ensure [valid_result] Result != null; **/
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
     * Accessor for the DynamicMenu bound to menu.
     *
     * @return the DynamicMenu bound to menu
     */
    public DynamicMenu menu()
    {
        /** require [has_menu_binding] hasNonNullBindingFor("menu"); **/

        return (DynamicMenu)valueForBinding("menu");

        /** ensure [valid_result] Result != null; **/
    }



}
