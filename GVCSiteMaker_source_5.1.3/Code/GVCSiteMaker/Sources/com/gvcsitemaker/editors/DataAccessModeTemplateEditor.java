// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.editors;

import com.gvcsitemaker.core.pagecomponent.DataAccessMode;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSValidation;


/**
 * This class edits the custom template for a Single DataAccessMode.  It is used by DataAccessLayoutTemplatesEditor.
 */
public class DataAccessModeTemplateEditor extends WOComponent 
{
    public String title;                           // Title to display on left side
    protected DataAccessMode mode;                    // DataAccessMode being edited
    public String dummyTemplateFilePath;           // For WOFileUpload
    protected String templateValidationError;         // Error message from validating template


    /**
     * Designated constructor.
     */
    public DataAccessModeTemplateEditor(WOContext context)
    {
        super(context);
    }



    /**
     * Overridden to reset templateValidationError message.
     */
    public void appendToResponse(WOResponse aResponse,
                                 WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);
        templateValidationError = null;
    }


    
    /**
     * Deletes the Custom template.  Does not save changes.
     *
     * @return the HTML content of the Custom template.
     */
    public WOComponent deleteTemplate()
    {
        mode().deleteCustomTemplate();
        return context().page();
    }



    /**
     * Set the HTML content of the Custom template if something was uploaded and it was valid.  If something was uploaded, but not valid show an error message.
     *
     * @param newUploadedTemplate - the new HTML content of the Custom template.
     */
    public void setUploadedTemplate(NSData newUploadedTemplate)
    {
        if ((newUploadedTemplate != null) && (newUploadedTemplate.length() > 0))
        {
            try
            {
                mode().setCustomTemplateHtml(newUploadedTemplate);
            }
            catch (NSValidation.ValidationException e)
            {
                templateValidationError = "Template not uploaded! " + e.getMessage() + " For help see below.";
                ((DataAccessBaseEditor)parent()).notifySectionInvalid();
            }
        }
    }

    

    /**
     * Empty method to keep binding synchornization happy (WOFileUpload).
     */
    public NSData uploadedTemplate()
    {
        return NSData.EmptyData;
    }


    
    /**
     * Cover method for mode().useCustomTemplate() for radio button.
     */
    public boolean useDefaultTemplate() 
    {
        return ! mode().useCustomTemplate();
    }

    /**
     * Cover method for mode().setUseCustomTemplate() for radio button.
     */
    public void setUseDefaultTemplate(boolean newUseDefaultTemplate) 
    {
        if (newUseDefaultTemplate)
        {
            mode().setUseCustomTemplate(false);
        }
    }

    /**
     * Cover method for mode().useCustomTemplate() for radio button.
     */
    public boolean useCustomTemplate()  
    {
        return mode().useCustomTemplate();
    }

    /**
     * Cover method for mode().setUseCustomTemplate() for radio button.
     */
    public void setUseCustomTemplate(boolean newUseCustomTemplate) 
    {
        if (newUseCustomTemplate)
        {
            mode().setUseCustomTemplate(true);
        }
    }

    

    //*********** Generic Get / Set methods  ***********\\


    public DataAccessMode mode()
    {
        return mode;
    }
    public void setMode(DataAccessMode newMode)
    {
        mode = newMode;
    }


    public String templateValidationError()
    {
        return templateValidationError;
    }
    public void setTemplateValidationError(String string)
    {
        templateValidationError = string;
    }

}
