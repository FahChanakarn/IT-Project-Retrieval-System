package com.springmvc.model;

import javax.persistence.*;

@Entity
@Table(name = "programming_lang")
public class ProgrammingLang {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lang_id")
	private int langId;

	@Column(name = "lang_name", length = 100, unique = true, nullable = false)
	private String langName;

	@Enumerated(EnumType.STRING) // ✅ เก็บ enum เป็นข้อความ เช่น "PROGRAMMING", "DBMS"
	@Column(name = "lang_type", length = 50, nullable = false)
	private LangType langType;

	// Enum ภายใน class
	public enum LangType {
		PROGRAMMING, DBMS
	}

	public ProgrammingLang() {
	}

	public ProgrammingLang(int langId, String langName, LangType langType) {
		this.langId = langId;
		this.langName = langName;
		this.langType = langType;
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

	public LangType getLangType() {
		return langType;
	}

	public void setLangType(LangType langType) {
		this.langType = langType;
	}
}
