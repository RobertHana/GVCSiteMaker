// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core;


import java.util.*;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * SectionStyle controls how the Sections of a Website appear when displayed (they are also referred to as site styles because currently all Sections of a Website use the same style).  SectionStyle extends Style by adding the concept of a default style, a directory to store external resources in, and the names of the WOComponents used to display the various bits of a Section.  The resourceDirectory is restricted to having only A-Z, a-z, 0-9, period, hyphen, underline, and the forward slash (/) as it is itended to be a path.  At present the resourceDirectory is only recorded for informational purposes.
 */
public class SectionStyle extends _SectionStyle
{
    /**
     * Bug Alert!
     *
     * Do not use
     *   fetchSpec.setRefreshesRefetchedObjects(true);
     * with
     *   EOEditingContext.setDefaultFetchTimestampLag( long );
     * or it will result exceptions like these when doing fetching after an object to be fetched has been saved in another ec:
     *
     * decrementSnapshotCountForGlobalID: com.webobjects.eoaccess.EODatabase com.webobjects.eoaccess.EODatabase@38f864 -- is unable to decrement snapshot count for object with global ID _EOIntegralKeyGlobalID[SectionStyle (java.lang.Integer)1000001] - count is already 0 or this snapshot doesn't exist
     *
     * incrementSnapshotCountForGlobalID: com.webobjects.eoaccess.EODatabase com.webobjects.eoaccess.EODatabase@38f864 -- is unable to increment snapshot count for object with global ID _EOIntegralKeyGlobalID[SectionStyle (java.lang.Integer)1000005] - no snapshot exists
     */

    
    // Validation Messages
    public static final String StyleResourceDirectoryIsRequiredKey = "StyleResourceDirectoryIsRequiredKey";
    public static final String StyleResourceDirectoryIsRequiredErrorMessage = "You must enter a resource directory for this style.";

    public static final String StyleResourceDirectoryIsInvalidKey = "StyleResourceDirectoryIsInvalidKey";
    public static final String StyleResourceDirectoryIsInvalidErrorMessage = "The resource directory can only contain the characters A-Z, a-z, 0-9, period, hyphen, underline, and the forward slash (/).";

    public static final String StyleIDIsInUseKey = "StyleIDIsInUseKey";
    public static final String StyleIDIsInUseErrorMessage = "A style with this ID already exists.";


    public static final String StyleNameIsInUseKey = "StyleNameIsInUseKey";
    public static final String StyleNameIsInUseErrorMessage = "A style with this name already exists.";


    
    /**
     * Factory method to create new instances of Style.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of SectionStyle or a subclass.
     */
    public static SectionStyle newSectionStyle()
    {
        return (SectionStyle) SMEOUtils.newInstanceOf("SectionStyle");

        /** ensure [result_not_null] Result != null; **/
    } 



