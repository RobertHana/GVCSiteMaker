// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


public class ConfigAppearance extends com.gvcsitemaker.core.components.WebsiteContainerBase implements SMSecurePage
{
    protected SectionStyle aStyle;
    private String bannerText;

    private boolean isBannerValid = true;

    /** @TypeInfo SectionStyle */
    protected NSArray styles;

    public boolean showCustomFooter = false;


    public ConfigAppearance(WOContext aContext)
    {
        super(aContext);
    }



    public void awake()
    {
        super.awake();

        setPreviewURL("");

        if ((website() != null) && isBannerTitleNull())
        {
            setBannerTitle(website().banner().bannerText());
        }
    }


    public void saveChanges()
    {
        validateBannerTitle();
        saveChangesIfValid();
    }


    public void savePreviewChanges()
    {
        validateBannerTitle();
        saveChangesIfValid();

        if (isValid())
        {
            setPreviewURL(website().siteURL());
        }
    }



    /**
     * Returns <code>true</code> if the banner title string .
     *
     * @return <code>true</code> if the banner title string (on the page, not in the EO) is null or the empty string.
     */
    public boolean isValid()
    {
        return isBannerValid() && isFooterValid();
    }



    /**
     * Saves all data on the page if isValid().
     */
    protected void saveChangesIfValid()
    {
        DebugOut.println(20, "In saveChangesIfValid");
        if (isValid())
        {
            DebugOut.println(20, "...saving");
            //session().defaultEditingContext().saveChanges();
            website().editingContext().saveChanges();
        }
    }



    /**
     * Validates the banner title text and leaves isValid() set to indicate if it is valid or not.  If the banner title is valid, it is set into the website's banner.
     */
    private void validateBannerTitle()
    {
        if (isBannerTitleNull())
        {
            setIsBannerValid(false);
            setBannerTitle(website().banner().bannerText());
        }
        else
        {
            setIsBannerValid(true);
            website().banner().setBannerText(getBannerTitle());
        }
    }



    /**
     * Returns <code>true</code> if the banner title string (on the page, not in the EO) is null or the empty string.
     *
     * @return <code>true</code> if the banner title string (on the page, not in the EO) is null or the empty string.
     */
    protected boolean isBannerTitleNull()
    {
        return (getBannerTitle() == null) || (getBannerTitle().length() < 1);
    }



    /**
     * Returns <code>true</code> if the footer is valid (all URLs are empty or valid at this time, more stringent validations later?).
     *
     * @return <code>true</code> if the footer is valid (all URLs are empty or valid at this time, more stringent validations later?).
     */
    public boolean isFooterValid()
    {
        return (!(isFooterLinkURL1Invalid() || isFooterLinkURL2Invalid() || isFooterLinkURL3Invalid() || isFooterLinkURL4Invalid()));
    }



    //
    // Lame URL validation cying out for a better solution  :-)
    public boolean isFooterLinkURL1Invalid()
    {
        String urlText = website().footer().linkURL1();
        return (urlText != null) && !URLUtils.hasValidURIScheme(urlText);
    }


    public boolean isFooterLinkURL2Invalid()
    {
        String urlText = website().footer().linkURL2();
        return (urlText != null) && !URLUtils.hasValidURIScheme(urlText);
    }


    public boolean isFooterLinkURL3Invalid()
    {
        String urlText = website().footer().linkURL3();
        return (urlText != null) && !URLUtils.hasValidURIScheme(urlText);
    }


    public boolean isFooterLinkURL4Invalid()
    {
        String urlText = website().footer().linkURL4();
        return (urlText != null) && !URLUtils.hasValidURIScheme(urlText);
    }


    public String invalidFooterLinkURL1Message()
    {
        return URLUtils.invalidURLErrorMessage(website().footer().linkURL1());
    }


    public String invalidFooterLinkURL2Message()
    {
        return URLUtils.invalidURLErrorMessage(website().footer().linkURL2());
    }


    public String invalidFooterLinkURL3Message()
    {
        return URLUtils.invalidURLErrorMessage(website().footer().linkURL3());
    }


    public String invalidFooterLinkURL4Message()
    {
        return URLUtils.invalidURLErrorMessage(website().footer().linkURL4());
    }


    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Configure Overall Appearance";
    }


    /* Generic setters and getters ***************************************/
    public SectionStyle aStyle()
    {
        return aStyle;
    }


    public void setAStyle(SectionStyle newAStyle)
    {
        aStyle = newAStyle;
    }


    public String previewURL()
    {
        //return ((ConfigTabSet)parent()).previewURL();
        return "";
    }


    public void setPreviewURL(String newPreviewURL)
    {
        //((ConfigTabSet)parent()).setPreviewURL(newPreviewURL);
    }


    public String getBannerTitle()
    {
        return bannerText;
    }


    public void setBannerTitle(String newBannerTitle)
    {
        bannerText = newBannerTitle;
    }


    public boolean isBannerValid()
    {
        return isBannerValid;
    }


    public void setIsBannerValid(boolean newState)
    {
        isBannerValid = newState;
    }



    public void showHideCustomFooter()
    {
        showCustomFooter = ! showCustomFooter;
    }



    /**
     * Sets the Website to be displayed / edited.  Overriden to clear the cached values
     * if the website changes.
     *
     * @param the Website to be displayed / edited
     */
    public void setWebsite(Website newWebsite)
    {
        /** require [new_website_not_null] newWebsite != null; **/

        if (newWebsite != website())
        {
            styles = null;
        }
        super.setWebsite(newWebsite);

        /** ensure [website_not_null] website() != null; **/
    }



    /** @TypeInfo SectionStyle */
    public NSArray styles()
    {
        if ((website() != null) && (styles == null))
        {
            styles = SectionStyle.stylesAdvailableForWebsite(website());
        }
        return styles;
    }



    /**
     * Returns the JS used to preview the site in a new window.
     *
     * @return the JS used to preview the site in a new window
     */
    public String previewJS()
    {
        return "window.open('" + website().siteURL() + "', '_preview')";
    }



    /**
     * Returns the string to use to init TinyMCE.
     *
     * @return the string to use to init TinyMCE
     */
    public String customJSEditorInitString()
    {
        return SMApplication.appProperties().stringPropertyForKey("tinymce-init-configappearance.js");
    }



}
