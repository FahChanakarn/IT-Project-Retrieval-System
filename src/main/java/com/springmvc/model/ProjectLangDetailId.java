package com.springmvc.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;

@Embeddable
public class ProjectLangDetailId implements Serializable {

    @Column(name = "project_id")
    private int projectId;

    @Column(name = "lang_id")
    private int langId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectLangDetailId that = (ProjectLangDetailId) o;
        return projectId == that.projectId && langId == that.langId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, langId);
    }
    
	public ProjectLangDetailId() {
	}

	public ProjectLangDetailId(int projectId, int langId) {
		super();
		this.projectId = projectId;
		this.langId = langId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getLangId() {
		return langId;
	}

	public void setLangId(int langId) {
		this.langId = langId;
	}
   
}

