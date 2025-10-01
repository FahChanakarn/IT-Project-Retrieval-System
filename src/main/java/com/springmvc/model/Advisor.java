package com.springmvc.model;

import javax.persistence.*;

@Entity
@Table(name="advisor")
public class Advisor {
	
	@Id
    @Column(name = "advisor_id", length = 10)
    private String advisorId;
	
	@Column(name = "advisor_prefix", length = 10, nullable = false)
	private String adv_prefix;

    @Column(name = "advisor_firstname", length = 100, nullable = false)
    private String adv_firstName;

    @Column(name = "advisor_lastname", length = 100, nullable = false)
    private String adv_lastName;

    @Column(name = "advisor_email", length = 30, unique = true, nullable = false)
    private String adv_email;

    @Column(name = "advisorpassword", length = 15, nullable = false)
    private String adv_password;

    @Column(name = "position", length = 100, nullable = false)
    private String adv_position;
    
    @Column(name = "advisor_status", length = 20, nullable = false)
    private String adv_status;

	public Advisor() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Advisor(String advisorId, String adv_prefix, String adv_firstName, String adv_lastName, String adv_email,
			String adv_password, String adv_position, String adv_status) {
		super();
		this.advisorId = advisorId;
		this.adv_prefix = adv_prefix;
		this.adv_firstName = adv_firstName;
		this.adv_lastName = adv_lastName;
		this.adv_email = adv_email;
		this.adv_password = adv_password;
		this.adv_position = adv_position;
		this.adv_status = adv_status;
	}

	public String getAdvisorId() {
		return advisorId;
	}

	public void setAdvisorId(String advisorId) {
		this.advisorId = advisorId;
	}

	public String getAdv_prefix() {
		return adv_prefix;
	}

	public void setAdv_prefix(String adv_prefix) {
		this.adv_prefix = adv_prefix;
	}

	public String getAdv_firstName() {
		return adv_firstName;
	}

	public void setAdv_firstName(String adv_firstName) {
		this.adv_firstName = adv_firstName;
	}

	public String getAdv_lastName() {
		return adv_lastName;
	}

	public void setAdv_lastName(String adv_lastName) {
		this.adv_lastName = adv_lastName;
	}

	public String getAdv_email() {
		return adv_email;
	}

	public void setAdv_email(String adv_email) {
		this.adv_email = adv_email;
	}

	public String getAdv_password() {
		return adv_password;
	}

	public void setAdv_password(String adv_password) {
		this.adv_password = adv_password;
	}

	public String getAdv_position() {
		return adv_position;
	}

	public void setAdv_position(String adv_position) {
		this.adv_position = adv_position;
	}

	public String getAdv_status() {
		return adv_status;
	}

	public void setAdv_status(String adv_status) {
		this.adv_status = adv_status;
	}

    
	
}
