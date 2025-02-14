package net.global_village.woextensions;

import com.webobjects.appserver.*;




/**
 * Base class for data input components that will be used on an EditingPage subclass.
 * It provides a caption, description, concept of required, and validation error reporting.
 * It also provides a unique identifier for this component and  easy access to the
 * editing context, page, and session.
 */
public abstract class EditingPageComponent extends WOComponent
{


    /** Constant for value binding */
    public static final String VALUE_BINDING = "value";
    public static final String ID_BINDING = "id";


    /** Optimization */
    private String uniqueIdentifier;


    public EditingPageComponent(WOContext context)
    {
        super(context);
    }



    /**
     * @return <code>true</code> these components are stateless
     */
    public boolean isStateless()
    {
        return true;
    }



    /**
     * Clears uniqueIdentifier().
     */
    public void reset()
    {
        super.reset();
        uniqueIdentifier = null;
    }



    /**
     * Sets uniqueIdentifier().
     */
    public void awake()
    {
        super.awake();
        uniqueIdentifier = context().elementID();
    }



    /**
     * The caption (title) to display with the input.
     *
     * @return the caption to display with the input
     */
    public abstract String caption();



    /**
     * Optional description that can be displayed with the input.
     *
     * @return the localized description of this input for use in the UI.
     */
    public abstract String description();



    /**
     * @return <code>true</code> if this property is required
     */
    public abstract boolean isRequired();



    /**
     * @return true if a validation failure has been registered for this component
     */
    public boolean hasValidationFailure()
    {
        return page().hasValidationFailureForKey(uniqueIdentifier());
    }



    /**
     * @return the validation failure message for this component
     */
    public String validationFailure()
    {
        /** require [hasValidationFailure] hasValidationFailure();
                    [is_string] page().validationFailureForKey(_uniqueIdentifier()) instanceof String;  **/
        return page().validationFailureMessageForKey(uniqueIdentifier());
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Overridden to register validation errors thrown during the takeValues phase .
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        page().validationFailedWithException(t, value, keyPath);

        // Try to force invalid value in so the user can see it and edit it
        try
        {
            // A keyPath of <none> indicates a constant value association or use of ^ syntax so try setting the value directly instead
            if ("<none>".equals(keyPath))
            {
                // BUG(ish) this can re-trigger the exception thrown by WOAssocation and result in the correct(!) exception
                // getting recorded over the incorrect one sent to the page above.
                setValue(value);
            }
            else
            {
                takeValueForKeyPath(value, keyPath);
            }
        }
        catch (ClassCastException e)
        {
            // Won't go, nothing we can do.
        }
        catch (IllegalArgumentException e)
        {
            // Won't go, nothing we can do.
        }

        /** ensure [error_recorded] hasValidationFailure();  **/
    }



    /**
     * Returns id() to use use as the unique identifier for this component.
     *
     * @see #id()
     * @return unique key to use when saving / retrieving validation error messages
     */
    public String uniqueIdentifier()
    {
        return id();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * If there is an "id" binding, the bound value is returned.  If id is not bound, returns context().elementID() as that will be unique.
     */
    public String id()
    {
        return hasBinding(ID_BINDING) ? (String) valueForBinding(ID_BINDING) : uniqueIdentifier;
    }



    /**
     * HACK so that _uniqueIdentifier() can be used in a contract in place of uniqueIdentifier() and
     * sub-classes can still override uniqueIdentifier().  If just uniqueIdentifier() is used and the subclass
     * does not have contracts such that it generates jassInternal_uniqueIdentifier(), then the
     * implementation in this class gets used.
     *
     * @return uniqueIdentifier()
     */
    public String _uniqueIdentifier()
    {
        return uniqueIdentifier();
    }



    /**
     * @return page's EOEditingContext
     */
    public com.webobjects.eocontrol.EOEditingContext editingContext()
    {
        return page().editingContext();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * @return session() downcast to net.global_village.woextensions.WOSession
     */
    public WOSession gvcWOSession()
    {
        return (WOSession) session();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * @return the nearest EditingPage parent component downcast to net.global_village.woextensions.EditingPage
     */
    public EditingPage page()
    {
        com.webobjects.appserver.WOComponent page = parent();
        while (page != null && ! (page instanceof EditingPage))
        {
            page = page.parent();
        }

        if (page == null)
        {
            throw new RuntimeException(name() + " used on a page that is not an EditingPage subclass or used in a constructor");
        }

        return (EditingPage) page;
    }



    /**
     * @return the value that this component is bound to
     */
    public Object value()
    {
        reportError( ! hasBinding(VALUE_BINDING), "Missing required 'value' binding");
        return valueForBinding(VALUE_BINDING);
    }



    /**
     * Sets the value that this component is bound to
     */
    public void setValue(Object newValue)
    {
        setValueForBinding(newValue, VALUE_BINDING);

        // value().equals(newValue) is not a valid post condition if a validation exception is raised
        // or if code downstream massages newValue.  Don't try to ensure this.
    }



    /**
     * @return the key path bound to 'value' in the WOD file for this component
     */
    public String valueBindingPath()
    {
        return keyPathForBinding(VALUE_BINDING);
    }



}
