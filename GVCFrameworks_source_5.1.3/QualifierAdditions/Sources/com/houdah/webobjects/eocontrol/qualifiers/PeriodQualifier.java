package com.houdah.webobjects.eocontrol.qualifiers;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/** 
 * <p>Qualifier to operate on month precision dates (a.k.a. periods) stored as year and month fields.</p>
 * 
 * <p>This sample code is provided for educational purposes. It is mainly to be considered a source of information 
 * and a way to spark off discussion. I make no warranty, expressed or implied, about the quality of this code or 
 * its usefulness in any particular situation. Use this code or any code based on it at your own risk. Basically, 
 * enjoy the read, but don't blame me for anything.</p>
 * 
 * @author Pierre Bernard http://homepage.mac.com/i_love_my/webobjects.html
 */
public class PeriodQualifier extends Qualifier implements EOQualifierEvaluation
{
	// Protected instance variables

	protected String keyPath;
	protected String yearKey;
	protected String monthKey;
	protected NSSelector qualifierOperator;
	protected Object yearValue;
	protected Object monthValue;
	protected Object period;

	// Constructor

	/** Constructs a qualifier to match a period EO.
	 * 
	 * @param keyPath the path to the period entity
	 * @param yearKey key to the period entity attribute holding the year value, not null
	 * @param monthKey key to the period entity attribute holding the month value, not null
	 * @param qualifierOperator comparison operator, not null
	 * @param yearValue reference value for year
	 * @param monthValue reference value for month
	 */
	public PeriodQualifier(
		String keyPath,
		String yearKey,
		String monthKey,
		NSSelector qualifierOperator,
		Object yearValue,
		Object monthValue)
	{
		if ((yearKey == null)
			|| (yearKey.indexOf(NSKeyValueCodingAdditions.KeyPathSeparator) > -1))
		{
			throw new IllegalArgumentException("yearKey must be a simple key");
		}

		if ((monthKey == null)
			|| (monthKey.indexOf(NSKeyValueCodingAdditions.KeyPathSeparator) > -1))
		{
			throw new IllegalArgumentException("monthKey must be a simple key");
		}

		if (qualifierOperator == null)
		{
			throw new IllegalArgumentException("qualifierOperator must not be null");
		}

		if (!((yearValue == NSKeyValueCoding.NullValue)
			|| (yearValue == null)
			|| (yearValue instanceof EOQualifierVariable)))
		{
			int intValue = ((Number) yearValue).intValue();

			if ((intValue < 1900) || (intValue > 3000))
			{
				throw new IllegalArgumentException("yearValue must be between 1900 and 3000");
			}
		}

		if (!((monthValue == NSKeyValueCoding.NullValue)
			|| (monthValue == null)
			|| (monthValue instanceof EOQualifierVariable)))
		{
			int intValue = ((Number) monthValue).intValue();

			if ((intValue < 1) || (intValue > 12))
			{
				throw new IllegalArgumentException("monthValue must be between 1 and 12");
			}
		}

		this.keyPath = "".equals(keyPath) ? null : keyPath;
		this.yearKey = yearKey;
		this.monthKey = monthKey;
		this.qualifierOperator = qualifierOperator;
		this.yearValue = yearValue;
		this.monthValue = monthValue;
		this.period = null;
	}

