// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.editors;
import com.gvcsitemaker.core.Section;
import com.gvcsitemaker.core.pagecomponent.Layout;
import com.gvcsitemaker.core.pagecomponent.ListLink;
import com.gvcsitemaker.core.support.OrderedComponentList;
import com.gvcsitemaker.core.utility.URLUtils;
import com.gvcsitemaker.pages.DisplayFile;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;


/**
 * This editor used by EditSection to edit a ListOfLinksLayout Component containing 
 * a List of Links.  Individual links are edited in the linkEditingContext.  The
 * list as a whole is edited in the editingContext() inherited from BaseEditor.
 */
public class ListOfLinksEditor extends BaseEditor 
{
    /**
     * The ListLinkComponent being displayed / edited at the top of the page.
     */
    protected com.gvcsitemaker.core.pagecomponent.ListLink selectedListLink;
    
    /**
     * List of components in this Layout in their display order.  
     * Used by WORepetition.
     */
	public OrderedComponentList orderedComponents;
    
    /**
     * Current item in WORepetition over orderedComponents.
     */
    public com.gvcsitemaker.core.pagecomponent.ListLink aLink;
    
    /**
     * Editing context for individual links.
     */
    protected EOEditingContext linkEditingContext;
    

    /**
     * Designated constructor
     */
    public ListOfLinksEditor(WOContext aContext)
    {
        super(aContext);
        linkEditingContext = smSession().newEditingContext();
    }

    

    /**
     * Takes the user to the main config page.
     *
     * @return the main config page
     */
    public WOComponent backToWebsiteSections()
    {
        return pageWithName("ConfigTabSet");
    }



    /**
     * Sets the Section whose list of links is being edited.  Overridden to also 
     * set orderedComponents.
     *
     * @param aSection - the  Section whose list of links should be edited
     */
    public void setSection(Section aSection)
    {
        /** require [valid_param] aSection != null;  **/
        if (section != aSection)
        {
            super.setSection(aSection);
            orderedComponents = new OrderedComponentList(sectionLayout().orderedComponents());
            
            // Page initially displays adding a new link.
            linkEditingContext().revert();
            createNewListLink();    
        }
        /** ensure [section_set] section() == aSection;  **/
    }



    /**
     * Creates a new ListLink page component instance and sets it as the 
     * selectedListLink().
     */
    protected void createNewListLink()
    {
        ListLink newListLink = com.gvcsitemaker.core.pagecomponent.ListLink.newListLink();
        Layout layout = (Layout) EOUtilities.localInstanceOfObject(linkEditingContext(), 
                                                                   sectionLayout());
        layout.addChild(newListLink);  
        setSelectedListLink(newListLink);

        /** ensure [link_set] selectedListLink() != null;
                   [link_inserted] linkEditingContext().insertedObjects().containsObject(selectedListLink());
                   [new_link] addMode();  **/
    }



