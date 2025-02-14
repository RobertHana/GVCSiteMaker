// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.components;
import net.global_village.foundation.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;


/**
 * This page hosts the EditLive! for Java editor applet.  It is intended to be called from a Direct Action
 * and to function as a popup editor window for an existing text area form input. It takes these parameters:<br/>
 * <ol>
 *   <li>formName (required) - this is the name of the form containing the text area containing the text to be edited</li>
 *   <li>textAreaName (required) - this is the name of the text area containing the text to be edited</li>
 *   <li>saveButtonNme (optional) - the name of the Save button in the form named formName to be clicked 
 * when the Save button is clicked in this window.  If this is not present then formName is just submitted.</li>
 *   <li>saveParentFormOnClose (optional) - true if the parent form should be submitted when this popup is closed.  All 
 * other values will result in the parent form not being saved.</li>
 * </ol>
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class EditLivePage extends WOComponent {


    public static final String FORM_NAME = "formName";
    public static final String TEXT_AREA_NAME = "textAreaName";
    public static final String SAVE_BUTTON_NAME = "saveButtonName";
    public static final String SAVE_PARENT_FORM_ON_CLOSE = "saveParentFormOnClose";
    
    protected static final String INSTALL_DIRECTORY = SMApplication.appProperties().stringPropertyForKey("WYSIWYGInstallDirectory");
    
    
    public EditLivePage(WOContext context) {
        super(context);
    }
    
    
    /**
     * @return formName.textAreaName
     */
    public String textAreaIdentifier() 
    {
        return formName() + "." + textAreaName();
    }

    
    
    /**
     * @return formName.saveButtonName.click() or formName.submit()
     */
    public String saveParentFormAction() 
    {
        if (saveButtonName() != null)
        {
            return formName() + "." + saveButtonName() + ".click()";
        }
        else
        {
            return formName() + "." +"submit()";
        }
    }

    

    /**
     * @return HTTPS URL for JavaScript API file
     */
    public String editLiveJavaScript()
    {
        return SMActionURLFactory.protocolFromRequest(context().request()) + RequestUtils.hostNameFromRequest(context().request()) + 
        INSTALL_DIRECTORY + "/editlivejava.js";
    }

    

    /**
     * @return HTTP (no, not HTTPS) URL for XML configuration file
     */
    public String editLiveXMLConfig()
    {
        return SMActionURLFactory.insecureProtocol() + RequestUtils.hostNameFromRequest(context().request()) + 
        INSTALL_DIRECTORY + "/gvcsitemaker_editliveconfig.xml";
    }

    
    
    /**
     * @return URL for EditLive! installation directory.  There has been much confusion and changes over
     * whether this should be a https URL for https requests.  MacIE does not like full URLs.  See
     * Ephox support FAQ.
     */
    public String editLiveDownloadDirectory()
    {
        return INSTALL_DIRECTORY;
    }
    
    
    
    /**
     * Overridden for Direct Action parameter validation.
     */
    public WOResponse generateResponse() {

        if (formName() == null)
        {
            throw new RuntimeException("formName not passed to EditLivePage");
        }

        if (textAreaName() == null)
        {
            throw new RuntimeException("textAreaName not passed to EditLivePage");
        }
        return super.generateResponse();
    }
    
    
    
    /**
     * @return formName form value
     */
    public String formName() 
    {
        return (String) context().request().formValueForKey(FORM_NAME);
    }

    
    
    /**
     * @return textAreaName form value
     */
    public String textAreaName() 
    {
        return (String) context().request().formValueForKey(TEXT_AREA_NAME);
    }

    
    
    /**
     * @return saveButtonName form value
     */
    public String saveButtonName() 
    {
        return (String) context().request().formValueForKey(SAVE_BUTTON_NAME);
    }

    
    
    /**
     * @return <code>true</code> if saveParentFormOnClose form value is 'true'
     */
    public boolean saveParentFormOnClose()
    {
        String saveParentFormOnClose = (String) context().request().formValueForKey(SAVE_PARENT_FORM_ON_CLOSE);
        return ( ! StringAdditions.isEmpty(saveParentFormOnClose)) && saveParentFormOnClose.equals("true");
    }
    
}
