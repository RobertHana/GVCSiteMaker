package net.global_village.woextensions;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;

import net.global_village.eofextensions.*;


/**
 * EOEnterpriseObject aware subclass of EditingPageComponentForEOProperty for use when the
 * <code>value</code> binding is bound to an attribute or relationship of an EOEnterpriseObject.
 * The keypath for <code>value</code> is analyzed to determine the corresponding EOAttribute
 * or EORelationship of an EOEntity.
 */
public class EditingPageComponentForEO extends EditingPageComponentForEOProperty
{

    /** Optimization variable */
    EOProperty property;



    public EditingPageComponentForEO(WOContext context)
    {
        super(context);
    }



    /**
     * Clear optimization values.
     */
    public void reset()
    {
        property = null;
        super.reset();
    }



    /**
     * @return the EOProperty modelling the value that is bound to this component
     */
    public EOProperty property()
    {
        /** check [keypath_indicates_property]  EOObject.isKeyPathToAttribute(parent(), valueBindingPath()) ||
                                                EOObject.isKeyPathToRelationship(parent(), valueBindingPath());    **/

        if (property == null)
        {
            if (EOObject.isKeyPathToAttribute(parent(), valueBindingPath()))
            {
                property = EOObject.attributeFromKeyPath(parent(), valueBindingPath());
            }
            else if (EOObject.isKeyPathToRelationship(parent(), valueBindingPath()))
            {
                property = EOObject.relationshipFromKeyPath(parent(), valueBindingPath());
            }
            else
            {
                reportError(true, "value binding does not point to attribute or relationship");
            }
        }

        return property;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * @return property() cast to cast to EOAttribute or null if relationship
     */
    public EOAttribute attribute()
    {
        return property() instanceof EOAttribute ? (EOAttribute) property() : null;
    }



    /**
     * @return property() cast to cast to EORelationship or null if attribute
     */
    public EORelationship relationship()
    {
        return property() instanceof EORelationship ? (EORelationship) property() : null;
    }


}
