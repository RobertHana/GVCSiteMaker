In order to run the tests, do the following:
- add the .jar from the framework to the User Entries part of the jUnit launcher classpath

This file summarizes some of the features of VirtualTables (VT).  It is intended as a "way-in" document for programmer users of VT.

Virtual Tables Setup
====================
These scripts need to be run to setup the schema for VT.  They are for FrontBase, modifications may be needed for other databases.
These scripts also need to be run before the unit tests can be run.

CreateVTTables.sql - creates the schema as defined in the EOModel
PerformanceIndexes.sql - extra indexes to make things faster
VTInitialLoadFrontbase.sql - default column type definitions
DropVTTables.sql - mostly useful for cleaning up test database after a bug


Using VirtualTables
===================

Initializing VT
---------------
In order to use the quick-PK generate mechanism built into VT, you need to call VirtualTable.initializeVirtualTables().  It is not necessary to call it first, or even at all if you don't need the PK generation mechanism.  Note that using this will cause gaps in PKs, though they will remain sequential unless something write to the database without using this mechanism.

Creating a VT and it's metadata
-------------------------------
VirtualTable table = VirtualTable.createVirtualTable(ec, name);
table.newColumn(columnName, ColumnType.StringColumnType);
VirtualLookupColumn column = (VirtualLookupColumn)table.newColumn(lookupColumnName, ColumnType.LookupColumnType);
column.setLookupColumn(anotherColumn);

Note that in this version of VT, there are no constraints (not null, unique, etc).

Creating VT data
----------------
VirtualRow row = table.newRow();
row.takeValueForFieldNamed(value, columnName);          // These two forms
row.takeValueForKey(lookupValue, lookupColumnName);     // are interchangable

Querying VT data
----------------
table.objectsWithQualifier(qualifier);

Note that the qualifier must _not_ be an SQL qualifier, as all qualification is done in memory for VT.  Also, unlike all other features of VT, the qualifier must use _normalized_ column names (using either normalized or non-normalized column names anywhere else is acceptable).


Virtual Column Names
====================

Virtual column names must obviously be unique (like all column names), but they have an additional restriction: they must not conflict with any method names on VirtualRow.  This is because you can use KVC on VirtualRow to get field values.  If you named a column the same as a method on VirtualRow, you would get the value of the method on VirtualRow, instead of the value of the field you wanted.


Creating a New Column Type
==========================

There are several things that need to happen when you create a new column type:

1) Create a new ColumnType row in the database.  The entityNameRestrictor attribute needs to be blank ("") if you're _not_ creating a VirtualColumn subclass, and should be the type name if you are (but isn't required to be: whatever you enter here is slapped between "Virtual" and "Column" to create the column EO for this type).

2) If the type is one that can be imported/exported, use ColumnType.addToValidImportTypes() and addToValidExportTypes() to add the new type name to the set of types that can be imported/exported (this obviously needs to be done at runtime).

3) Create a new VirtualColumn subclass if you need to.  For example, VirtualLookupColumn adds the "columnToLookup" property so that it knows which column is looked up.  Most new column types will likely not have to create a new VirtualColumn subclass.  If you create a new VirtualColumn subclass, you'll also need to:
  a) name it Virtual<type>Column.  The ColumnType's entityNameRestrictor should contain "<type>". See (1), above
  b) override awakeFromInsertion() to set the restrictingValue attribute to anything you want.  This is used in single table inheritance, but you must set the value even if you're not using single table inheritance
  c) override typeDescription() if necessary
  d) override importAttributeName() if necessary.  You'll need to do this if, for example, your new type holds a relationship to another object instead of the object itself.  See VirtualLookupColumn's implementation for more information
  e) override valueForImportedValue() if necessary.  If you needed to override importAttributeName(), then you'll likely need to override this as well (and vice versa).  This method allows you to change the imported value into something completely different.  For example, the import might get a string of UUencoded bytes and you could have your VirtualColumn subclass write those bytes to the disk and return a path to the file.  Obviously, if this is the case, your VirtualField subclass (described below) should store a path and not a file!

4) Create a new VirtualField subclass, and
  a) name it Virtual<type>Field, where <type> is the name of the new type
  b) create an attribute called <type>Value, where <type> is the name of the new type.  This must be an attribute (not relationship!) of the EO.  For example, if the new type is called FooBar, you'll need an attribute called fooBarValue on your new VirtualField subclass
  c) override awakeFromInsertion() to set the restrictingValue attribute to the new type's name
  d) override value() and setValue() to be synonyms for <type>Value() and set<type>Value()

In addition to the above, you can obviously add any custom logic to your classes as necessary (for example, you might want additional validation on the field value).


Importing Table Data
====================

Importing data is the most complicated process in VT.  There are many parameters and callbacks used to ensure maximum flexibility.  Here are the more complicated options and how/why they are used:

1) The valueForImportedValue() callback and the Sender are used to translate value taken from an import file into values that you want to store. See (3e) in Creating a New Column Type, above, for an example of how and when to use this.  The Sender is an arbitrary object you can pass into the import process to further refine what to do with the import data.

2) Formatters are used to parse locale-specific data.  You can pass in a dictionary of column names and formatters to the import process.  If a formatter exists for a given column name, then each field in that column will have it's string data passed to the formatter to formatting.  Normally you would not use both a formatter and the valueForImportedValue() callback, since they accomplish basically the same thing in different ways. If you ever need to, keep in mind that the import value is formatted _first_, then the formatted value is passed to valueForImportedValue().


The Future of Virtual Tables
============================

In the future, the following features may be added to VT:

0) See the BUGS file for bugs and limitations of the current VT framework.

1) Concrete Tables.  This would allow the "Virtual Table Server" and performance-critical virtual tables to use the exact same interface as virtual tables, but would store the data in a physical database table, instead of in virtual tables.

2) Constraints.  This would add constraint checking to field values.  Currently, any validation you want has to be custom written on the VirtualField subclass.  This feature would make some of the constraints more data-driven.
