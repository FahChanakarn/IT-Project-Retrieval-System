package com.springmvc.model;

import javax.persistence.*;

@Entity
@Table(name = "student_496")
@PrimaryKeyJoinColumn(name = "stu_id")
public class Student496 extends Student {

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @Column(name = "stu_image", length = 255)
    private String stu_image;

    @Column(name = "registered_subject", length = 100, nullable = false)
    private String registeredSubject;

    public Student496() {
        super();
    }

    public Student496(String stuId, String stu_prefix, String stu_firstName, String stu_lastName,
                      String stu_password, Project project,String stu_image, String registeredSubject) {
        super(stuId, stu_prefix, stu_firstName, stu_lastName, stu_password);
        this.project = project;
        this.stu_image = stu_image;
        this.registeredSubject = registeredSubject;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

	public String getStu_image() {
		return stu_image;
	}

	public void setStu_image(String stu_image) {
		this.stu_image = stu_image;
	}

	public String getRegisteredSubject() {
		return registeredSubject;
	}

	public void setRegisteredSubject(String registeredSubject) {
		this.registeredSubject = registeredSubject;
	}

    
}
