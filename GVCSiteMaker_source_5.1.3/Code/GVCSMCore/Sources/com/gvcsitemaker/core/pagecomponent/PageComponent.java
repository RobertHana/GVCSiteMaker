// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.pagecomponent;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;

/**
 * PageComponent and sub-classes are the building blocks from which SiteMaker pages are created.  Each class has an associated UI class of the same name, but in a different package.  These can be found in the ComponentPrimitives sub-project of SiteMaker.  The componentType attribute of a PageComponent determines its actual class when instantiated.  PageComponents can be created either manually or from the methds in SectionFactory.  Using SectionFactory is easier and is the preferred method for layouts and where specfic values are needed in the bindings.
 * <br><br>
 * PageComponent is in effect an abstract class but is not indicated as such due to EOF constraints.  It should not be instantiated directly, there is no support for this in the UI.  PageComponent should be sub-classed to create concrete, usable UI building blocks.
 * <br><br>
 * PageComponent fully implements its API and provides services for binding access, constant definition, and copying.  It defines standard bindings and provides convenience methods.
 * <br><br>
 * <b>Note</b>: The toChildren relationship on a PageComponent is currently only used by Layout and its sub-classes.
 */
public class PageComponent extends _PageComponent 
{

    // Binding keys
    public static final String CAN_EDIT_BINDINGKEY = "canEdit";
    
    // Binding values
    public static final String EXTERNAL_TYPE = "external";
    public static final String SITEFILE_TYPE = "file";
    public static final String URL_TYPE = "url";
    public static final String TRUE = "yes";
    public static final String FALSE = "no";
    public static final String ALIGN_LEFT = "left";
    public static final String ALIGN_CENTER = "center";
    public static final String ALIGN_RIGHT = "right";

    protected NSMutableDictionary cachedBindingDictionary = null;
    
    /** 
     * Context for EOCopying that indicates only the pageComponent (rather than the entire site) is being copied.
     * @see duplicate(NSMutableDictionary)
     */
    public static final String COMPONENT_ONLY_COPY_CONTEXT = "CopyComponentOnly";
    
    
    
    /**
     * @param context copying context passed to duplicate
     * @return <code>true</code> if the passed context indicates that this is a Component only copy
     */
    public static boolean isComponentOnlyCopy(NSDictionary context)
    {
        /** require [context_not_null] context != null; **/
        return (context.objectForKey(EOCopying.COPY_CONTEXT) != null) && context.objectForKey(EOCopying.COPY_CONTEXT).equals(PageComponent.COMPONENT_ONLY_COPY_CONTEXT);
    }    
    

    /**
     * Factory method to create new instances of PageComponent.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of PageComponent or a subclass.
     */
    public static PageComponent newPageComponent()
    {
        return (PageComponent) SMEOUtils.newInstanceOf("PageComponent");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to ensure that the bindings attribute has a value (empty dictionary) if one is not already set.  Also sets default value for canEdit to true.<br>
     * This is used by SectionFactory to set the default values for the PageComponents implementing a Section.  At present each type of Page Component is only used for one Section type.  If one needs to be shared and have different default binding then a sub-class will need to be created with only awakeFromInsertion overridden.
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        
        if (bindings() == null)
        {
            setBindings(NSDictionary.EmptyDictionary);
        }

        if ( ! hasValueForBindingKey(CAN_EDIT_BINDINGKEY))
        {
            setCanEdit(true);
        }

        setDateModified(Date.now());

        /** ensure [bindings_not_null] bindings() != null; **/
    }



 
    /**
     * Clears all cached values.
     */
    public void clearCachedValues()
    {
        cachedBindingDictionary = null;
    }
    

    
    /**
     * Returns a copy of an PageComponent sub-class.  The copy is made manually as some relationships (e.g. toParent) need to be copied before other relationships (e.g. toChildren) so that DBC and other code functions correctly.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this PageComponent sub-class
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        EOEnterpriseObject copy = EOCopying.Utility.newInstance(this);

        // Handle circular relationships by registering this object right away.
        EOGlobalID globalID = editingContext().globalIDForObject(this);
        copiedObjects.setObjectForKey(copy, globalID);

        EOCopying.Utility.copyAttributes(this, copy);

        // copyAttributes uses takeStoredValueForKey which does not call setBindings(String) etc. so we need to manually clear any cached values
        ((PageComponent)copy).clearCachedValues();

        EOEntity entity = ((EOEntityClassDescription)classDescription()).entity();

        // Copy relationships in an order which prevents null pointer and other errors.
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("toParent"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("toChildren"));
        
        if (Section.isSectionOnlyCopy(copiedObjects) || (PageComponent.isComponentOnlyCopy(copiedObjects)))
        {
            ((PageComponent)copy).addObjectToBothSidesOfRelationshipWithKey(toRelatedFile(), "toRelatedFile");
        }
        else
        {
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("toRelatedFile"));
        }
        
        if (PageComponent.isComponentOnlyCopy(copiedObjects))
        {
            ((PageComponent)copy).addObjectToBothSidesOfRelationshipWithKey(section(), "sections");
        }
        else
        {
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("sections"));
        }

        return copy;

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Generates the primary key for this page compoent.
     */
    protected void generatePrimaryKey()
    {
        /** require [pk_not_set]  componentId() == null;  **/

        SMEOUtils.setPrimaryKeyForNewObject(this);

        /** ensure [pk_set]  componentId() != null;  **/
    }



