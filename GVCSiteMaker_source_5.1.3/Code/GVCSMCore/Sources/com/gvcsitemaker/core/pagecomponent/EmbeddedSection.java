/**
 * Implementation of EmbeddedSection common to all installations.
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


public class EmbeddedSection extends _EmbeddedSection
{

    /**
     * Factory method to create new instances of EmbeddedSection.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of EmbeddedSection or a subclass.
     */
    public static EmbeddedSection newEmbeddedSection()
    {
        return (EmbeddedSection) SMEOUtils.newInstanceOf("EmbeddedSection");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("EmbeddedSection");
    }



    /**
    * Returns a copy of this EmbeddedSection with the embedded section copied by reference.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this EmbeddedSection
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        EOEnterpriseObject copy = super.duplicate(copiedObjects);

        copy.addObjectToBothSidesOfRelationshipWithKey(linkedSection(), "linkedSection");

        return copy;

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Makes the connection from EmbeddedSection to a Section.  Ensures that the reciprocal relationship is set so that the delete rules work.
     *
     * @param aSection - the Section to link to.
     */
    public void linkToSection(Section aSection)
    {
        /** require [valid_param] aSection != null;  [same_ec] aSection.editingContext() == editingContext();  **/
        addObjectToBothSidesOfRelationshipWithKey(aSection, "linkedSection");
        /** ensure [link_made] linkedSection() == aSection;  **/
    }



    /**
     * Returns <code>true</code> if the embedded Section has been deleted.  Being embedded in another section does not prevent a Section from being deleted.  EmbeddedSection must handle this common situation.
     *
     * @return <code>true</code> if the embedded Section has been deleted.
     */
    public boolean hasTargetBeenDeleted()
    {
        return linkedSection() == null;
    }



    /**
     * Returns the embedded Website or null if the Website has been deleted.
     *
     * @return the embedded Website or null if the Website has been deleted.
     */
    public Website linkedWebsite()
    {
        return hasTargetBeenDeleted() ? null : linkedSection().website();
        /** ensure
        [target_deleted_or_returns_website] ((hasTargetBeenDeleted() && (Result == null)) ||
                                             ((Result != null) && Result.equals(linkedSection().website())) );
        **/
    }



     //************** Binding Cover Methods **************\\




}

