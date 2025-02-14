package com.gvcsitemaker.components;

import net.global_village.virtualtables.*;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import er.ajax.*;


public class ReorderModalDialog extends WOComponent
{
    public VirtualColumn currentColumn;
    protected NSMutableArray ordered;


    public ReorderModalDialog(WOContext context)
    {
        super(context);
    }



    public void closeDialog()
    {
        AjaxModalDialog.close(context());
    }



    public NSMutableArray ordered()
    {
        return ordered;
    }


    public void setOrdered(NSMutableArray newOrdered)
    {
        ordered = newOrdered;
    }



}
