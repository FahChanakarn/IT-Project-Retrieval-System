package com.springmvc.model;

import javax.persistence.*;

@Entity
@Table(name = "project_lang_detail")
public class ProjectLangDetail {

    @EmbeddedId
    private ProjectLangDetailId id;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @MapsId("langId")
    @JoinColumn(name = "lang_id")
    private ProgrammingLang programmingLang;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

	public ProjectLangDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProjectLangDetail(ProjectLangDetailId id, Project project, ProgrammingLang programmingLang,
			String description) {
		super();
		this.id = id;
		this.project = project;
		this.programmingLang = programmingLang;
		this.description = description;
	}

	public ProjectLangDetailId getId() {
		return id;
	}

	public void setId(ProjectLangDetailId id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public ProgrammingLang getProgrammingLang() {
		return programmingLang;
	}

	public void setProgrammingLang(ProgrammingLang programmingLang) {
		this.programmingLang = programmingLang;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    
}

