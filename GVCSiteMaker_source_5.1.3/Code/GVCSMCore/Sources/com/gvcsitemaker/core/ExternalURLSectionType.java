/**
 * Implementation of ExternalURLSectionType common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */  
package com.gvcsitemaker.core;

import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;


public class ExternalURLSectionType extends _ExternalURLSectionType
{
  


    /**
     * Returns the singleton instance of ExternalURLSectionType in the passed EOEditingContext
     */
    public static ExternalURLSectionType getInstance(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        return (ExternalURLSectionType) getInstance(ec, "ExternalURLSectionType");
        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Factory method to create new instances of ExternalURLSectionType.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of ExternalURLSectionType or a subclass
     */
    public static ExternalURLSectionType newExternalURLSectionType()
    {
        return (ExternalURLSectionType) SMEOUtils.newInstanceOf("ExternalURLSectionType");

        /** ensure [return_not_null] Result != null; **/
    }

    

    /**
     * Returns the external URL as the URL for the passed Section.  This method should NOT be used by client code.  Use one of the sectionURL() methods on Section instead.
     *
     * @param aSection the Section to return the URL for.
     * @param aRequest the request to take the domain name from
     *
     * @return the external URL as the URL for the passed Section
     */
    public String urlForSection(Section aSection, WORequest aRequest)
    {
        return ((Hyperlink)aSection.component()).urlForRequest(aRequest);

        // TODO: this can be violated when an External URL section is created without a URL (yes, that can happen...) /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Returns false as External URls cannot be emebedded in another Section.
     *
     * @returns false as External URls cannot be emebedded in another Section
     */
    public boolean canBeEmbedded()
    {
        return false;
    }

    
    
    /**
     * Returns false as external URL section can not be home Sections 
     *
     * @returns false as external URL section can not be home Sections 
     */
    public boolean canBeHomeSection(Section aSection)
    {
        /** require [section_is_valid] aSection != null;   **/
        return false;
    }
    
    
    
    /**
     * Returns <code>false</code> as there is no way to access protect external URLs.
     *
     * @param aSection - the Section to check type of access protection for
     * @return <code>false</code> as there is no way to access protect external URLs
     */
    public boolean usesAccessProtection(Section aSection)
    {
        /** require [valid_section] aSection != null;    **/
        return false;
    }
    
        
    
    /**
     * Returns <code>true</code> if this aSection references something outside of GVC.SiteMaker.
     *
     * @return <code>true</code> if this aSection references something outside of GVC.SiteMaker
     */
    public boolean isExternalReference(Section aSection)
    {
        /** require [valid_section] aSection != null;    **/
        
        return ((Hyperlink)aSection.component()).isExternalLink();
    }
    
    
}

