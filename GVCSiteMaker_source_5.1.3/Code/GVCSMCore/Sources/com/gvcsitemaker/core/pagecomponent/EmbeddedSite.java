/**
 * Implementation of EmbeddedSite common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 */  
package com.gvcsitemaker.core.pagecomponent;


import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


public class EmbeddedSite extends _EmbeddedSite
{

    /**
     * Factory method to create new instances of EmbeddedSite.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of EmbeddedSite or a subclass.
     */
    public static EmbeddedSite newEmbeddedSite()
    {
        return (EmbeddedSite) SMEOUtils.newInstanceOf("EmbeddedSite");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("EmbeddedSite");
    }



    /**
    * Returns a copy of this EmbeddedSite with the embedded website copied by reference.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this EmbeddedSite
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        EOEnterpriseObject copy = super.duplicate(copiedObjects);

        copy.addObjectToBothSidesOfRelationshipWithKey(linkedWebsite(), "linkedWebsite");

        return copy;

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Makes the connection from EmbeddedSite to a Website.  Ensures that the reciprocal relationship is set so that the delete rules work.
     *
     * @param aWebsite - the Website to link to.
     */
   public void linkToWebsite(Website aWebsite)
    {
        /** require [valid_param] aWebsite != null;  [same_ec] aWebsite.editingContext() == editingContext();  **/
        addObjectToBothSidesOfRelationshipWithKey(aWebsite, "linkedWebsite");
        /** ensure [link_made] linkedWebsite() == aWebsite;  **/
    }



    /**
     * Returns <code>true</code> if the embedded Website has been deleted.  Being embedded in another section does not prevent a Website from being deleted.  EmbeddedSite must handle this common situation.
     *
     * @return <code>true</code> if the embedded Website has been deleted.
     */
    public boolean hasTargetBeenDeleted()
    {
        return linkedWebsite() == null;
    }



    /**
     * Returns the home Section of the embedded Website or null if the Website has been deleted.
     *
     * @return the Website which contains the embedded Section.
     */
    public Section linkedSection()
    {
        return hasTargetBeenDeleted() ? null : linkedWebsite().homeSection();

        /** ensure
        [result_valid] ((hasTargetBeenDeleted() && (Result == null)) ||
                        ( Result.equals(linkedWebsite().homeSection())) );
         **/
    }



    /**
     * Returns the section or sections embedded by this component.  Returns  NSArray.EmptyArray if the Website has been deleted.
     *
     * @return the 
     */
    public NSArray embeddedSections()
    {
        // always skip home sections because the embeded site section points to the embeded site's home section.
        return hasTargetBeenDeleted() ? NSArray.EmptyArray : linkedWebsite().nonHomeSections();
        
        /** ensure
            [result_valid] ((hasTargetBeenDeleted() && (Result.equals(NSArray.EmptyArray))) ||
                        ( Result.equals(linkedWebsite().nonHomeSections())) );
         **/
    }


    
    //************** Binding Cover Methods **************\\


}

