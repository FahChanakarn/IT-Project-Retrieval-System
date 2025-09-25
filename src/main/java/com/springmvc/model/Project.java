package com.springmvc.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "project")
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_id")
	private int projectId;

	@Column(name = "projectname_th", length = 255, nullable = false)
	private String proj_NameTh;

	@Column(name = "projectname_en", length = 255, nullable = false)
	private String proj_NameEn;

	@Column(name = "semester", length = 50, nullable = false)
	private String semester;

	@Column(name = "abstract_th", columnDefinition = "TEXT")
	private String abstractTh;

	@Column(name = "abstract_en", columnDefinition = "TEXT")
	private String abstractEn;

	@Column(name = "project_type", length = 100)
	private String projectType;

	@Column(name = "approve_status", length = 50)
	private String approveStatus;

	@Column(name = "approve_date")
	private Date approveDate;

	@Column(name = "testing_status", length = 20, columnDefinition = "VARCHAR(20) DEFAULT '0'")
	private String testing_status;

	@Column(name = "keyword_th")
	private String keywordTh;
	
	@Column(name = "keyword_en")
	private String keywordEn;


	@ManyToOne
	@JoinColumn(name = "advisor_id", nullable = false)
	private Advisor advisor;

	@ManyToOne
	@JoinColumn(name = "type_id", nullable = true)
	private TypeDB typeDB;

	@OneToMany(mappedBy = "project")
	private List<Student496> student496s;

	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
	private Set<ProjectLangDetail> projectLangDetails = new HashSet<>();
	
	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
	private List<DocumentFile> documentFiles;

	public Project() {
		super();
	}

	public Project(int projectId, String proj_NameTh, String proj_NameEn, String semester, String abstractTh,
			String abstractEn, String projectType, String approveStatus, Date approveDate, String testing_status,
			String keywordTh, String keywordEn, Advisor advisor, TypeDB typeDB, List<Student496> student496s,
			Set<ProjectLangDetail> projectLangDetails, List<DocumentFile> documentFiles) {
		super();
		this.projectId = projectId;
		this.proj_NameTh = proj_NameTh;
		this.proj_NameEn = proj_NameEn;
		this.semester = semester;
		this.abstractTh = abstractTh;
		this.abstractEn = abstractEn;
		this.projectType = projectType;
		this.approveStatus = approveStatus;
		this.approveDate = approveDate;
		this.testing_status = testing_status;
		this.keywordTh = keywordTh;
		this.keywordEn = keywordEn;
		this.advisor = advisor;
		this.typeDB = typeDB;
		this.student496s = student496s;
		this.projectLangDetails = projectLangDetails;
		this.documentFiles = documentFiles;
	}


	// Getter/Setter ทั้งหมด
	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getProj_NameTh() {
		return proj_NameTh;
	}

	public void setProj_NameTh(String proj_NameTh) {
		this.proj_NameTh = proj_NameTh;
	}

	public String getProj_NameEn() {
		return proj_NameEn;
	}

	public void setProj_NameEn(String proj_NameEn) {
		this.proj_NameEn = proj_NameEn;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public String getAbstractTh() {
		return abstractTh;
	}

	public void setAbstractTh(String abstractTh) {
		this.abstractTh = abstractTh;
	}

	public String getAbstractEn() {
		return abstractEn;
	}

	public void setAbstractEn(String abstractEn) {
		this.abstractEn = abstractEn;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public Date getApproveDate() {
		return approveDate;
	}

	public void setApproveDate(Date approveDate) {
		this.approveDate = approveDate;
	}

	public String getTesting_status() {
		return testing_status;
	}

	public void setTesting_status(String testing_status) {
		this.testing_status = testing_status;
	}

	public String getKeywordTh() {
		return keywordTh;
	}

	public void setKeywordTh(String keywordTh) {
		this.keywordTh = keywordTh;
	}

	public String getKeywordEn() {
		return keywordEn;
	}

	public void setKeywordEn(String keywordEn) {
		this.keywordEn = keywordEn;
	}

	public Advisor getAdvisor() {
		return advisor;
	}

	public void setAdvisor(Advisor advisor) {
		this.advisor = advisor;
	}

	public TypeDB getTypeDB() {
		return typeDB;
	}

	public void setTypeDB(TypeDB typeDB) {
		this.typeDB = typeDB;
	}
	
	public List<Student496> getStudent496s() {
		return student496s;
	}

	public void setStudent496s(List<Student496> student496s) {
		this.student496s = student496s;
	}

	public Set<ProjectLangDetail> getProjectLangDetails() {
		return projectLangDetails;
	}

	public void setProjectLangDetails(Set<ProjectLangDetail> projectLangDetails) {
		this.projectLangDetails = projectLangDetails;
	}

	public List<DocumentFile> getDocumentFiles() {
		return documentFiles;
	}

	public void setDocumentFiles(List<DocumentFile> documentFiles) {
		this.documentFiles = documentFiles;
	}
	
}