    /**
     * Returns the list of all styles that anAdmin can configure.  This is all the styles that User's orgUnitToAdminStylesFor() and any Org Units below that or all styles for System administrators.  The list of styles is sorted according to styleSortOrdering().
     *
     * @return the list of all styles that anAdmin can configure.
     */
    static public NSArray stylesAdvailableToAdmin(User anAdmin)
    {
        /** require [an_admin_not_null] anAdmin != null; **/
        
        NSArray stylesAdvailableToAdmin;

        if (anAdmin.isSystemAdmin())
        {
            stylesAdvailableToAdmin = allStyles(anAdmin.editingContext());
        }
        else if (anAdmin.orgUnitToAdminStylesFor() != null)
        {
            stylesAdvailableToAdmin = stylesInOrgUnit(anAdmin.orgUnitToAdminStylesFor());
        }
        else
        {
            // The user is not a sys admin nor a style admin, they get nothing
            stylesAdvailableToAdmin = NSArray.EmptyArray;
        }

        return stylesAdvailableToAdmin;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the list of all styles sorted according to styleSortOrdering()
     *
     * @return the list of all styles sorted according to styleSortOrdering()
     */
    static public NSArray allStyles(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        
        EOFetchSpecification fetchSpec = new EOFetchSpecification("SectionStyle", null, styleSortOrdering());

        return ec.objectsWithFetchSpecification(fetchSpec);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the list of all styles in anOrgUnit and all the styles in any Org Units below that.  The list of styles is sorted according to styleSortOrdering().
     *
     * @return the list of all styles in anOrgUnit and all the styles in any Org Units below that.
     */
    static public NSArray stylesInOrgUnit(OrgUnit anOrgUnit)
    {
        /** require [an_org_unit_not_null] anOrgUnit != null; **/
        
        NSArray orgUnitsInHierarchy = anOrgUnit.orderedOrgUnitsInHierarchy();
        NSMutableArray orgUnitQualfiers = new NSMutableArray();
        Enumeration orgUnitEnumeration = orgUnitsInHierarchy.objectEnumerator();
        while (orgUnitEnumeration.hasMoreElements())
        {
            OrgUnit thisOrgUnit = (OrgUnit)orgUnitEnumeration.nextElement();
            orgUnitQualfiers.addObject(new EOKeyValueQualifier("owningOrgUnit", EOQualifier.QualifierOperatorEqual, thisOrgUnit));
        }

        EOFetchSpecification fetchSpec = new EOFetchSpecification("SectionStyle",
                                                                  new EOOrQualifier(orgUnitQualfiers),
                                                                  styleSortOrdering());

        return anOrgUnit.editingContext().objectsWithFetchSpecification(fetchSpec);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the list of all styles that can be used on aWebsite.  This is all the styles that Website's parentOrgUnit() and any Org Units above that unit. The list of styles is sorted according to styleSortOrdering().
     *
     * @return the list of all styles that anAdmin can configure.
     */
    static public NSArray stylesAdvailableForWebsite(Website aWebsite)
    {
        /** require [a_website_not_null] aWebsite != null; **/
        
        NSMutableArray orgUnitQualfiers = new NSMutableArray();
        OrgUnit currentUnit = aWebsite.parentOrgUnit();

        // Work up from aWebsite.parentOrgUnit() to the root unit.
        while (currentUnit != null)
        {
            orgUnitQualfiers.addObject(new EOKeyValueQualifier("owningOrgUnit", EOQualifier.QualifierOperatorEqual, currentUnit));
            currentUnit = currentUnit.parentOrgUnit();
        }

        EOOrQualifier orgUnitQualifier = new EOOrQualifier(orgUnitQualfiers);
        NSMutableArray publishedStylesInOrgUnitQualfiers = new NSMutableArray(orgUnitQualifier);
        publishedStylesInOrgUnitQualfiers.addObject(new EOKeyValueQualifier("published", EOQualifier.QualifierOperatorEqual, "Y"));

        EOFetchSpecification fetchSpec = new EOFetchSpecification("SectionStyle",
                                                                  new EOAndQualifier(publishedStylesInOrgUnitQualfiers),
                                                                  styleSortOrdering());

        return aWebsite.editingContext().objectsWithFetchSpecification(fetchSpec);

        /** ensure [result_not_null] Result != null; **/

    }



    /**
     * Returns the default SectionStyle to use for newly created sites.
     *
     * @return the default SectionStyle to use for newly created sites.
     */
    static public SectionStyle defaultSectionStyle(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        
        EOQualifier qualifier = new EOKeyValueQualifier("defaultStyle", EOQualifier.QualifierOperatorEqual, "Y");
        EOFetchSpecification fetchSpec = new EOFetchSpecification("SectionStyle", qualifier, null);
        NSArray matchingStyles = ec.objectsWithFetchSpecification(fetchSpec);

        if (matchingStyles.count() > 1)
        {
            throw new RuntimeException("Multiple styles found marked as default: " + matchingStyles.valueForKey("name"));
        }

        return (SectionStyle)matchingStyles.lastObject();

        /** ensure [result_not_null] Result != null; **/
    }

    

    /**
    * Returns the SectionStyle with the indicated name.  Null is returned if there is no SectionStyle with this name.
     *
     * @return the SectionStyle with the indicated name.
     */
    static public SectionStyle sectionStyleWithName(EOEditingContext ec, String aName)
    {
        /** require
        [ec_not_null] ec != null;
        [a_name_not_null] aName != null; **/
        
        EOQualifier qualifier = new EOKeyValueQualifier("name",
                                                        EOQualifier.QualifierOperatorCaseInsensitiveLike,
                                                        aName.trim());
        EOFetchSpecification fetchSpec = new EOFetchSpecification("SectionStyle", qualifier, null);
        NSArray matchingStyles = ec.objectsWithFetchSpecification(fetchSpec);

        if (matchingStyles.count() > 1)
        {
            throw new RuntimeException("Multiple styles found with name: " + aName);
        }

        return (SectionStyle) ((matchingStyles.count() == 1) ? matchingStyles.lastObject() : null);
    }



    /**
     * Returns the SectionStyle with the indicated styleID.  Null is returned if there is no SectionStyle with this styleID.
     *
     * @return the SectionStyle with the indicated details.
     */
    static public SectionStyle sectionStyleWithStyleID(EOEditingContext ec, String theStyleID)
    {
        /** require
        [ec_not_null] ec != null;
        [the_style_id_not_null] theStyleID != null; **/
        
        EOQualifier qualifier = new EOKeyValueQualifier("styleID",
                                                        EOQualifier.QualifierOperatorCaseInsensitiveLike,
                                                        theStyleID.trim());
        EOFetchSpecification fetchSpec = new EOFetchSpecification("SectionStyle", qualifier, null);
        NSArray matchingStyles = ec.objectsWithFetchSpecification(fetchSpec);

        if (matchingStyles.count() > 1)
        {
            throw new RuntimeException("Multiple styles found with styleID: " + theStyleID);
        }

        return (SectionStyle) ((matchingStyles.count() == 1) ? matchingStyles.lastObject() : null);
    }



    /**
     * Sets default values for isDefaultStyle (no) and the display component names.
     */
    public void awakeFromInsertion( EOEditingContext ec )
    {
        super.awakeFromInsertion( ec );

        // Eventually these will be chosen from a list...
        setPageScaffoldComponent("PageScaffold");
        setBannerComponent("WOString");
        setFooterComponent("FooterSub");
        setNavBarComponent("NavBar");
        setSectionContentsComponent("SectionContents");
        
        setIsDefaultStyle(false);
    }



    /**
     * Makes this SectionStyle the default style for new Websites.  The current default Style, if any, is removed from being the default.  This SectionStyle is published if needed.
     */
    public void makeDefaultStyle()
    {
        /** require
        [not_default_style] ! isDefaultStyle();
        [is_owned_by_root_unit] owningOrgUnit().isRootUnit(); **/
        
        SectionStyle currentDefault = defaultSectionStyle(editingContext());

        if (currentDefault != null)
        {
            currentDefault.setIsDefaultStyle(false);
        }

        setIsDefaultStyle(true);
        setIsPublished(true);

        /** ensure [is_default_style] isDefaultStyle(); **/
    }



    /**
     * Returns the list of all Websites which are using this SectionStyle.
     *
     * @return the list of all Websites which are using this SectionStyle.
     */
    public NSArray websitesUsingStyle()
    {
        // sort by banner text
        return EOUtilities.objectsMatchingKeyAndValue(editingContext(), "Website", "sectionStyle", this);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns <code>true</code> if aUser can change the publication status of this StyleTemplate
     *
     * @return <code>true</code> if aUser can change the publication status of this StyleTemplate
     */
    public boolean canChangePublicationStatus(User aUser)
    {
        /** require [a_user_not_null] aUser != null; **/
        
        return aUser.canAdminStyles() && ! isDefaultStyle();
    }

    

    /**
     * Returns <code>true</code> if this SectionStyle can become the default style.
     *
     * @return <code>true</code> if this SectionStyle can become the default style.
     */
    public boolean canBecomeDefaultStyle()
    {
        return owningOrgUnit().isRootUnit() && ! isDefaultStyle();
    }



    /**
     * Funky non-EOF validation method used by GVSiteMaker
     */
    public NSMutableDictionary validate()
    {
        NSMutableDictionary errorMessages = new NSMutableDictionary();

        errorMessages.addEntriesFromDictionary(super.validate());

        if (resourceDirectory() == null)
        {
                errorMessages.setObjectForKey(StyleResourceDirectoryIsRequiredErrorMessage,
                                              StyleResourceDirectoryIsRequiredKey);
        }
        else if ( ! SMFileUtils.stringContainsOnlyValidPathChars(resourceDirectory()))
        {
            errorMessages.setObjectForKey(StyleResourceDirectoryIsInvalidErrorMessage,
                                          StyleResourceDirectoryIsInvalidKey);
        }



        // Validate that the name is unique as this is what the user sees.  Actually, this only needs to be uniqe within a section of the Org Unit hierarchy, but that is way too hard to deal with now given that styles can be moved between Org Units.  This is done here rather than on the super class as the name must only be unique for each type of style.
        if (name() != null) // null name handled by superclass
        {
            if ( editingContext().registeredObjects().containsObject(this) )
            {
                // we are validating an object already in the ec
                SectionStyle existingStyleWithName = sectionStyleWithName(editingContext(), name() );
                if ( (existingStyleWithName != null) && ! existingStyleWithName.equals(this) )
                {
                    errorMessages.setObjectForKey(StyleNameIsInUseErrorMessage,
                                                  StyleNameIsInUseKey);
                }
            }
            else
            {
                // we are validating a new object (it's not in the ec)
                if ( sectionStyleWithName(editingContext(), name() ) != null )
                {
                    errorMessages.setObjectForKey(StyleNameIsInUseErrorMessage,
                                                  StyleNameIsInUseKey);
                }
            }
        }

        // Validate that styleID is unique.  This is done here rather than on the super class as the name must only be unique for each type of style.
        if (styleID() != null) // null details handled by superclass
        {
            if ( editingContext().registeredObjects().containsObject(this) )
            {
                // we are validating an object already in the ec
                SectionStyle existingStyleWithID = sectionStyleWithStyleID(editingContext(), styleID() );
                if ( (existingStyleWithID != null) && ! existingStyleWithID.equals(this) )
                {
                    errorMessages.setObjectForKey(StyleIDIsInUseErrorMessage,
                                                  StyleIDIsInUseKey);
                }
            }
            else
            {
                // we are validating a new object (it's not in the ec)
                if ( sectionStyleWithStyleID(editingContext(), styleID() ) != null )
                {
                    errorMessages.setObjectForKey(StyleIDIsInUseErrorMessage,
                                                  StyleIDIsInUseKey);
                }
            }
        }
        
        return errorMessages;
    }



    /**
     * Overridden to use the results of validate().
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();
        
        try
        {
            super.validateForSave();
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        /*
        if ()
        {
            exceptions.addObject();
        }
         */
        
        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }



    /**
     * Returns <code>true</code> if this SectionStyle is the default one to use for new Websites / Sections.
     *
     * @return <code>true</code> if this SectionStyle is the default one to use for new Websites / Sections.
     */
    public boolean isDefaultStyle()
    {
        return defaultStyle().equals("Y");
    }



    /**
     * Controls if this SectionStyle is the default one to use for new Websites / Sections.
     *
     * @param value - <code>true</code> if this SectionStyle is the default one to use for new Websites / Sections.
     */
    public void setIsDefaultStyle( boolean value )
    {
        setDefaultStyle( value ? "Y" : "N");
    }
    
    
    
    /**
     * Returns the url for the CSS related to this SectionStyle.  If relatedCSSUrl is not specified, "<resource directory><styleId>.css" is returned.
     *
     * @param the url for the CSS related to this SectionStyle 
     */
    public String cssURL()
    {
        /** require [style_id_not_null] styleID() != null; **/   
    	
        String cssURL = relatedCSSUrl();
        
        if (relatedCSSUrl() == null)
        {
            cssURL = NSPathUtilities.stringByAppendingPathComponent(resourceDirectory(), styleID() + ".css");
        }
        
    		return cssURL;
    		
    		/** ensure [result_not_null] Result != null; **/    		
    }
}