    /**
     * Returns bindings(), which are a String, as a dictionary.  Returns an empty dictionary if bindings() is null.  See setBindings(NSDictionary) for the inverse method.
     *
     * @return bindings(), which are a String, as a dictionary.
     */
    public NSMutableDictionary bindingDictionary()
    {
        // Memory and performance optimization.  Need to refresh if this object is
        // a fault as it may have been refaulted.  If this is the first method called
        // awakeFromFetch() will not have been called yet
        if (isFault() || (cachedBindingDictionary == null))
        {
            NSDictionary deserializedDictionary = (NSDictionary)NSPropertyListSerialization.propertyListFromString(bindings());

            if (deserializedDictionary == null)
            {
                cachedBindingDictionary = new NSMutableDictionary();
            }
            else
            {
                cachedBindingDictionary = new NSMutableDictionary(deserializedDictionary);
                
            }
        }

        return cachedBindingDictionary;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Point of single choice for converting a component's PK to a string.
     * Can be an integer or an NSData (byte(24)).
     *
     * @return the component ID as a string
     */
    public String componentIdAsString()
    {
        if ( ! SMApplication.smApplication().isUsingIntegerPKs())
        {
            Object downcaster = componentId();
            return ERXStringUtilities.byteArrayToHexString(((NSData)downcaster).bytes());
        }
        return componentId().toString();
    }



    /**
     * Overriden to clear cached values.
     */
    public void setBindings(String aValue)
    {
        // Just clear this specific cached value instead of calling clearCachedValues() so that cached values in sub-classes are not reset unneccessarily
        cachedBindingDictionary = null;
        super.setBindings(aValue);
    }


    
    /**
     * Sets bindings(), which are a String, from the contents of a dictionary.  This is an overloaded version of setBindings(String).  See bindingDictionary() for the inverse method.
     *
     * @param aDictionary - the dictionary whose contents are to be stored as bindings().
     */
    protected void setBindings(NSDictionary aDictionary)
    {
        /** require [a_dictionary_not_null] aDictionary != null; **/

        setBindings(NSPropertyListSerialization.stringFromPropertyList(aDictionary));

        /** ensure [successfully_set_bindings] bindingDictionary().equals(aDictionary); **/
    }



    /**
     * Returns the value for the named binding.  If the name is not bound to a value, or if it is bound to an empty string, null is returned.
     *
     * @param bindingKey - the name of a binding to return a value for.
     * @return the value for the named binding or null if there is no value
     */
    public Object valueForBindingKey(String bindingKey)
    {
        /** require [binding_key_not_null] bindingKey != null; **/
        
        Object valueForBindingKey = null;

        valueForBindingKey = bindingDictionary().objectForKey(bindingKey);

        // Treat empty strings an null
        if ((valueForBindingKey != null) &&
            (valueForBindingKey instanceof String) &&
            (((String)valueForBindingKey).length() == 0))
        {
            valueForBindingKey = null;
        }

        return valueForBindingKey;
    }



    /**
     * Returns the value for the named binding, or theDefault if hasValueForBindingKey(aKey) returns false.
     *
     * @param bindingKey - the name of a binding to return a value for.
     * @return the value for the named binding or null if there is no value
     */
    public Object valueForBindingKeyWithDefault(String bindingKey, Object theDefault)
    {
        /** require [binding_key_not_null] bindingKey != null; **/
        
        Object valueForBindingKey = theDefault;

        if (hasValueForBindingKey(bindingKey))
        {
            valueForBindingKey = valueForBindingKey(bindingKey);
        }

        return valueForBindingKey;

        /** ensure [has_value_or_returns_default] ((hasValueForBindingKey(bindingKey) && (Result.toString().equalsIgnoreCase(valueForBindingKey(bindingKey).toString()))) || (( ! hasValueForBindingKey(bindingKey)) && (Result == theDefault))); **/
    }



    /**
     * Returns <code>true</code> if valueForBindingKey(aKey) would return a non null value.
     *
     * @param bindingKey - the name of a binding to return a value for.
     * @return <code>true</code> if valueForBindingKey(aKey) would return a non null value.
     */
    public boolean hasValueForBindingKey(String bindingKey)
    {
        /** require [binding_key_not_null] bindingKey != null; **/
        
        return valueForBindingKey(bindingKey) != null;

        /** ensure [result_correct] (Result == (valueForBindingKey(bindingKey) != null)); **/
    }



    /**
     * If <code>bindingValue</code> is not null, sets the binding named <code>bindingKey</code> to <code>bindingValue</code> replacing any previously set value.  If <code>bindingValue</code> is null, removes the binding named <code>bindingKey</code> from bindings().
     *
     * @param bindingValue - the value to set for the binding named <code>bindingKey</code>, or null if that binding is to be removed.
     * @param bindingKey - the name of the binding to be set, updated, or removed.
     */
    public void setBindingForKey(Object bindingValue, String bindingKey)
    {
        /** require [binding_key_not_null] bindingKey != null; **/
        
        if (bindingKey != null)
        {
            // Parse the bindings into a Dictionary
            //DebugOut.println(22,"********** bindings are: "  + bindings());
            DebugOut.println(22,"Setting binding: "  + bindingValue + " for key: "  + bindingKey);

            NSMutableDictionary theBindings = bindingDictionary();

            if (bindingValue != null)
            {
                theBindings.setObjectForKey(bindingValue, bindingKey);
            }
            else
            {
                theBindings.removeObjectForKey(bindingKey);
            }
            setBindings(theBindings);
        }

        /** ensure [correct_action_performed] (((bindingValue == null) && ! hasValueForBindingKey(bindingKey)) || (valueForBindingKey(bindingKey).equals(bindingValue))); **/
    }



    /**
     * Returns <code>true</code> if 'bindingKey' is bound to the value TRUE.
     *
     * @param bindingKey - the key to return a boolean value from the bindings for.
     * @return <code>true</code> if 'bindingKey' is bound to the value TRUE.
     */
    public boolean booleanValueForBindingKey(String bindingKey)
    {
        /** require [has_bindingKey] bindingKey != null;  [has_binding] hasValueForBindingKey(bindingKey); **/
        
        String bindingValue = (String) valueForBindingKey(bindingKey);
        return bindingValue.equalsIgnoreCase(TRUE);
    }



    /**
     * Sets the binding for 'bindingKey' to the string representation of booleanValue.
     *
     * @param booleanValue - the boolean value to set for bindingKey
     * @param bindingKey - the key to set a boolean value in the bindings for.
     */
    public void setBooleanValueForBindingKey(boolean booleanValue, String bindingKey)
    {
        /** require [has_bindingKey] bindingKey != null; **/
        setBindingForKey(booleanValue ? TRUE : FALSE, bindingKey);
        /** ensure [has_binding] hasValueForBindingKey(bindingKey); **/
    }



    /**
     * Cover method for toRelatedFile relationship to add or remove a SiteFile from this relationship.  Calls addObjectToBothSidesOfRelationshipWithKey if aSiteFile is not null and, if it is null, calls removeObjectFromBothSidesOfRelationshipWithKey.  This allows this method to be bound directly to a WOBrowser or WOPopup in which it is possible to not make a selection (e.g. to select nothing).
     *
     * @param aSiteFile - the SiteFile to use for the toRelatedFile relationship, or null if the relationship is to be removed.
     */
    public void setRelatedFile(SiteFile aSiteFile)
    {
        if (aSiteFile != null)
        {
            addObjectToBothSidesOfRelationshipWithKey(aSiteFile, "toRelatedFile");
        }
        else
        {
            removeObjectFromBothSidesOfRelationshipWithKey(toRelatedFile(), "toRelatedFile");
        }

        /** ensure [to_related_file_equals_a_site_file] toRelatedFile() == aSiteFile; **/
    }



    /**
     * Cover method equivalent to toRelatedFile().  This is here to balance out setRelatedFile(SiteFile) for WOComponent binding synchronization.
     *
     * @return toRelatedFile()
     */
    public SiteFile relatedFile()
    {
        return toRelatedFile();

        /** ensure [result_equals_to_related_file] Result == toRelatedFile(); **/
    }



    /**
     * Returns the Section this PageComponent is the root component of (e.g. where aSection.component() == this), or null if this PageComponent is a child of another PageComponent.  This is a cover method for sections() which always has either 0 or 1 elements.
     *
     * @return the Section this PageComponent is the root component of (e.g. where aSection.component() == this), or null if this PageComponent is a child of another PageComponent.  This is a cover method for sections() which always has either 0 or 1 elements.
     */
    public Section section()
    {
        return (sections().count() > 0) ? (Section)sections().objectAtIndex(0) : null;

        /** ensure [valid_result] ((Result == null) || (Result == sections().objectAtIndex(0))); **/
    }


    /**
     * Returns the Section this PageComponent is part of.  It traverses the PageComponent hierarchy to find this.
     */
    public Section sectionUsedIn()
    {
        // requires it not to be an orphanded page component...
        
        PageComponent parent = this;
        Section componentsSection = null;

        // If this page component is orphaned both parent and componentsSection will be null
        while ( (parent != null) && (componentsSection == null))
        {
            componentsSection = parent.section();
            parent = parent.toParent();
        }

        // If this componentsSection is still null, check if it is a non-current version.  Non-current versions have no sections assigned, instead it is attached to the version 
        if (componentsSection == null)
        {
            parent = this;            
            while ( (parent != null) && (componentsSection == null))
            {
                if (parent.version() != null)
                {
                    componentsSection = parent.version().section();
                }
                parent = parent.toParent();
            }
        }

        return componentsSection;

        /** ensure [result_not_null] Result != null; **/
    }



    


    /**
     * Adds aComponent to the toChildren relationship and maintains the component order by placing it at the end of the list.
     *
     * @param aComponent - the PageComponent to add to the toChildren relationship
     */
    public void addChild(PageComponent aComponent)
    {
        /** require [valid_param] aComponent != null;  [component_not_added] ! toChildren().containsObject(aComponent); **/

        editingContext().insertObject(aComponent);
        addObjectToBothSidesOfRelationshipWithKey(aComponent, "toChildren");

        /** ensure [component_added] toChildren().containsObject(aComponent);  **/
    }

    

    /**
     * Utility method to validate a value as a range restricted integer and to return the validated value coerced to a String.  This is intended to be used to validate bindings.
     *
     * @param valueToValidate - the value to validate as an integer value
     * @param minimum - the minimum acceptable value
     * @param maximum - the maximum acceptable value
     * @param acceptNumm - <code>true</code> if null values are permitted.
     * @param uiName - name to use for the value being validated in error messages
     * @param attributeName - name of the attribute being validated, used in NSValidation.ValidationException
     * @return the validated valueToValidate coerced to a String
     * @exception NSValidation.ValidationException - if the valueToValidate is not valid
     */
     public String validateInteger(Object valueToValidate, int minimum, int maximum, boolean acceptNull, String uiName, String attributeName)
        throws NSValidation.ValidationException
    {
        Integer integerValue = null;
        boolean isValid = true;
        boolean isNullValue = (valueToValidate == null) ||
            ((valueToValidate instanceof String) && (((String)valueToValidate).trim().length() == 0));

        // Coerce type
        if (acceptNull && isNullValue)
        {
            // valid
        }
        else if (valueToValidate instanceof String)
        {
            try
            {
                valueToValidate = ((String)valueToValidate).trim();
                integerValue = new Integer((String)valueToValidate);
            }
            catch (NumberFormatException numberFormatException)
            {
                isValid = false;
            }
        }
        else if (valueToValidate instanceof Number)
        {
            integerValue = new Integer(((Number)valueToValidate).intValue());
        }
        else
        {
            isValid = false;
        }

        // Range check
        if (isValid && ! isNullValue)
        {
            int intValue = integerValue.intValue();

            if ((intValue < minimum) || (intValue > maximum))
            {
                isValid = false;
            }
        }

        if (! isValid)
        {
            throw new NSValidation.ValidationException(uiName + " must be a number between " + minimum + " and " + maximum,
                                                       this, attributeName);
        }

        return (isNullValue) ? null : valueToValidate.toString();
        
        /** ensure [valid_result] acceptNull || (Result != null);  **/
    }



    /**
     * Performs some extra validations, paritcularly for bindings.  As the UI has validation outside of EOF, any of these failing should indicate a programming flaw.  Also updates dateModified.
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        // TODO Move this someplace else.  It causes problems when new objects are created: after initial save changes to relationships in that EC are ignored.
        // This seems as good a place as any to set this.
        // setDateModified(Date.now());

        try
        {
            super.validateForSave();
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }

    
    
    /**
     * Returns the SectionVersion this PageComponent is attached to.  This is a cover method for versions(),  which always has either 0 or 1 elements
     *
     * @return the SectionVersion this PageComponent is attached to.  This is a cover method for versions(),  which always has either 0 or 1 elements
     */
    public SectionVersion version()
    {
        return (versions().count() > 0) ? (SectionVersion) versions().objectAtIndex(0) : null;

        /** ensure [valid_result] ((Result == null) || (Result == versions().objectAtIndex(0))); **/
    }
    

    
    /**
     * Overridden to make sure the bindings property is really changed.
     *
     * @return the changes the the passed snapshot
     */    
    public NSDictionary changesFromSnapshot(NSDictionary snapshot)
    {
    	NSDictionary changesFromSnapshot = super.changesFromSnapshot(snapshot);

    	if ((changesFromSnapshot.count() == 1) && (changesFromSnapshot.containsKey("bindings")) && (snapshot.containsKey("bindings")))
    	{
            NSDictionary deserializedChangesFromSnapshotDictionary = (NSDictionary) NSPropertyListSerialization.propertyListFromString((String) changesFromSnapshot.objectForKey("bindings"));
            NSDictionary deserializedSnapshotDictionary = (NSDictionary) NSPropertyListSerialization.propertyListFromString((String) snapshot.objectForKey("bindings"));

            if (deserializedChangesFromSnapshotDictionary.equals(deserializedSnapshotDictionary))
            {
            	changesFromSnapshot = new NSDictionary();
            }
    	}
    	
        return changesFromSnapshot;
    }



    //************** Binding Cover Methods **************\\
 
    /**
     * Returns <code>true</code> if this PageComponent can be altered (is not read only).  This is determined by the canEdit binding having a value of 'yes', or not having a value set.  At present this is only used when PageComponents are edited, there is no place in the UI change this value.  It appears to be intended to create read only versions of PageComponents though there are no examples of this.
     *
     * @return <code>true</code> if this PageComponent can be altered (is not read only).  
     */
    public boolean canEdit()
    {
        String canEditBindingValue = (String) valueForBindingKeyWithDefault(CAN_EDIT_BINDINGKEY, TRUE);

        return canEditBindingValue.equalsIgnoreCase(TRUE);
    }



    /**
     * Sets the editable status of this page component.
     *
     * @param isEditable - <code>true</code> if this PageComponent is editable, <code>false</code> if it is read only.
     */
    public void setCanEdit(boolean isEditable)
    {
        setBindingForKey(isEditable ? TRUE : FALSE, CAN_EDIT_BINDINGKEY);
    }
}
