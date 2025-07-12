package com.springmvc.model;

import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="typeDB")
public class TypeDB {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private int typeId;
	
	@Column(name = "type_name", length = 100, nullable = false)
    private String typeName;
	
	@Column(name = "software_name", length = 100, nullable = false)
    private String softwareName;
	
	@OneToMany(mappedBy = "typeDB")
    private List<Project> projects;

	public TypeDB() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TypeDB(int typeId, String typeName, String softwareName, List<Project> projects) {
		super();
		this.typeId = typeId;
		this.typeName = typeName;
		this.softwareName = softwareName;
		this.projects = projects;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getSoftwareName() {
		return softwareName;
	}

	public void setSoftwareName(String softwareName) {
		this.softwareName = softwareName;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	} 
	
}
