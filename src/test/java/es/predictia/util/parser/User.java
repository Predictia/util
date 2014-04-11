/**
 * 
 */
package es.predictia.util.parser;

import java.util.Set;

public class User {

	public User() {
		super();
	}
	
	public User(String username) {
		super();
		this.username = username;
	}

	private String username;
	
	private String email;
	
	private String password;
	
	private boolean active;

    private Set<Role> roles;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "User [username=" + username + "]";
	}
	
}