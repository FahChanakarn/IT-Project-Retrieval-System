package com.springmvc.model;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "download_tokens")
public class DownloadToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "token_id")
	private int tokenId;

	@Column(name = "token_string", unique = true, nullable = false, length = 255)
	private String tokenString;

	@Column(name = "file_id", nullable = false)
	private int fileId;

	@Column(name = "created_date", nullable = false)
	private Date createdDate;

	@Column(name = "expiry_date", nullable = false)
	private Date expiryDate;

	@Column(name = "download_count", columnDefinition = "int default 0")
	private int downloadCount;

	@Column(name = "last_downloaded")
	private Date lastDownloaded;

	@Column(name = "is_active", columnDefinition = "boolean default true")
	private boolean isActive;

	public DownloadToken() {
		super();
	}

	public DownloadToken(String tokenString, int fileId, Date createdDate, Date expiryDate) {
		this.tokenString = tokenString;
		this.fileId = fileId;
		this.createdDate = createdDate;
		this.expiryDate = expiryDate;
		this.downloadCount = 0;
		this.isActive = true;
	}

	// Check if token is expired
	public boolean isExpired() {
		return !isActive || new Date().after(expiryDate);
	}

	// Getters and Setters
	public int getTokenId() {
		return tokenId;
	}

	public void setTokenId(int tokenId) {
		this.tokenId = tokenId;
	}

	public String getTokenString() {
		return tokenString;
	}

	public void setTokenString(String tokenString) {
		this.tokenString = tokenString;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public int getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}

	public Date getLastDownloaded() {
		return lastDownloaded;
	}

	public void setLastDownloaded(Date lastDownloaded) {
		this.lastDownloaded = lastDownloaded;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}
}