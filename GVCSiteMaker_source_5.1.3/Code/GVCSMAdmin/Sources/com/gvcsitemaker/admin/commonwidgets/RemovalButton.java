// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.commonwidgets;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

public class RemovalButton extends WOComponent
{

    protected String removeButtonName;
    protected String removeButtonActionName;
    protected String removeWarningMessage;
    public Deleteable objectToDelete;


    public RemovalButton(WOContext aContext)
    {
        super(aContext);
    }



    public WOComponent confirmDelete() {
        if( objectToDelete.canBeDeleted() ) {
            ConfirmDeletePage nextPage = (ConfirmDeletePage)pageWithName("ConfirmDeletePage");
            nextPage.setMessage("Are you sure you want to " + getRemoveButtonName().toLowerCase() + "?");
            nextPage.setCallingPage(this);
            return nextPage;
        }
        else {
            NSMutableDictionary errorDict = new NSMutableDictionary();
            errorDict.setObjectForKey(objectToDelete.errorMessages(),
                                      Deleteable.DELETE_NOT_PERMITTED_ERROR_KEY);
            this.parent().takeValueForKey(errorDict, "errorMessages");
            return null;
        }
    }

    public WOComponent removeAction() {
        return (WOComponent) performParentAction( getRemoveButtonActionName() );
    }

    /* ********** Generic setters and getters ************** */
    public String getRemoveButtonName() {
        return removeButtonName;
    }

    public void setRemoveButtonName( String newRemoveButtonName ) {
        removeButtonName = newRemoveButtonName;
    }

    public String getRemoveButtonActionName() {
        return removeButtonActionName;
    }

    public void setRemoveButtonActionName( String newRemoveButtonActionName ) {
        removeButtonActionName = newRemoveButtonActionName;
    }

    public String getRemoveWarningMessage() {
        return removeWarningMessage;
    }

    public void setRemoveWarningMessage( String newRemoveWarningMessage ) {
        removeWarningMessage = newRemoveWarningMessage;
    }
}
