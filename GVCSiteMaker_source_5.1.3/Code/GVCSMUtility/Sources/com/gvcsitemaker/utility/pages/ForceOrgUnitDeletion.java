// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.utility.pages;
import java.util.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import com.webobjects.eocontrol.EOEditingContext;

/**
 * Forces the deletion of an OrgUnit, and all Styles, Websites, and OrgUnits below it.
 */
public class ForceOrgUnitDeletion extends WOComponent implements WOSMConfirmable
{
	public NSArray rootOrgUnits;
	public OrgUnit selectedOrgUnit;
    public String errorMessage;
	public EOEditingContext notifyingEditingContext = null;

    
    public ForceOrgUnitDeletion(WOContext context)
    {
        super(context);
        rootOrgUnits = new NSArray(OrgUnit.rootUnit(session().defaultEditingContext()));

        EODatabaseContext.setDefaultDelegate(null);  // Hack around DB consistency issues
    }




    /**
     * Method used to initiate deletion of an OrgUnit section. ConfirmAction confirms the deletion which is actually performed by confirmAction().
     */
    public WOComponent deleteSelectedUnit()
    {

        if (selectedOrgUnit == null)
        {
            errorMessage = "Select an Org Unit first";
            return this;
        }

        if (selectedOrgUnit.isRootUnit())
        {
            errorMessage = "Can not delete root unit!";
            return this;
        }

        errorMessage = null;
        
        ConfirmAction confirmDelete = (ConfirmAction)pageWithName("ConfirmAction");

        confirmDelete.setCallingComponent(this);
        confirmDelete.setDescriptor("Org Unit");
        confirmDelete.setActionName("delete");
        confirmDelete.setMessage(selectedOrgUnit.unitName());
        
        return confirmDelete;
    }


    
    /**
     *	Method used to perform the deletion of an OrgUnit. Part of the WOSMConfirmable interface.
     */
    public WOComponent confirmAction(boolean value)
    {
        if (value)
        {
            notifyingEditingContext = ((SMSession)session()).newEditingContext();
            try
            {
                deleteOrgUnit((OrgUnit)EOUtilities.localInstanceOfObject(notifyingEditingContext, selectedOrgUnit));
                notifyingEditingContext.saveChanges();
            }
            finally
            {
                ((SMSession)session()).unregisterEditingContext(notifyingEditingContext);
                notifyingEditingContext = null;
            }
            
        }

        return pageWithName("Main");
    }


    

    public void deleteOrgUnit(OrgUnit unitToDelete)
    {
        DebugOut.println(1, "Preparing to delete OrgUnit " + unitToDelete.unitName());        

        // First off delete our websites
        Enumeration websites = new NSArray(unitToDelete.websites()).objectEnumerator();  // Copy collection as enumeration loop modifies it
        while (websites.hasMoreElements())
        {
            Website aWebsite = (Website) websites.nextElement();
            DebugOut.println(1, "Deleting website " + aWebsite.siteID());        

            notifyingEditingContext.deleteObject(aWebsite);
        }

        // Recursively delete child units
        // Copy collection as enumeration loop modifies it
        Enumeration childUnits = new NSArray(unitToDelete.childOrgUnits()).objectEnumerator();
        while (childUnits.hasMoreElements())
        {
            OrgUnit childUnit = (OrgUnit) (OrgUnit) childUnits.nextElement();
            DebugOut.println(1, "Deleting child unit " + childUnit.unitName());
            deleteOrgUnit(childUnit);
        }

        // Now nothing else has a reference so we can delete the SectionStyles
        NSArray stylesInUnit = EOUtilities.objectsWithQualifierFormat(notifyingEditingContext,
                                                                      "SectionStyle",
                                                                      "owningOrgUnit = %@",
                                                                      new NSArray(unitToDelete));

        Enumeration sectionStyles = stylesInUnit.objectEnumerator();
        while (sectionStyles.hasMoreElements())
        {
            SectionStyle aSectionStyle = (SectionStyle) sectionStyles.nextElement();
            DebugOut.println(1, "Deleting Style " + aSectionStyle.styleID());        

            notifyingEditingContext.deleteObject(aSectionStyle);
        }


        // We are all alone, delete this unit
        DebugOut.println(1, "Actually deleting OrgUnit " + unitToDelete.unitName());
        notifyingEditingContext.deleteObject(unitToDelete);
    }



}
