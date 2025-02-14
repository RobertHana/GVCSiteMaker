package net.global_village.eofextensions;

import java.lang.reflect.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * <code>GVCFacade</code> is the base class for the EO Facades.  A facade can be defined as a showy misrepresentation intended to conceal something unpleasant.  And that is what GVCFacade is used for, it provides a consistant interface to it's <code>EOEnterpriseObject</code> controlling how the object is displayed and how it is modified.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public abstract class GVCFacade extends Object
{
    protected EOEditingContext editingContext;
    protected EOEnterpriseObject focusObject;


    /**
     * Designated constructor.  Creates a new GVCFacade with the passed editing context and facade object.  It makes sure that the facade object is in the passed editing context.
     *
     * @param ec the editing context to be used by the facade
     * @param aFocusObject the main object to be managed by the facade
     */
    public GVCFacade(EOEditingContext ec, EOEnterpriseObject aFocusObject)
    {
        super();
        /** require [valid_ec_param] ec != null; **/

        editingContext = ec;
        if (aFocusObject == null)
        {
            createNew();  // facades must have a facadeObject so create one if none is passes in
        }
        else
        {
            focusObject = EOUtilities.localInstanceOfObject(editingContext(), aFocusObject);
        }

        /** ensure [has_focus_object] hasFocusObject(); **/
    }



    /**
     * Constructor that creates a GVCFacade with a new editing context and no enterprise object.
     * 
     * @deprecated Use a constructor that takes an editing context parameter.
     */
    public GVCFacade()
    {
        // TODO Don't do this!  Pass a session managed EC that is locked for the facade
        this(new EOEditingContext(), null);

        /** ensure [has_focus_object] hasFocusObject(); **/
    }



    /**
     * Constructor that creates a GVCFacade using the passed editingContext with no enterprise object.
     *
     * @param ec the editing context to be used by the facade
     */
    public GVCFacade(EOEditingContext ec)
    {
        this(ec, null);
        /** require [valid_ec_param] ec != null; **/

        /** ensure [has_focus_object] hasFocusObject(); **/
    }



    /**
     * Constructor that creates a GVCFacade with its own editing context and with the passed enterprise object. It makes sure that the enterprise object is in the editing context.
     *
     * @param aFocusObject the main object to be managed by the facade
     * @deprecated Use a constructor that takes an editing context parameter.
     */
    public GVCFacade(EOEnterpriseObject aFocusObject)
    {
        // TODO Don't do this!  Pass a session managed EC that is locked for the facade
        this(new EOEditingContext(), aFocusObject);
        /** require [valid_aFocusObject_param] aFocusObject != null; **/

        /** ensure [has_focus_object] hasFocusObject(); **/
    }



    /**
     * Returns the facades for the passed EO objects.  The facades created will have the same editing context as the passed editingContext.
     *
     * @param ec the editingContext to be used when creating the facades
     * @param eoObjects the list of objects that will be used to create the facades
     * @return the facades for the passed EO objects
     */
    public static NSArray facadesForObjects(Class facadeClass, EOEditingContext ec, NSArray eoObjects)
    {
        /** require
        [valid_facadeClass_param] facadeClass != null;
        [valid_ec_param] ec != null;
        [valid_eoObjects_param] eoObjects != null; **/

        NSMutableArray facadesForObjects = new NSMutableArray();
        Constructor facadeConstructor;

        if (eoObjects.count() > 0)
        {
            Class eoObjectClass = eoObjects.lastObject().getClass();
            try
            {
                facadeConstructor = facadeClass.getConstructor(new Class[] {EOEditingContext.class, eoObjectClass} );
                java.util.Enumeration enumerator = eoObjects.objectEnumerator();
                while (enumerator.hasMoreElements())
                {
                    Object anEOObject = enumerator.nextElement();
                    Object newFacade =  facadeConstructor.newInstance(new Object[] {ec, anEOObject});
                    facadesForObjects.addObject(newFacade);
                }
            }
            catch (Exception e)
            {
                throw new ExceptionConverter(e);
            }
        }

        JassAdditions.post("GVCFacade", "facadesForObjects", facadesForObjects.count() == eoObjects.count());
        return facadesForObjects;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the editing context being used by this facade
     *
     * @return editing context being used by this facade
     */
    public EOEditingContext editingContext()
    {
        return editingContext;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if facade has a <code>focusObject</code>, <code>false</code> otherwise
     *
     * @return <code>true</code> if facade has a <code>focusObject</code>, <code>false</code> otherwise
     */
    public boolean hasFocusObject()
    {
        return focusObject() != null;
    }



    /**
     * Returns the main enterprise object managed by this facade.
     *
     * @return the main enterprise object managed by this facade.
     */
    public EOEnterpriseObject focusObject()
    {
        return focusObject;
    }



    /**
     * Sets the enterprise object with the passed <code>objectForFacade</code>.  It makes sure that the passed object is in the facade's editing context.
     *
     * @param objectForFacade the object to be managed by the facade
     */
    public void setFocusObject(EOEnterpriseObject objectForFacade)
    {
        /** require [valid_param] objectForFacade != null; **/

        focusObject = EOUtilities.localInstanceOfObject(editingContext(), objectForFacade);                

        /** ensure [has_focus_object] hasFocusObject(); **/
    }



    /**
     * Returns the facades for the passed EO objects.  The facades created will have the same editing context as this facade.
     *
     * @param eoObjects the list of objects that will be used to create the facades
     * @return the facades for the passed EO objects
     */
    public NSArray facadesForObjects(NSArray eoObjects)
    {
        /** require [valid_param] eoObjects != null; **/

        NSArray facadesForObjects = facadesForObjects(this.getClass(), editingContext(), eoObjects);

        JassAdditions.post("GVCFacade", "facadesForObjects", facadesForObjects.count() == eoObjects.count());
        return facadesForObjects;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if the <code>focusObject</code> of this facade is equal to the <code>focusObject</code> of the <code>otherFacade</code>
     *
     * @param otherFacade the other facade to compare with
     * @return <code>true</code> if the <code>focusObject</code> of this facade is equal to the <code>focusObject</code> of the <code>otherFacade</code>
     */
    public boolean equals(Object otherFacade)
    {
        // can not have a precondition forbidding null values as takeValue:forKeyPath: calls it during page drawing
        boolean result = false;

        if (otherFacade != null)
        {
            // can not call this on null object which would result in a NullPointer Exception
            result = EOObject.globalID(focusObject()).equals(EOObject.globalID(((GVCFacade)otherFacade).focusObject()));
        }
        
        return result;    
    }



    /**
     * Create a new facade object for this facade.
     */
    public final void createNew()
    {
        EOEnterpriseObject newObject = newInstance();
        editingContext().insertObject(newObject);
        setFocusObject(newObject);

        /** ensure [has_focus_object] hasFocusObject(); **/
    }



    /**
     * Returns a new instance of an EOEnterpriseObject to be inserted in this facades editing context.  This method is called by createNew() method.
     *
     * @return a new instance of an EOEnterpriseObject to be inserted in this facades editing context
     *
     * Jass can't handle contracts on abstract methods. Add this to your subclass: Result != null
     */
    public abstract EOEnterpriseObject newInstance();



    /**
     * Returns a new instance of an GVCFacade containing the focusObject of this facade but with a new editing context.  Subclasses must override to copy any instance variables as well.  We currently do not allow copying of facades with newly inserted focus objects though we may at a later date.
     *
     * @return a new instance of an GVCFacade containing the focusObject of this facade but with a new editing context
     */
    public GVCFacade copy()
    {
        /** require [not_newly_inserted_focus_object] ! isNewlyInsertedFocusObject(); **/

        GVCFacade copy;

        try
        {
            Constructor facadeConstructor = this.getClass().getConstructor(new Class[] { focusObject().getClass() });
            copy = (GVCFacade) facadeConstructor.newInstance(new Object[] { focusObject() });
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }

        return copy;

        /** ensure [valid_result] Result != null; [is_copy] Result.equals(this); **/
    }



    /**
     * Returns true if focusObject has just been created.
     *
     * @return true if focusObject has just been created
     */
    public boolean isNewlyInsertedFocusObject()
    {
        /** require [has_focus_object] hasFocusObject(); **/

        return EOObject.globalID(focusObject()).isTemporary();
    }



    /**
     * Returns <code>true</code> if the facade's editing context has changes.
     *
     * @return <code>true</code> if the facade's editing context has changes
     */
    public boolean hasChanges()
    {
        return editingContext().hasChanges();
    }



    /**
     * Returns <code>true</code> if the focusObject has been sucessfully saved.
     *
     * @return <code>true</code> if the focusObject has been sucessfully saved
     */
    public boolean hasBeenSaved()
    {
        return ( ! isNewlyInsertedFocusObject()) && ( ! hasChanges());
    }



    /**
     * Revert changes to the facade object.  For newly inserted focus objects it creates an entirely new instance of the focuse object.
     */
    public void revert()
    {
        if (isNewlyInsertedFocusObject())
        {
            editingContext().revert();
            createNew();
        }
        else
        {
            editingContext().revert();
        }

        /** ensure [changes_reverted] (! hasChanges()) || isNewlyInsertedFocusObject(); **/
    }



    /** invariant [has_editing_context] editingContext != null; **/



}
