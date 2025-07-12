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
}

