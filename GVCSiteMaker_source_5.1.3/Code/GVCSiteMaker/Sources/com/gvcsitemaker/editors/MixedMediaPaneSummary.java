// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.editors;

import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.interfaces.WOSMConfirmable;
import com.gvcsitemaker.core.mixedmedia.MixedMediaContentConfiguration;
import com.gvcsitemaker.core.pagecomponent.MixedMediaPane;
import com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement;
import com.gvcsitemaker.core.pagecomponent.MixedMediaUnknownPane;
import com.gvcsitemaker.pages.ConfirmAction;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;

/**
 * Component to edit summary of panes on MixedMediaSectionEditor, will display list of all media types, holds a subclass of MixedMediaPane PageComponent
 */
public class MixedMediaPaneSummary extends WOComponent implements WOSMConfirmable
{
	protected NSArray contentTypes; 	// List of content types for a MixedMedia pane
	public String aContentType;		// Used in a WORepetition to display the different content types for a MixedMedia pane.
	public MixedMediaContentConfiguration selectedContentType; // Holder for selected content type
	public boolean isContentTypeUnknown; // Holder for selected content type
	public WOComponent parentComponent;
	public MixedMediaPane paneToDelete;
    public String widthValidationError;
    public String heightValidationError;    


    /**
	 * Designated constructor
	 */	
    public MixedMediaPaneSummary(WOContext context) 
    {
        super(context);
        
        contentTypes = (NSArray) SMApplication.mixedMediaContentConfigurations().valueForKey("title");
        parentComponent = context.page();
    }



    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    /**
     * Returns list of content types for a MixedMedia pane.
     *
     * @return list of content types for a MixedMedia pane
     */    
    public NSArray contentTypes()
    {
        return contentTypes;

        /** ensure [result_not_null] Result != null; **/    		
    }       



