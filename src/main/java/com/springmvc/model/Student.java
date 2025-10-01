package com.springmvc.model;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "student")
public class Student {

	@Id
	@Column(name = "stu_id", length = 10)
	private String stuId;

	@Column(name = "stu_prefix", length = 10, nullable = false)
	private String stu_prefix;

	@Column(name = "stu_firstname", length = 100, nullable = false)
	private String stu_firstName;

	@Column(name = "stu_lastname", length = 100, nullable = false)
	private String stu_lastName;

	
	
	@Column(name = "stu_password", length = 15, nullable = false)
	private String stu_password;

	public Student() {
		super();
	}

	public Student(String stuId, String stu_prefix, String stu_firstName, String stu_lastName, String stu_password) {
		this.stuId = stuId;
		this.stu_prefix = stu_prefix;
		this.stu_firstName = stu_firstName;
		this.stu_lastName = stu_lastName;
		this.stu_password = stu_password;

	}

	// Getter/Setter
	public String getStuId() {
		return stuId;
	}

	public void setStuId(String stuId) {
		this.stuId = stuId;
	}

	public String getStu_prefix() {
		return stu_prefix;
	}

	public void setStu_prefix(String stu_prefix) {
		this.stu_prefix = stu_prefix;
	}

	public String getStu_firstName() {
		return stu_firstName;
	}

	public void setStu_firstName(String stu_firstName) {
		this.stu_firstName = stu_firstName;
	}

	public String getStu_lastName() {
		return stu_lastName;
	}

	public void setStu_lastName(String stu_lastName) {
		this.stu_lastName = stu_lastName;
	}

	public String getStu_password() {
		return stu_password;
	}

	public void setStu_password(String stu_password) {
		this.stu_password = stu_password;
	}
}
