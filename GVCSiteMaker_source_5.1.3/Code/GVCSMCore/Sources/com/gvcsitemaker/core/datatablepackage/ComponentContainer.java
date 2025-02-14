// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.datatablepackage;

import java.util.*;

import com.webobjects.foundation.*;

/**
 * Container that represents a PageComponent in a data table package.
 */
public class ComponentContainer
{
    protected String bindings;
    protected boolean canEdit;
    protected boolean useCustomTemplate;
    protected String searchFormType;    
    protected String componentType;   
    protected String subType;      
    protected NSMutableArray children;
    protected String databaseTableName;
    protected String columnName;   
    protected String template;        
    protected String defaultFieldValue;
    protected String componentOrder;
    
    
    /**
     * Designated constructor.
     */     
    public ComponentContainer()
    {
        children = new NSMutableArray();
    }
    
    
    /**
     * Returns the child component with of this container whose type is aComponentType
     * 
     * @param aComponentType the name of the the component type to match
     * @return  the child component with of this container whose type is aComponentType
     */      
    public ComponentContainer childComponentWithType(String aComponentType)
    {
        /** require [aComponentType_not_null]  aComponentType != null; **/
        
        ComponentContainer matchingComponentContainer = null;
        
        Enumeration childrenEnumerator = children().objectEnumerator();
        while (childrenEnumerator.hasMoreElements())
        {
            ComponentContainer aComponent = (ComponentContainer) childrenEnumerator.nextElement();
            if (aComponent.componentType().equals(aComponentType))
            {
                matchingComponentContainer = aComponent;
            }
        }
        
        return matchingComponentContainer;
    }     
    
    
    
    /**
     * Returns the child ComponentContainer for the passed aColumnName and aComponentType.
     * 
     * @param aColumnName the name of the the column to match
     * @param aComponentType the name of the the component type to match      
     * @return the child ComponentContainer for the passed aColumnName and aComponentType.
     */    
    public ComponentContainer childComponentForColumn(String aColumnName, String aComponentType)
    {
        /** require 
         [aColumnName_not_null]  aColumnName != null; 
         [aComponentType_not_null]  aComponentType != null;         
         **/
        
        ComponentContainer matchingComponentContainer = null;
        
        Enumeration childrenEnumerator = children().objectEnumerator();
        while (childrenEnumerator.hasMoreElements())
        {
            ComponentContainer aComponent = (ComponentContainer) childrenEnumerator.nextElement();
            if (aComponent.columnName().equals(aColumnName) && aComponent.componentType().equals(aComponentType))
            {
                matchingComponentContainer = aComponent;
            }
        }
        
        return matchingComponentContainer;
    }      

    /*** Generic getters/setters ****/
    
    public String databaseTableName() 
    {
        return databaseTableName;
    }



    public void setDatabaseTableName(String aValue) 
    {
        databaseTableName = aValue; 
    }    
      
    
    
    public String columnName() 
    {
        return columnName;
    }



    public void setColumnName(String aValue) 
    {
        columnName = aValue; 
    }    

    
    
    public String bindings() 
    {
        return bindings;
    }



    public void setBindings(String aValue) 
    {
        bindings = aValue; 
    }

    
    
    public String template() 
    {
        return template;
    }



    public void setTemplate(String aValue) 
    {
        template = aValue; 
    }    



    public String componentType() 
    {
        return componentType;
    }



    public void setComponentType(String aValue) 
    {
        componentType = aValue;
    }

    
    
    public String subType() 
    {
        return subType;
    }



    public void setSubType(String aValue) 
    {
        subType = aValue;
    }

    

    public NSArray children()
    {
        return children;
    }

    
    
    public void addToChildren(ComponentContainer newComponent) 
    {
        NSMutableArray array = (NSMutableArray)children();
        array.addObject(newComponent);
    }
    
    
    

    public boolean useCustomTemplate()
    {
        return useCustomTemplate;
    }

    
    
    public void setUseCustomTemplate(boolean booleanValue)
    {
        useCustomTemplate = booleanValue;
    }    
    
    
    public String searchFormType()
    {
        return searchFormType;
    }
    
    
    
    public void setSearchFormType(String newType)    
    {
        searchFormType = newType;
    }    
    
    
    
    public boolean canEdit()
    {
        return canEdit;
    }

    
    
    public void setCanEdit(boolean booleanValue)
    {
        canEdit = booleanValue;
    }      
    
    
    
    public String defaultFieldValue()
    {
        return defaultFieldValue;
    }
    
    
    
    public void setDefaultFieldValue(String newValue)    
    {
        defaultFieldValue = newValue;
    }    
    
    
    
    public String componentOrder()
    {
        return componentOrder;
    }
    
    
 
    public void setComponentOrder(String newComponentOrder)
    {
        componentOrder = newComponentOrder;
    }  
}
