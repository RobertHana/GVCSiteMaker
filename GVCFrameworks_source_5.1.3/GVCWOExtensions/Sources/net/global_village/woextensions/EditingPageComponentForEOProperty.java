package net.global_village.woextensions;


import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;


/**
 * EOEnterpriseObject aware subclass of EditingPageComponent for use when the <code>property</code> binding
 * is bound to an EOAttribute or EORelationship of an EOEntity. EditingPageComponent is extended with
 * EOModel aware code providing a localized caption and description. The definition of required and
 * defaults for caption and description are taken directly from the EOModel.
 */
public class EditingPageComponentForEOProperty extends EditingPageComponent
{

    public static final String PROPERTY_BINDING = "property";



    public EditingPageComponentForEOProperty(WOContext context)
    {
        super(context);
    }



    /**
     * The caption to display with the input. This implementation returns the
     * EOFValidation.DisplayName for the property if it exists. If it does not
     * exist, it returns defaultCaption().
     *
     * @return the caption to display with the input
     */
    public String caption()
    {
        // BUG Returns Entity.displayName if Entity.property.displayName is not defined
        boolean captionExists = gvcWOSession().localizedEOStringExists(entity(), property().name(), EOFValidation.DisplayName);
        return captionExists ? gvcWOSession()
                        .localizedEOString(entity(), property().name(),
                                        EOFValidation.DisplayName)
                        : defaultCaption();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * This value is used if no localized EOFValidation.DisplayName is defined
     * for property().  By default it is the displayNameForKey() from the
     * EOClassDescription.
     *
     * @return property().name()
     */
    public String defaultCaption()
    {
        return entity().classDescriptionForInstances().displayNameForKey(property().name()).toLowerCase();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Optional description that can be displayed with the input. This
     * implementation returns the EOFValidation.Description for the property if
     * it exists. If it does not exist, it returns defaultDescription().
     *
     * @return the localized description of this input for use in the UI.
     */
    public String description()
    {
        return gvcWOSession().localizedEOStringExists(entity(),
                        property().name(), EOFValidation.Description) ? gvcWOSession()
                        .localizedEOString(entity(), property().name(),
                                        EOFValidation.Description)
                        : defaultDescription();
    }



    /**
     * This value is used if no localized EOFValidation.Description is defined
     * for property().
     *
     * @return this implementation returns null
     */
    public String defaultDescription()
    {
        return null;
    }



    /**
     * @return <code>true</code> if this property is required
     */
    public boolean isRequired()
    {
        if (property() instanceof EOAttribute)
        {
            return !((EOAttribute) property()).allowsNull();
        }

        return ((EORelationship) property()).isMandatory();
    }



    /**
     * @return the EOProperty modelling the value that is bound to this
     *         component
     */
    public EOProperty property()
    {
        return (EOProperty)valueForBinding(PROPERTY_BINDING);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * @return the EOEntity that property() is part of
     */
    public EOEntity entity()
    {
        return (EOEntity) NSKeyValueCoding.DefaultImplementation.valueForKey(property(), "entity");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overridden catch Throwables other than EOFValidationExceptions and run the value through GVCEOFValidation so that
     * we get an EOFValidationException. Non-EOFValidationException are usually formatter failures.
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        // If we have a nice EOFValidationException we can just register it and be done
        if (t instanceof EOFValidationException)
        {
            super.validationFailedWithException(t, value, keyPath);
            return;
        }

        // Get the EO (via the EOEntityClassDescription) to validate the value so that we get
        // GVCEOFValidation involved
        try
        {
            // BUG! This won't call any validateValue... methods, EOCustomObject's validateValueForKey does that
            // Also need the EO for that validation
            EOClassDescription cd = EOClassDescription.classDescriptionForEntityName(entity().name());
            cd.validateValueForKey(value, property().name());
            throw new RuntimeException("Failed to throw for invalid value " + value + " on " + property());
        }
        catch (EOFValidationException e)
        {
            super.validationFailedWithException(e, value, keyPath);
        }
        catch (Exception e)
        {
            // Fall back to original mesasge
            super.validationFailedWithException(t, value, keyPath);
        }


        /** ensure [error_recorded] hasValidationFailure();  **/
    }


}
