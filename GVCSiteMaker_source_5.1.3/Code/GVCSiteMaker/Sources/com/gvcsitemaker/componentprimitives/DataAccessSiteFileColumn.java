// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;

import com.gvcsitemaker.appserver.Application;
import com.gvcsitemaker.core.SiteFile;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.support.DataAccessParameters;
import com.gvcsitemaker.core.support.SMDataAccessActionURLFactory;
import com.gvcsitemaker.core.support.WOFileUploadBindings;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.foundation.NSNumberFormatter;

import er.extensions.foundation.ERXStringUtilities;

import net.global_village.foundation.Date;



/**
 * Implements the UI for com.gvcsitemaker.core.pagecomponent.DataAccessSiteFileColumn showing an image or URL, and a upload input and checkbox as configured.  It also implements the edit function for the "show as image" and width configuration items.
 */
public class DataAccessSiteFileColumn extends DataAccessColumn
{
    protected boolean shouldDeleteFile;
    protected WOFileUploadBindings fileUploadBindings;

    protected Object fieldValue;       // Optimization
    protected boolean hasCheckedFieldValue;  // Optimization


    /**
     * Designated constructor
     */
    public DataAccessSiteFileColumn(WOContext context)
    {
        super(context);
        hasCheckedFieldValue = false;
        fileUploadBindings = new WOFileUploadBindings();
    }



    /**
     * Overridden to handle performing the upload and deletion.
     */
    public void takeValuesFromRequest(WORequest aRequest, WOContext aContext)
    {
        super.takeValuesFromRequest(aRequest, aContext);

        if (shouldDeleteFile())
        {
            setFieldValue(null);
            website().updateFileSizeUsage();
        }
        else
        {
            if (fileUploadBindings().wasFileUploaded())
            {
                // Check for quota violation
                long availableSpace = website().availableSpaceInBytes();

                // We can reuse any space occupied by the existing file
                if (siteFile() != null)
                {
                    availableSpace += siteFile().fileSizeUsage().longValue();
                }

                if (availableSpace < fileUploadBindings().uploadedFile().length())
                {
                    NSNumberFormatter quotaFormatter = ((Application)application()).properties().numberFormatterForKey("StandardNumberFormatter");
                    String availableQuota = quotaFormatter.format(website().availableSpaceInMegaBytes());
                    validationMessageStore().registerValidationFailureForKey("Upload refused.  Uploading this file will exceed your available quota of " + availableQuota + " MB.", dataAccessColumn().normalizedFieldName() + ".quota");
                }
                else
                {
                    // Create a VirtualSiteFileField / SiteFile to hold the uploaded data if it does not already exist.
                    SiteFile siteFile = siteFile();
                    if (siteFile == null)
                    {
                        siteFile = dataAccessSiteFileColumn().createSiteFile();
                        setFieldValue(siteFile);
                    }
                    else
                    {
                        /* Manually tell the field itself that it has changed (this triggers
                         * notifications and Modified By and Modified Date updates).  This is only
                         * needed if the field already existed.
                         */
                        field().willChange();
                    }

                    fileUploadBindings().setFolder(website().databaseTablesFolder());
                    siteFile.initializeFromUpload(fileUploadBindings());
                    siteFile.setUploadDate(Date.now());

                    // The bindings will be reset and reused for the next record in the list.  When
                    // that happens, we lose the information about what we just uploaded and that has
                    // not yet been saved.  We create a new object for the next record to use.
                    fileUploadBindings = new WOFileUploadBindings();
                }
            }
        }
    }



    /**
     * Reset our instance variables as we are stateless.
     */
    public void reset()
    {
        super.reset();
        fileUploadBindings.reset();
        setShouldDeleteFile(false);

        fieldValue = null;
        hasCheckedFieldValue = false;
    }



    /**
     * Returns the URL to display the associated SiteFile.  This can not be used if hasUnsavedChanges() as the wrong file contents will be returned.
     *
     * @return the URL to display the associated SiteFile.
     */
    public String fileUrl()
    {
        /** require [has_fieldValue] fieldValue() != null;  [has_been_saved]  ! hasUnsavedChanges();  **/
        return SMDataAccessActionURLFactory.fileUrl((DataAccessParameters)valueForBinding("dataAccessParameters"),
                                                    dataAccessSiteFileColumn(),
                                                    siteFile());
        /** ensure [valid_response]  Result != null;  **/
    }



    /**
     * Returns <code>true</code> if there are unsaved changes to the image data.
     *
     * @return <code>true</code> if there are unsaved changes to the image data.
     */
    public boolean hasUnsavedChanges()
    {
        return (siteFile() != null) && siteFile().hasNewData();
    }



