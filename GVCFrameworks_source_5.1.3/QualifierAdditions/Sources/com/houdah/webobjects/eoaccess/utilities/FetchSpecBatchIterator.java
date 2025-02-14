package com.houdah.webobjects.eoaccess.utilities;

import java.util.Enumeration;

import com.houdah.webobjects.eocontrol.qualifiers.InSetQualifier;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSRange;
import com.webobjects.foundation.NSSet;

/**
 * <p>
 * The purpose of the fetch specification iterator is to retrieve and manipulate
 * a potentially exorbitant number of objects matching a given query by slicing
 * it into reasonable sized batches. With memory management as of WebObjects
 * 5.2, members of past batches will become subject to garbage collection unless
 * retained by application code.
 * </p>
 * 
 * <p>
 * It is a good idea to combine the use of the FetchSpecBatchIterator with
 * batched saveChanges() operations. Indeed, as an editing context holds strong
 * references to modified objects, memory use may grow over time unless changes
 * are saved.
 * </p>
 * 
 * <p>
 * The FetchSpecBatchIterator by fetching primary keys only shortly before
 * delivering the first batch of objects. Batches are then created from subsets
 * of the retrieved set of primary keys. Though retrieved objects may represent
 * fresher data than was available at the time of the first fetch, their
 * selection however will not be modified by changes to the database occurring
 * after the initial fetch.
 * </p>
 * 
 * <p>
 * The database is queried once to build the list of rows to retrieve. On each
 * iteration only a batch objects rows is retrieved to be instantiated as
 * objects. This allows for memory efficient operations on large sets of
 * objects.
 * </p>
 * 
 * <p>
 * CAVEAT: This does NOT work on objects whose entity has a compound primary
 * key.
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
public class FetchSpecBatchIterator implements Enumeration
{
	// Public class constants

	public static final int DEFAULT_BATCH_SIZE = 200;

	// Protected instance variables

	protected EOFetchSpecification fetchSpecification;

	protected EOEditingContext editingContext;

	protected int batchSize;

	protected boolean useFaults;

	protected NSArray prefetchingRelationshipKeyPaths;

	protected NSArray primaryKeys = null;

	protected int batchIndex;

	protected int batchCount;

	// Constructors

	/**
	 * Constructor. <BR>
	 * 
	 * Once constructed, data is retrieved in a lazy manner from the database.
	 * 
	 * @param the
	 *            fetch specification specifying the objects to retrieve
	 * @param the
	 *            editing context to fetch into
	 * @param the
	 *            maximum size of the batches to create
	 */
	public FetchSpecBatchIterator(EOFetchSpecification fetchSpecification,
			EOEditingContext editingContext)
	{
		this(fetchSpecification, editingContext, DEFAULT_BATCH_SIZE);
	}

	/**
	 * Designated Constructor. <BR>
	 * 
	 * Once constructed, data is retrieved in a lazy manner from the database.
	 * 
	 * @param the
	 *            fetch specification specifying the objects to retrieve
	 * @param the
	 *            editing context to fetch into
	 * @param the
	 *            maximum size of the batches to create
	 */
	public FetchSpecBatchIterator(EOFetchSpecification fetchSpecification,
			EOEditingContext editingContext, int batchSize)
	{
		if (fetchSpecification == null) {
			throw new IllegalArgumentException("fetchSpecification may not be null");
		}

		if (editingContext == null) {
			throw new IllegalArgumentException("editingContext may not be null");
		}

		if (batchSize <= 0) {
			throw new IllegalArgumentException("batchSize must greater than zero");
		}

		this.fetchSpecification = pkFetchSpec(editingContext, fetchSpecification);
		this.editingContext = editingContext;
		this.useFaults = false;
		this.batchSize = batchSize;
		this.prefetchingRelationshipKeyPaths = fetchSpecification.prefetchingRelationshipKeyPaths();
	}

	// Public instance methods

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements()
	{
		return hasMoreBatches();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Enumeration#nextElement()
	 */
	public Object nextElement()
	{
		return nextBatch();
	}

	public NSArray nextBatch()
	{
		if (hasMoreBatches()) {
			NSArray batch = null;
			NSArray primaryKeys = primaryKeys();
			int pkCount = primaryKeys.count();
			int fetchCount = this.batchIndex * this.batchSize;
			int length = (pkCount - fetchCount > this.batchSize) ? this.batchSize
					: (pkCount - fetchCount);
			NSRange range = new NSRange(fetchCount, length);
			NSArray primaryKeysToFetch = primaryKeys.subarrayWithRange(range);
			String pkAttributeName = (String) this.fetchSpecification.rawRowKeyPaths()
					.objectAtIndex(0);

			if (useFaults()) {
				NSMutableArray mutableBatch = new NSMutableArray(length);

				for (int i = 0; i < length; i++) {
					mutableBatch.addObject(EOUtilities.faultWithPrimaryKey(editingContext(),
							this.fetchSpecification.entityName(), new NSDictionary(
									primaryKeysToFetch.objectAtIndex(i), pkAttributeName)));
				}

				batch = mutableBatch.immutableClone();
			}
			else {
				InSetQualifier qualifier = new InSetQualifier(pkAttributeName, new NSSet(
						primaryKeysToFetch));
				EOFetchSpecification fetchSpec = new EOFetchSpecification(this.fetchSpecification
						.entityName(), qualifier, this.fetchSpecification.sortOrderings());

				fetchSpec.setPrefetchingRelationshipKeyPaths(this.prefetchingRelationshipKeyPaths);
				batch = editingContext().objectsWithFetchSpecification(fetchSpec);
			}
			this.batchIndex += 1;

			return batch;
		}

		throw new IllegalStateException("No more batches");
	}

	public boolean hasMoreBatches()
	{
		if (!hasFetched()) {
			fetchPrimaryKeys();
		}

		return this.batchIndex < this.batchCount;
	}

	public EOEditingContext editingContext()
	{
		return this.editingContext;
	}

	public boolean useFaults()
	{
		return this.useFaults;
	}

	/**
	 * Defines if the iterator should retrieve faults rather than fullfledged
	 * objects.<BR>
	 * 
	 * This is of advantage in those rare situations where the values of the
	 * objects attributes are not needed. Here it saves both and IO time and
	 * memory space.<BR>
	 * 
	 * CAVEAT: This feature should not be used when the rows retrieved are to be
	 * transformed into full blown enterprise objects later on. Faulting objects
	 * one at a time would create a significant performance hit. Object deletion
	 * unfortunately does fire the fault in order to perform validation!
	 * 
	 * @param useFauls
	 *            true to enable this feature, off by default
	 */
	public void setUseFaults(boolean useFaults)
	{
		this.useFaults = useFaults;
	}

	// Protected instance methods

	protected NSArray primaryKeys()
	{
		if (!hasFetched()) {
			fetchPrimaryKeys();
		}

		return this.primaryKeys;
	}

	protected boolean hasFetched()
	{
		return (this.primaryKeys != null);
	}

	protected void fetchPrimaryKeys()
	{
		EOEntity entity = EOUtilities.entityNamed(editingContext(), this.fetchSpecification
				.entityName());
		NSArray primaryKeyAttributeNames = entity.primaryKeyAttributeNames();
		String pkAttributeName = (String) primaryKeyAttributeNames.objectAtIndex(0);
		NSArray primaryKeyDictionaries = editingContext().objectsWithFetchSpecification(
				this.fetchSpecification);

		this.primaryKeys = (NSArray) primaryKeyDictionaries.valueForKey(pkAttributeName);
		this.batchIndex = 0;
		this.batchCount = (int) Math
				.ceil((this.primaryKeys.count() * 1.0) / (this.batchSize * 1.0));
	}

	// Protected class methods

	protected static EOFetchSpecification pkFetchSpec(EOEditingContext editingContext,
			EOFetchSpecification fetchSpecification)
	{
		EOEntity entity = EOUtilities.entityNamed(editingContext, fetchSpecification.entityName());
		EOFetchSpecification pkFetchSpec = (EOFetchSpecification) fetchSpecification.clone();

		pkFetchSpec.setFetchesRawRows(true);
		pkFetchSpec.setRawRowKeyPaths(entity.primaryKeyAttributeNames());
		pkFetchSpec.setFetchLimit(fetchSpecification.fetchLimit());

		if (entity.primaryKeyAttributes().count() != 1) {
			throw new IllegalArgumentException("The entity " + entity.name()
					+ " has a compound primary key. Not supported.");
		}

		return pkFetchSpec;
	}
}