package com.gvcsitemaker.components;

import com.gvcsitemaker.core.*;
import com.webobjects.appserver.*;


public class CurrentSiteInfoComponent extends WOComponent
{
    public Website website;


    public CurrentSiteInfoComponent(WOContext context)
    {
        super(context);
    }


    /**
     * Returns the string "published" or "unpublished" depending on whether the website is published or unpublished.
     *
     * @return the string "published" or "unpublished" depending on whether the website is published or unpublished
     */
    public String publishedString()
    {
        return ((website != null) && (website.isPublished())) ? "published" : "unpublished";
    }


    /**
     * Returns the string "published" or "unpublished" depending on whether the website is published or unpublished.
     *
     * @return the string "published" or "unpublished" depending on whether the website is published or unpublished
     */
    public String publishedToggleString()
    {
        return ( ! ((website != null) && (website.isPublished()))) ? "publish" : "unpublish";
    }


    /**
     * Toggles the value of the isPublished ivar on the current web site.
     */
    public void togglePublished()
    {
        website.setIsPublished( ! website.isPublished());
        website.editingContext().saveChanges();
    }



}
