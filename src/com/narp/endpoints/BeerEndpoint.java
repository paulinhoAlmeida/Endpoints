package com.narp.endpoints;

import com.narp.endpoints.PMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JDOCursorHelper;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

@Api(name = "beerendpoint", namespace = @ApiNamespace(ownerDomain = "narp.com", ownerName = "narp.com", packagePath = "endpoints"))
public class BeerEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listBeer")
	public CollectionResponse<Beer> listBeer(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Beer> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Beer.class);
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<Beer>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Beer obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Beer> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getBeer")
	public Beer getBeer(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		Beer beer = null;
		try {
			beer = mgr.getObjectById(Beer.class, id);
		} finally {
			mgr.close();
		}
		return beer;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param beer the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertBeer")
	public Beer insertBeer(Beer beer) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (containsBeer(beer)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.makePersistent(beer);
		} finally {
			mgr.close();
		}
		return beer;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param beer the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateBeer")
	public Beer updateBeer(Beer beer) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsBeer(beer)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(beer);
		} finally {
			mgr.close();
		}
		return beer;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeBeer")
	public void removeBeer(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			Beer beer = mgr.getObjectById(Beer.class, id);
			mgr.deletePersistent(beer);
		} finally {
			mgr.close();
		}
	}

	private boolean containsBeer(Beer beer) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Beer.class, beer.getId());
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			contains = false;
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
