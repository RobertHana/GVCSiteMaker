package com.webobjects.eoaccess;

public class EntityTattletale {

	public static void tattletale(EOEntity entity)
	{
        System.out.println("********* entityName: " + entity.name());
		System.out.println("********* dbSnapshotKeys: " + entity.dbSnapshotKeys());
        System.out.println("********* attributesToFetch: " + entity.attributesToFetch());
        System.out.println("********* attributes: " + entity.attributes());
	}
}
