package com.narp.endpoints;

import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.datastore.KeyFactory;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class User {

	@Persistent(valueStrategy = IdGeneratorStrategy.SEQUENCE)
	private long key;

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	@Persistent
	@PrimaryKey
	private String user;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Persistent
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Persistent
	private Boolean active;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

//	@ApiMethod(name="loginValid")
//	public Boolean loginValid(String user, String passwrod) {
//		PersistenceManager pm = PMF.get().getPersistenceManager();
//		try {
//			Key k = KeyFactory.createKey(User.class.getSimpleName(), user);
//			User e = pm.getObjectById(User.class, k);
//			if (e.getPassword() == passwrod) {
//				return true;
//			} else {
//				return false;
//			}
//		} finally {
//			pm.close();
//		}
//	}

	public User(String user, String password) {
		this.setUser(user);
		this.setPassword(password);
		this.setActive(true);
	}
}