	// Public instance methods

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifierEvaluation#evaluateWithObject(java.lang.Object)
	 */
	public boolean evaluateWithObject(Object object)
	{
		Object rhs = period();
		String keyPath = keyPath();
		Object target =
			(keyPath != null)
				? NSKeyValueCodingAdditions.Utility.valueForKeyPath(object, keyPath())
				: object;

		Object year = NSKeyValueCoding.Utility.valueForKey(target, yearKey());
		Object month = NSKeyValueCoding.Utility.valueForKey(target, monthKey());

		Object lhs = null;

		if ((year == null) || (month == null))
		{
			lhs = NSKeyValueCoding.NullValue;
		}
		else
		{
			lhs = new Integer(((Number) year).intValue() * 100 + ((Number) month).intValue());
		}

		return EOQualifier.ComparisonSupport.compareValues(lhs, rhs, qualifierOperator());
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#qualifierWithBindings(com.webobjects.foundation.NSDictionary, boolean)
	 */
	public EOQualifier qualifierWithBindings(NSDictionary bindings, boolean requiresAll)
	{
		Object yearValue = yearValue();
		Object monthValue = monthValue();
		boolean hasBindings = false;
		if (yearValue instanceof EOQualifierVariable)
		{
			Object value = null;
			if (bindings != null)
			{
				value = bindings.valueForKeyPath(((EOQualifierVariable) yearValue).key());
			}

			if (value != null)
			{
				yearValue = value;
				hasBindings = true;
			}
			else
			{
				if (requiresAll)
				{
					throw new EOQualifier.QualifierVariableSubstitutionException(
						"Error in variable substitution: value for variable "
							+ yearValue
							+ " not found");
				}
				else
				{
					return null;
				}
			}
		}

		if (monthValue instanceof EOQualifierVariable)
		{
			Object value = null;
			if (bindings != null)
			{
				value = bindings.valueForKeyPath(((EOQualifierVariable) monthValue).key());
			}

			if (value != null)
			{
				monthValue = value;
				hasBindings = true;
			}
			else
			{
				if (requiresAll)
				{
					throw new EOQualifier.QualifierVariableSubstitutionException(
						"Error in variable substitution: value for variable "
							+ monthValue
							+ " not found");
				}
				else
				{
					return null;
				}
			}
		}

		if (hasBindings)
		{
			return new PeriodQualifier(
				keyPath(),
				yearKey(),
				monthKey(),
				qualifierOperator(),
				yearValue,
				monthValue);
		}
		else
		{
			return this;
		}
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#validateKeysWithRootClassDescription(com.webobjects.eocontrol.EOClassDescription)
	 */
	public void validateKeysWithRootClassDescription(EOClassDescription rootClassDescription)
	{
		Qualifier.validateKeyPathWithRootClassDescription(yearPath(), rootClassDescription);
		Qualifier.validateKeyPathWithRootClassDescription(monthPath(), rootClassDescription);
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#addQualifierKeysToSet(com.webobjects.foundation.NSMutableSet)
	 */
	public void addQualifierKeysToSet(NSMutableSet keySet)
	{
		keySet.addObject(yearPath());
		keySet.addObject(monthPath());
	}

	// Getters

	public String keyPath()
	{
		return this.keyPath;
	}

	public String yearKey()
	{
		return this.yearKey;
	}

	public String monthKey()
	{
		return this.monthKey;
	}

	public NSSelector qualifierOperator()
	{
		return this.qualifierOperator;
	}

	public Object yearValue()
	{
		return this.yearValue;
	}

	public Object monthValue()
	{
		return this.monthValue;
	}

	public String yearPath()
	{
		String yearPath = yearKey();
		String keyPath = keyPath();

		if (keyPath != null)
		{
			yearPath = keyPath + NSKeyValueCodingAdditions.KeyPathSeparator + yearPath;
		}

		return yearPath;
	}

	public String monthPath()
	{
		String monthPath = monthKey();
		String keyPath = keyPath();

		if (keyPath != null)
		{
			monthPath = keyPath + NSKeyValueCodingAdditions.KeyPathSeparator + monthPath;
		}

		return monthPath;
	}

	public Object period()
	{
		if (this.period == null)
		{
			Object yearValue = yearValue();
			Object monthValue = monthValue();

			if (yearValue instanceof EOQualifierVariable)
			{
				throw new IllegalStateException(
					"Value must be substitued for yearValue variable."
						+ " The qualifier variable '$"
						+ ((EOQualifierVariable) yearValue).key()
						+ "' is unbound.");
			}

			if (monthValue instanceof EOQualifierVariable)
			{
				throw new IllegalStateException(
					"Value must be substitued for monthValue variable."
						+ " The qualifier variable '$"
						+ ((EOQualifierVariable) monthValue).key()
						+ "' is unbound.");
			}

			if ((yearValue == null) || (monthValue == null))
			{
				this.period = NSKeyValueCoding.NullValue;
			}
			else
			{
				this.period =
					new Integer(
						((Number) yearValue).intValue() * 100 + ((Number) monthValue).intValue());
			}
		}

		return period;
	}
}