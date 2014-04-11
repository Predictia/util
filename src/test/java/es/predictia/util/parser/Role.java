/**
 * 
 */
package es.predictia.util.parser;

public class Role {

	public Role() {
		super();
	}
	
	public Role(String name) {
		super();
		this.name = name;
	}

	private String name;
	
	private String description;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Role [name=" + name + "]";
	}

}