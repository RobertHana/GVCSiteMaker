// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.core.components;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.appserver.*;


/**
 * Hyperlink to open popup window containing EditLive WYSIWYG editor for named text area.  The popup
 * window is passed three values: formName, textAreaName, and the optional saveButtonName.  See the
 * EditLivePage component for details on what these do.
 */
public class EditLiveLink extends WOComponent {
    
    
    public EditLiveLink(WOContext context) {
        super(context);
    }

    
    
    /**
     * @return true if the WYSIWYG editor link should be shown
     */
    public boolean isWYSIWYGEnabled() {
        return SMApplication.appProperties().booleanPropertyForKey("WYSIWYGEditorEnabled");
    }
    
    
    
    /**
     * Returns the URL used to open the window containing the EditLive applet.  This WO template adds
     * to this URL the formName, textAreaName, and the optional saveButtonName form parameters.
     * 
     * @return the URL used to open the window containing the EditLive applet
     */
    public String editLiveUrl() 
    {
        if (formName() == null)
        {
            throw new RuntimeException("formName is not bound in EditLiveLink");
        }

        if (textAreaName() == null)
        {
            throw new RuntimeException("textAreaName is not bound in EditLiveLink");
        }

        return SMActionURLFactory.editLiveUrl(context().request());
    }
    

    
    /**
     * @return value bound to formName binding.
     */
    public String formName() 
    {
        return (String) valueForBinding(EditLivePage.FORM_NAME);
    }

    
    
    /**
     * @return value bound to textAreaName binding.
     */
    public String textAreaName() 
    {
        return (String) valueForBinding(EditLivePage.TEXT_AREA_NAME);
    }

    
    
    /**
     * @return value bound to saveButtonName binding.
     */
    public String saveButtonName() 
    {
        return (String) valueForBinding(EditLivePage.SAVE_BUTTON_NAME);
    }

    
    /**
     * @return value bound to saveParentFormOnClose binding.
     */
    public Boolean saveParentFormOnClose() 
    {
        return (Boolean) valueForBinding(EditLivePage.SAVE_PARENT_FORM_ON_CLOSE);
    }
    
    
    public boolean isStateless() {
        return true;
    }
}