    /**
     * Returns <code>true</code> if the contents of this file should be displayed, rather than a link, when it is an image file.
     *
     * @return <code>true</code> if the contents of this file should be displayed, rather than a link, when it is an image file.
     */
    public boolean showFileContents()
    {
        /** require [has_dataAccessSiteFileColumn] dataAccessSiteFileColumn() != null;  [has_fieldValue] siteFile() != null;  **/
        return dataAccessSiteFileColumn().showFileContents() && siteFile().isImageFile();
    }



    /**
     * Returns <code>true</code> if the 'Filename: racoon.gif (MIME Type: image/gif; Size: 4.0 Kb)' text summary and URL should be shown.  This is always shown when the column is editable, even if the file contents are also displayed, and when the column is not editable and the file contents are not displayed.
     *
     * @return <code>true</code> if the text summary and URL should be shown.
     */
    public boolean showTextDescription()
    {
        /** require [has_dataAccessSiteFileColumn] dataAccessSiteFileColumn() != null;  **/
        return ( ! (dataAccessSiteFileColumn().showFileContents() && siteFile().isImageFile())) ||  dataAccessSiteFileColumn().isEditable();
    }



    /**
     * Returns the validation error caused by an invalid width value value when configuring this DataAccessSiteFileColumn.  Returns null if no validation error occured.
     *
     * @return the validation error caused by an invalid width value value when configuring this DataAccessSiteFileColumn or null if no validation error occured.
     */
    public String widthValidationFailure()
    {
        /** require
        [edit_mode] isInEditMode();
        [parent_is_ValidationMessageStore]  parent() instanceof com.gvcsitemaker.core.interfaces.ValidationMessageStore;
        **/
        return validationMessageStore().validationFailureForKey(dataAccessColumn().normalizedFieldName() + ".width");
     }



    /**
     * Returns the validation error caused by an upload exceeding the remaining quota.  Returns null if no validation error occured.
     *
     * @return the validation error caused by an upload exceeding the remaining quota.  Returns null if no validation error occured.
     */
    public String quotaValidationFailure()
    {
        /** require
        [edit_mode] isInDisplayMode();
        [parent_is_ValidationMessageStore]  parent() instanceof com.gvcsitemaker.core.interfaces.ValidationMessageStore;
        **/
        return validationMessageStore().validationFailureForKey(dataAccessColumn().normalizedFieldName() + ".quota");
     }



    /**
     * Overridden to handle validation errors caused by data input when displaying or configuring this PageComponent.
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        if (keyPath.endsWith("width"))    // Edit Mode
        {
            validationMessageStore().registerValidationFailureForKey(t.getMessage(), dataAccessColumn().normalizedFieldName() + ".width");

            // Force the invalid value into the EO so that the user can see what was wrong.
            takeValueForKeyPath(value, keyPath);
        }
        else
        {
             super.validationFailedWithException(t, value, keyPath);
        }
    }



    /**
     * Returns the String to display for this field value when uneditable.
     * Creates an HTML link and does XML escaping if required.
     *
     * @return the String to display for this field value
     */
    public String fieldDisplayValue()
    {
        StringBuffer linkHTML = new StringBuffer(255);
        linkHTML.append("<a href=\"");
        linkHTML.append(fileUrl());
        linkHTML.append("\" target=\"_daFile\">");
        linkHTML.append(siteFile().uploadedFilename());
        linkHTML.append("</a>");

        String fieldDisplayValue = linkHTML.toString();
        if (shouldXMLEncodeData())
        {
            fieldDisplayValue = ERXStringUtilities.escapeNonXMLChars(fieldDisplayValue);
        }

        return fieldDisplayValue;
    }




    //*********** Generic Get / Set methods  ***********\\

    public Object fieldValue()
    {
        // Optimization
        if ( ! hasCheckedFieldValue)
        {
            fieldValue = valueForBinding("fieldValue");
            hasCheckedFieldValue = true;
        }

        return fieldValue;
    }
    public void setFieldValue(Object newValue)
    {
        hasCheckedFieldValue = false;
        super.setFieldValue(newValue);
        /** ensure [value_set] (newValue == null) || fieldValue().equals(newValue); **/
    }
    public SiteFile siteFile()
    {
        return (SiteFile) fieldValue();
    }

    public boolean shouldDeleteFile() {
        return shouldDeleteFile;
    }
    public void setShouldDeleteFile(boolean newShouldDeleteFile) {
        shouldDeleteFile = newShouldDeleteFile;
    }

    public Website website()
    {
        return dataAccessSiteFileColumn().sectionUsedIn().website();
    }

    public com.gvcsitemaker.core.pagecomponent.DataAccessSiteFileColumn dataAccessSiteFileColumn()
    {
        return (com.gvcsitemaker.core.pagecomponent.DataAccessSiteFileColumn) componentObject();
    }

    public WOFileUploadBindings fileUploadBindings() {
        return fileUploadBindings;
    }
    public void setFileUploadBindings(WOFileUploadBindings newFileUploadBindings) {
        fileUploadBindings = newFileUploadBindings;
    }

}
