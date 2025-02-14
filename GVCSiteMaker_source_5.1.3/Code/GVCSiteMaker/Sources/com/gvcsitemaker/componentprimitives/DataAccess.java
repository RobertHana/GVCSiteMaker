// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.componentprimitives;

import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.appserver.SMSession;
import com.gvcsitemaker.core.components.PageScaffold;
import com.gvcsitemaker.core.pagecomponent.PageComponent;
import com.gvcsitemaker.core.support.DataAccessParameters;
import com.gvcsitemaker.core.utility.DebugOut;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import net.global_village.eofextensions.EODatabaseContextAdditions;


/**
 * DataAccess implements the UI for a com.gvcsitemaker.core.pagecomponent.DataAccess PageComponent.  It determines the mode of the section (single, list, add, search), and defers the display request to the appropriate DataAccessMode component.  It also holds the editing context for the Section.
 */
public class DataAccess extends ComponentPrimitive
{
    protected DataAccessParameters dataAccessParameters;
    protected EOEditingContext editingContext;


    /**
     * Designated constructor.
     */
    public DataAccess(WOContext context)
    {
        super(context);
    }



    /*
     * Overidden to get binding values..
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        // This is needed as this component is non-synchronizing and editingContext() and dataAccessParameters() rely on this binding.
        // If it is not set when the page is rendered the dependant values are referenced first resulting in an null pointer exception.
        // See setComponentObject() in this class and componentObject() in ComponentPrimitive for details.
        componentObject();
        super.appendToResponse(aResponse, aContext);
    }



    /**
     * Overridden to use local editing context.
     */
    public void setComponentObject(PageComponent aPageComponent)
    {
        /** require [has_context] context() != null; [valid_param] aPageComponent != null;  **/

        if (editingContext == null)
        {
            // Determine the DataAccess mode of the request.
            com.gvcsitemaker.core.pagecomponent.DataAccess dataAccessComponent = (com.gvcsitemaker.core.pagecomponent.DataAccess)aPageComponent;
            String modeName = dataAccessComponent.validatedModeName(DataAccessParameters.modeNameFrom(context().request()));
            com.gvcsitemaker.core.pagecomponent.DataAccessMode mode = dataAccessComponent.componentForMode(modeName);

            // If this mode uses Component Actions then this page needs to be cached in case the form is submitted.
            if (mode.usesComponentActions())
            {
                pageScaffold().setSaveInPageCache(true);
            }

            // Choose the editing context to use based on whether this mode has any editable content.
            if (mode.isEditable())
            {
                // This is used for special handling when SiteFiles are inserted, updated, and deleted.
                editingContext = ((SMSession)session()).newEditingContext();

                // Performance optimization by avoiding refetching data.
                if (SMApplication.appProperties().booleanPropertyForKey("EditableDAModesUseSessionsTimestamp"))
                {
                    editingContext().setFetchTimestamp(session().defaultEditingContext().fetchTimestamp());
                }

                super.setComponentObject((PageComponent) EOUtilities.localInstanceOfObject(editingContext(), dataAccessComponent));

                // Alternate, less effective performance optimization of prefetching data.
                if ( ! SMApplication.appProperties().booleanPropertyForKey("EditableDAModesUseSessionsTimestamp"))
                {
                    EODatabaseContextAdditions.preloadRelationship(new NSArray(dataAccessComponent().databaseTable()), "columns");
                }

                DebugOut.println(20, "Created NotifyingEditingContext");
            }
            else
            {
                // This is used for non-editable modes
                editingContext = dataAccessComponent.editingContext();
                super.setComponentObject(dataAccessComponent);
                DebugOut.println(20, "Using standard EditingContext");
            }

            DebugOut.println(30, "Parsing form values into new DataAccessParameters: " + context().request().formValues());
            dataAccessParameters = new DataAccessParameters(context(), dataAccessComponent());
        }

        /** ensure
        [componentObject_set] componentObject() != null;
        [has_editingContext] editingContext() != null;
        [has_dataAccessParameters] dataAccessParameters() != null;
        [same_editingContext_in_dataAccessParameters] dataAccessParameters().editingContext() == editingContext();
        [same_editingContext_in_dataAccessComponent] dataAccessComponent().editingContext() == editingContext();
        **/
    }



    /**
     * Returns componentObject() downcast to com.gvcsitemaker.core.pagecomponent.DataAccess.
     *
     * @return componentObject() downcast to com.gvcsitemaker.core.pagecomponent.DataAccess.
     */
    public com.gvcsitemaker.core.pagecomponent.DataAccess dataAccessComponent()
    {
        return (com.gvcsitemaker.core.pagecomponent.DataAccess) componentObject();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the PageScaffold instance displaying this component.
     *
     * @return the PageScaffold instance displaying this component.
     */
    public PageScaffold pageScaffold()
    {
        /** require  [has_context] context() != null;  [parent_is_PageScaffold] context().page() instanceof PageScaffold;     **/

        // This is a bit of a nasty way to get this, but it works.  Perhaps passing it down as a binding would be better?
        return (PageScaffold) context().page();

        /** enusre [valid_result]  Result != null;  **/
    }


    //************* Generic Accessors and Mutators below here *****************

    public DataAccessParameters dataAccessParameters()
    {
        return dataAccessParameters;
        /** ensure [result_valid] Result != null; **/
    }


    public EOEditingContext editingContext()
    {
        return editingContext;
        /** ensure [result_valid] Result != null; **/
    }



}
