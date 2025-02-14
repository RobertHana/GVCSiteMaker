package com.houdah.webobjects.eoaccess;

import com.houdah.webobjects.eoaccess.coercion.BestMatchQualifierCoercionSupport;
import com.houdah.webobjects.eoaccess.coercion.BestRelationshipMatchesQualifierCoercionSupport;
import com.houdah.webobjects.eoaccess.coercion.ExistsInRelationshipQualifierCoercionSupport;
import com.houdah.webobjects.eoaccess.coercion.InSetQualifierCoercionSupport;
import com.houdah.webobjects.eoaccess.coercion.InSubqueryQualifierCoercionSupport;
import com.houdah.webobjects.eoaccess.coercion.PeriodQualifierCoercionSupport;
import com.houdah.webobjects.eoaccess.coercion.PiggybackQualifierCoercionSupport;
import com.houdah.webobjects.eoaccess.coercion.QualifierAttributeCoercion;
import com.houdah.webobjects.eoaccess.databaseContext.DatabaseContext;
import com.houdah.webobjects.eoaccess.qualifiers.BestMatchQualifierSupport;
import com.houdah.webobjects.eoaccess.qualifiers.BestRelationshipMatchesQualifierSupport;
import com.houdah.webobjects.eoaccess.qualifiers.ExistsInRelationshipQualifierSupport;
import com.houdah.webobjects.eoaccess.qualifiers.FalseQualifierSupport;
import com.houdah.webobjects.eoaccess.qualifiers.InSetQualifierSupport;
import com.houdah.webobjects.eoaccess.qualifiers.InSubqueryQualifierSupport;
import com.houdah.webobjects.eoaccess.qualifiers.PeriodQualifierSupport;
import com.houdah.webobjects.eoaccess.qualifiers.PiggybackQualifierSupport;
import com.houdah.webobjects.eoaccess.qualifiers.TrueQualifierSupport;
import com.houdah.webobjects.eocontrol.qualifiers.BestMatchQualifier;
import com.houdah.webobjects.eocontrol.qualifiers.BestRelationshipMatchesQualifier;
import com.houdah.webobjects.eocontrol.qualifiers.ExistsInRelationshipQualifier;
import com.houdah.webobjects.eocontrol.qualifiers.FalseQualifier;
import com.houdah.webobjects.eocontrol.qualifiers.InSetQualifier;
import com.houdah.webobjects.eocontrol.qualifiers.InSubqueryQualifier;
import com.houdah.webobjects.eocontrol.qualifiers.PeriodQualifier;
import com.houdah.webobjects.eocontrol.qualifiers.PiggybackQualifier;
import com.houdah.webobjects.eocontrol.qualifiers.TrueQualifier;
import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eoaccess.EOQualifierSQLGeneration;
import com.webobjects.foundation.NSLog;

/**
 * <p>
 * Principal class of the EOAccess project. Sets up various classes in the
 * project.
 * </p>
 * 
 * <p>
 * This sample code is provided for educational purposes. It is mainly to be
 * considered a source of information and a way to spark off discussion. I make
 * no warranty, expressed or implied, about the quality of this code or its
 * usefulness in any particular situation. Use this code or any code based on it
 * at your own risk. Basically, enjoy the read, but don't blame me for anything.
 * </p>
 * 
 * @author Pierre Bernard http://homepage.mac.com/i_love_my/webobjects.html
 */
public class PrincipalClass
{
	// Static initializer

	static {
		NSLog.debug.appendln("Initializing EOAccess");

		NSLog.debug.appendln("Setting up SQL generation support for custom qualifiers");

		EOQualifierSQLGeneration.Support.setSupportForClass(new BestMatchQualifierSupport(),
				BestMatchQualifier.class);

		EOQualifierSQLGeneration.Support.setSupportForClass(
				new BestRelationshipMatchesQualifierSupport(),
				BestRelationshipMatchesQualifier.class);

		EOQualifierSQLGeneration.Support.setSupportForClass(new InSetQualifierSupport(),
				InSetQualifier.class);

		EOQualifierSQLGeneration.Support.setSupportForClass(new InSubqueryQualifierSupport(),
				InSubqueryQualifier.class);

		EOQualifierSQLGeneration.Support.setSupportForClass(
				new ExistsInRelationshipQualifierSupport(), ExistsInRelationshipQualifier.class);

		EOQualifierSQLGeneration.Support.setSupportForClass(new PeriodQualifierSupport(),
				PeriodQualifier.class);

		EOQualifierSQLGeneration.Support.setSupportForClass(new TrueQualifierSupport(),
				TrueQualifier.class);

		EOQualifierSQLGeneration.Support.setSupportForClass(new FalseQualifierSupport(),
				FalseQualifier.class);

		EOQualifierSQLGeneration.Support.setSupportForClass(new PiggybackQualifierSupport(),
				PiggybackQualifier.class);

		NSLog.debug.appendln("Setting up coercion support for custom qualifiers");

		QualifierAttributeCoercion.registerSupportForClass(new BestMatchQualifierCoercionSupport(),
				BestMatchQualifier.class);

		QualifierAttributeCoercion.registerSupportForClass(
				new BestRelationshipMatchesQualifierCoercionSupport(),
				BestRelationshipMatchesQualifier.class);

		QualifierAttributeCoercion.registerSupportForClass(new InSetQualifierCoercionSupport(),
				InSetQualifier.class);

		QualifierAttributeCoercion.registerSupportForClass(
				new InSubqueryQualifierCoercionSupport(), InSubqueryQualifier.class);

		QualifierAttributeCoercion.registerSupportForClass(
				new ExistsInRelationshipQualifierCoercionSupport(),
				ExistsInRelationshipQualifier.class);

		QualifierAttributeCoercion.registerSupportForClass(new PeriodQualifierCoercionSupport(),
				PeriodQualifier.class);

		QualifierAttributeCoercion.registerSupportForClass(new PiggybackQualifierCoercionSupport(),
				PiggybackQualifier.class);

		NSLog.debug.appendln("Setting the EOAccess DatabaseContext as database context class");
		EODatabaseContext.setContextClassToRegister(DatabaseContext.class);

		/*
		 * Not in use, GVC already provides handling for this.
		 * NSLog.debug.appendln( "Setting the EOAccess DatabaseContextDelegate
		 * as default delegate of EODatabaseContext");
		 * EODatabaseContext.setDefaultDelegate(new DatabaseContextDelegate());
		 */
	}
}