    /**
     * Convenience method to return pane being edited.
     *
     * @return the pane being edited
     */
    public com.gvcsitemaker.core.pagecomponent.MixedMediaPane pane()
    {
        return (MixedMediaPane) valueForBinding("componentObject");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenience method to return pane's parent which is a MixedMediaPaneArrangement
     *
     * @return the pane's parent which is a MixedMediaPaneArrangement
     */
    public com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement arrangement()
    {
        return (com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement) valueForBinding("arrangement");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the text to display for width based on the subtype if not required.
     *
     * @return the text to display for width based on the subtype if not required.
     */    
    public String calculatedWidthText()
    {
		String calculatedWidthText = null;
		int widthDifference = (100 - (new Integer(arrangement().rootPanelWidth())).intValue());

		//Show remaining width
		if ((arrangement().subType().equals(MixedMediaPaneArrangement.TwoPanesSideBySideLayout) || 
				arrangement().subType().equals(MixedMediaPaneArrangement.ThreePanesSideBySideOnTopLayout) ||
				arrangement().subType().equals(MixedMediaPaneArrangement.ThreePanesTopAndBottomOnRightLayout) ||
				arrangement().subType().equals(MixedMediaPaneArrangement.FourPanesLayout))
				&& (pane().order() == 2))
		{
			return widthDifference + " percent (determined by width of Pane 1)";
		}
		else if (arrangement().subType().equals(MixedMediaPaneArrangement.ThreePanesSideBySideOnBottomLayout) && (pane().order() == 3))
		{
			return widthDifference + " percent (determined by width of Pane 2)";
		}
		else if ((arrangement().subType().equals(MixedMediaPaneArrangement.ThreePanesTopAndBottomOnLeftLayout) ||
				arrangement().subType().equals(MixedMediaPaneArrangement.ThreePanesTopAndBottomOnRightLayout)) && (pane().order() == 3))
		{
			return widthDifference + " percent (determined by width of Pane 1)";
		}
		//Show same width
		else if (arrangement().subType().equals(MixedMediaPaneArrangement.ThreePanesTopAndBottomOnLeftLayout) && (pane().order() == 2))
		{
			return arrangement().rootPanelWidth() + " percent (determined by width of Pane 1)";
		}
		else if (arrangement().subType().equals(MixedMediaPaneArrangement.FourPanesLayout) && (pane().order() == 3))
		{
			return arrangement().rootPanelWidth() + " percent (determined by width of Pane 1)";
		}
		else if (arrangement().subType().equals(MixedMediaPaneArrangement.FourPanesLayout) && (pane().order() == 4))
		{
			return widthDifference + " percent (determined by width of Pane 1)";
		}

		return calculatedWidthText;
    }
    
    
    
    /**
     * Returns true if the calculatedWithText should be shown, false otherwise.
     *
     * @return true if the calculatedWithText should be shown, false otherwise.
     */    
    public boolean showCalculatedWidth()
    {
        return calculatedWidthText() != null;
    }     



    /**
     * Returns true if the maximum width should be shown, false otherwise.
     *
     * @return true if the maximum width should be shown, false otherwise.
     */   
    public boolean showMaximumWidth()
    {
        return (! (pane().isWidthRequired() || showCalculatedWidth()));
    }



    /**
     * Returns true if the content type for this summary is still unknown.
     *
     * @return true if the content type for this summary is still unknown.
     */    
    public boolean isContentTypeUnknown()
    {
        return (pane() instanceof MixedMediaUnknownPane);
    }      



    /**
     * Returns the MixedMediaPaneEditor with the selected pane for editing.
     *
     * @return the MixedMediaPaneEditor with the selected pane for editing.
     */    
    public WOComponent editPaneContents()
    {
        MixedMediaPaneEditor nextPage = (MixedMediaPaneEditor)pageWithName("MixedMediaPaneEditor");
        nextPage.setSection(arrangement().section());
        nextPage.setPane(pane());
        if (arrangement().section().isVersionable())
        {
            nextPage.setVersion(pane().arrangement().version());
        }
        return nextPage;
        
		/** ensure [result_not_null] Result != null; **/        
    }



    /**
     * Returns true if the height field should be displayed, false otherwise
     *
     * @return true if the height field should be displayed, false otherwise
     */    
    public boolean showHeight()
    {
        return ((! isContentTypeUnknown()) && (pane().contentConfiguration().requiresIFrame() || pane().displayInIFrame()));
    }



    /**
     * Method used to delete this pane's contents by replacing it with a MixedMediaUnknownPane. This is part of the WOSMConfirmable interface and
     * sets up the confirmation page with the correct message
     *
     * @return the page used to confirm deleting an object. 
     */
    public WOComponent deletePaneContents()
    {
        ConfirmAction confirmDelete = (ConfirmAction)pageWithName("ConfirmAction");
        setPaneToDelete(pane());        
        confirmDelete.setCallingComponent(this);
        confirmDelete.setDescriptor("contents of");
        confirmDelete.setActionName("Delete");
        confirmDelete.setMessage("Pane " + pane().order());
        
        return confirmDelete;
        
		/** ensure [result_not_null] Result != null; **/             
    }


    /**
     * Deletes pane if confirmed.
     *
     * @return parent component which is this page
     */     
	public WOComponent confirmAction(boolean value)
	{
		if (value)
		{
    			arrangement().replacePane(paneToDelete(), null);
    			arrangement().editingContext().saveChanges();
    			setPaneToDelete(null);
		}

		return parentComponent;

		/** ensure [result_not_null] Result != null; **/          
	}    



    /**
     * Overridden to clear validation messages
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        super.appendToResponse(response, context);
        widthValidationError = null;
        heightValidationError = null;
    }    



    /**
     * Translates validation faliure expections into UI error messages.
     */
    public void validationFailedWithException(Throwable exception, Object value, String keyPath) 
    {
        if (keyPath.equals("rootPanelWidth") ||  keyPath.equals("arrangement.rootPanelWidth"))
        {
            widthValidationError = "A width greater than zero is required.";
        }
        else if (keyPath.equals("height") ||  keyPath.equals("pane.height"))
        {
            if (pane().displayInIFrame() || (pane().isMixedMediaConfigurableContentPane() && pane().contentConfiguration().requiresIFrame()))
            {
                heightValidationError = "A height greater than zero is required.";               
            }
            else
            {
                heightValidationError = "The height must be blank, or a number larger than zero.";                 
            }
        }        
        else super.validationFailedWithException(exception, value, keyPath);
    }    



    public boolean showDescription()
    {
    		return (pane().description() != null);
    }	


	public MixedMediaPane paneToDelete()
	{
		return paneToDelete;
	}

	public void setPaneToDelete(MixedMediaPane aPane)
	{
		paneToDelete = aPane;
	}	



}