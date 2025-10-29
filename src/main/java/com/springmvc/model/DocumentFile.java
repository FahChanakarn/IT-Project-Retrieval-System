package com.springmvc.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "document_file")
public class DocumentFile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "file_id")
	private int fileId;

	@Column(name = "file_no")
	private int fileno;

	@Column(name = "filename", length = 255)
	private String filename;

	@Column(name = "file_path", length = 255)
	private String filepath;

	@Column(name = "send_date")
	private Date sendDate;

	@Column(name = "file_status", length = 50)
	private String status;

	@Column(name = "file_type", length = 255)
	private String filetype;

	@Column(name = "publish_status")
	private boolean publishStatus;

	@Column(name = "uploaded_by", length = 10)
	private String uploadedBy;

	@ManyToOne
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	public DocumentFile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DocumentFile(int fileId, int fileno, String filename, String filepath, Date sendDate, String status,
			String filetype, boolean publishStatus, String uploadedBy, Project project) {
		super();
		this.fileId = fileId;
		this.fileno = fileno;
		this.filename = filename;
		this.filepath = filepath;
		this.sendDate = sendDate;
		this.status = status;
		this.filetype = filetype;
		this.publishStatus = publishStatus;
		this.uploadedBy = uploadedBy;
		this.project = project;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public int getFileno() {
		return fileno;
	}

	public void setFileno(int fileno) {
		this.fileno = fileno;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public boolean isPublishStatus() {
		return publishStatus;
	}

	public void setPublishStatus(boolean publishStatus) {
		this.publishStatus = publishStatus;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

}