    /**
     * Work horse method to create a new link or update an existing one.  Saves 
     * the link, creates a new link, displays the new ListLink at the top of the 
     * page, and (optionally) shows a preview of the section.
     *
     * @param shouldPreview <code>true</code> if the Section preview should be shown
     * @return the current page
     */
    protected WOComponent _doUpdateWithPreview(boolean shouldPreview)
    {
        WOComponent resultPage;

        if (selectedListLink().isMissingAnyRequiredField())
        {
            return error("You must fill in Item Name as well as Type and Source");
        }
        else if (! selectedListLink().isUriValid())
        {
            return invalidHrefError();
        }
        else
        {
            linkEditingContext().saveChanges();
            
            updateList();

            if (shouldPreview)
            {
                showPreview();
            }

            createNewListLink();
            resultPage = context().page();
        }

        return resultPage;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the ErrorPage with a message for an invalid external URL link.
     *
     * @return the ErrorPage with a message for an invalid external URL link.
     */
    protected WOComponent invalidHrefError()
    {
        return error(URLUtils.invalidURLErrorMessage(selectedListLink.uri() ) );
        /** ensure [valid_result] Result != null;  **/
    }


    
    /**
     * Removes the current item in the WORepetition from the components in this layout.
     */
    public void deleteLink()
    {
        sectionLayout().removeChild(aLink());
        updateList();

        /** ensure [link_deleted] ! sectionLayout().orderedComponents().containsObject(aLink());  **/
    }


    
    /**
     * Sets the current item in the WORepetition as selectedListLink() so that it can be edited.  
     * This method is bound to the Revise link beside eack link.
     */
    public WOComponent reviseLink()
    {
        linkEditingContext().revert();
        setSelectedListLink((ListLink)EOUtilities.localInstanceOfObject(linkEditingContext(), aLink()));
        return context().page();
    }


    
    /**
     * Returns DisplayFile page configured to show the file linked to a aLink.  This applies only to 
     * list links to SiteFiles.
     *
     * @return DisplayFile page configured to show the file linked to a aLink.
     */
    public WOComponent displayFile()
    {
        /** require [is_file] aLink.isSiteFileLink();  **/

        DisplayFile displayFile = (DisplayFile)pageWithName("DisplayFile");
        displayFile.setAuthOverride(true);
        displayFile.setFile(aLink().toRelatedFile());

        return displayFile;
    }

    
    
    /**
     * Cancels all changes and redisplays this page.
     * 
     * @return this page
     */
    public WOComponent cancelChanges()
    {
        linkEditingContext().revert();
        createNewListLink();

        return context().page();
    }


    
    /**
     * Moves this link closer to the start of the list of links and redisplays the page.
     */
    public WOComponent moveUp()
    {
        sectionLayout().moveEarlier(aLink());
        updateList();

        return context().page();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Moves this link closer to the end of the list of links and redisplays the page.
     */
    public WOComponent moveDown()
    {
        sectionLayout().moveLater(aLink());
        updateList();

        return context().page();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Moves this link to the start of the list of links and redisplays the page.
     */
    public WOComponent moveFirst()
    {
        sectionLayout().moveFirst(aLink());
        updateList();

        return context().page();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Moves this link to the end of the list of links and redisplays the page.
     */
    public WOComponent moveLast()
    {
        sectionLayout().moveLast(aLink());
        updateList();

        return context().page();
        /** ensure [valid_result] Result != null;  **/
    }




    /**
     * Saves changes to the list of links at the bottom and refreshes the list.
     */
    public void updateList()
    {
        editingContext().saveChanges();
        orderedComponents = new OrderedComponentList(sectionLayout().orderedComponents());
    }


    
    /**
     * Returns the file size in kilobytes for a link to an uploaded SiteFile, or a place holder string if this is not applicable or available.  This is used in the table of list items to show the size of an uploaded file that has been included in the list.
     *
     * @return the file size in kilobytes for a link to an uploaded SiteFile, or a place holder string if this is not applicable or available.  
     */
    public String fileSize()
    {
        if (aLink().isExternalLink() || aLink().isFileMissing())
        {
            return new String(" --- ");
        }
        else
        {
            // Return the size of the file
            return (new Float(aLink().toRelatedFile().fileSizeInKilobytes())).toString();
        }
    }



    /**
     * Returns string containing "URL:" or "FileName:" followed by the path or file name to the link. This is used in the table of list items to show information about what is linked to.
     *
     * @return string containing "URL:" or "FileName:" followed by the path or file name to the link.
     */
    public String fileInfo()
    {
        String fileInfo;

        if (aLink().isSiteFileLink())
        {
            if (aLink().isFileMissing())
            {
                fileInfo =  "<FONT color=\"#FF0000\">File no longer available</FONT>";
            }
            else
            {
                fileInfo = "Filename: " + aLink().toRelatedFile().uploadedFilename();
            }
        }
        else
        {
            fileInfo = "URL: " + aLink().linkURI(context().request());
        }

        return fileInfo;
    }



    /**
     * Returns the URI for an internally held file.
     *
     * @return the URI for an internally held file.
     *
     * @return != null
     */
    public String linkURI()
    {
        return aLink().linkURI(context().request());
        /** ensure [valid_result] Result != null;  **/
    }


    
    /**
     * Convenience method returning the ListOfLinksLayout for this Section.
     *
     * @return the ListOfLinksLayout Component for this Section.
     */
    public com.gvcsitemaker.core.pagecomponent.ListOfLinksLayout sectionLayout()
    {
        /** require [has_section] section() != null;  **/
        return (com.gvcsitemaker.core.pagecomponent.ListOfLinksLayout)section().component();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the order of aLink() in orderedComponents() (e.g. 1, 2, 3 etc.).  This is used to populate the first column of the table of list items.
     *
     * @return the order of aLink() in orderedComponents() (e.g. 1, 2, 3 etc.).
     */
    public int placementOrder()
    {
        return orderedComponents.ordered().indexOfObject(aLink()) + 1;
    }


    /**
     * Returns <code>true</code> if the page is in "add link" mode, that is if selectedListLink() returns newListLink() rather that one of the elements of orderedComponents().  This is used to control what is displayed in the UI, for example Add versus Update button labels.
     *
     * @return <code>true</code> if the page is in "add link" mode
     */
    public boolean addMode()
    {
        return linkEditingContext().globalIDForObject(selectedListLink()).isTemporary();
    }


    /**
     * Do nothing method to satisfy binding synchronization.
     */
    public void setAddMode(boolean dummy)
    {
    }



    /**
     * Returns the EOEditingContext used by this editor.
     * 
     */
    public EOEditingContext linkEditingContext()
    {
        return linkEditingContext;
    }
  

    /**
     * Returns the ListLink which is being displayed / edited at the top of the page.  This is either one of the elements in orderedComponents() or is newListLink().
     *
     * @return the ListLink which is being displayed / edited at the top of the page.
     */
    public com.gvcsitemaker.core.pagecomponent.ListLink selectedListLink()
    {
        return selectedListLink;
    }


    
    /**
     * Sets the ListLink which is to be displayed / edited at the top of the page.  This is either one of the elements in orderedComponents() or is newListLink().
     *
     * @param aListLinkComponent - the ListLink which is to be displayed / edited at the top of the page.
     */
    public void setSelectedListLink(com.gvcsitemaker.core.pagecomponent.ListLink aListLinkComponent)
    {
        /** require [valid_parameter]  aListLinkComponent != null;   **/
        selectedListLink = aListLinkComponent;
        /** ensure  [link_set] selectedListLink() == aListLinkComponent;  **/
    }


    
    /**
     * Returns the current item in WORepetition over orderedComponents().
     *
     * @return the current item in WORepetition over orderedComponents().
     */
    public com.gvcsitemaker.core.pagecomponent.ListLink aLink()
    {
        return aLink;
        /** ensure [valid_result] Result != null;  **/
    }


    
    /**
     * Sets the current item in WORepetition over orderedComponents().
     *
     * @param newLink -  the current item in WORepetition over orderedComponents()
     */
    public void setALink(com.gvcsitemaker.core.pagecomponent.ListLink newLink)
    {
        aLink = newLink;
        /** ensure [link_set] aLink == newLink;  **/
    }
    
    
    /**
     * Update list so orderedComponents will be up to date
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);
        updateList();
    }    

}
