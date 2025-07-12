package com.springmvc.model;

import javax.persistence.*;

@Entity
@Table(name="programming_lang")
public class ProgrammingLang {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lang_id")
    private int langId;

    @Column(name = "langname", length = 100, unique = true, nullable = false)
    private String langName;

	public ProgrammingLang() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProgrammingLang(int langId, String langName) {
		super();
		this.langId = langId;
		this.langName = langName;
	}

	public int getLangId() {
		return langId;
	}

	public void setLangId(int langId) {
		this.langId = langId;
	}

	public String getLangName() {
		return langName;
	}

	public void setLangName(String langName) {
		this.langName = langName;
	}

	

}
