package com.springmvc.manager;

import com.springmvc.model.DocumentFile;
import com.springmvc.model.HibernateConnection;
import com.springmvc.model.Project;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UploadManager {

	private static final String UPLOAD_BASE_PATH = "D:/Project496Uploads/uploadsFile";

	// DTO class สำหรับ wrap ข้อมูลไฟล์พร้อมชื่อผู้อัปโหลด
	public static class FileWithUploader {
		private DocumentFile file;
		private String uploaderName;

		public FileWithUploader(DocumentFile file, String uploaderName) {
			this.file = file;
			this.uploaderName = uploaderName;
		}

		public DocumentFile getFile() {
			return file;
		}

		public String getUploaderName() {
			return uploaderName;
		}
	}

	// ดึงรายการไฟล์พร้อมชื่อผู้อัปโหลด
	public List<FileWithUploader> getFilesByProject(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			// Query ไฟล์ทั้งหมดของโครงงาน
			Query<DocumentFile> fileQuery = session.createQuery(
					"FROM DocumentFile WHERE project.projectId = :pid ORDER BY fileno ASC", DocumentFile.class);
			fileQuery.setParameter("pid", projectId);
			List<DocumentFile> files = fileQuery.list();

			List<FileWithUploader> fileList = new ArrayList<>();

			// Query ชื่อผู้อัปโหลดสำหรับแต่ละไฟล์
			for (DocumentFile file : files) {
				String uploaderName = "-";

				// ตรวจสอบว่ามี uploaded_by หรือไม่
				if (file.getUploadedBy() != null && !file.getUploadedBy().trim().isEmpty()) {
					try {
						// ✅ Query จากตาราง student (เพราะใช้ JOINED inheritance)
						// แสดงเฉพาะชื่อ ไม่แสดง prefix
						String sql = "SELECT stu_firstname FROM student WHERE stu_id = :stuId";

						Query<String> nameQuery = session.createNativeQuery(sql);
						nameQuery.setParameter("stuId", file.getUploadedBy());

						String result = nameQuery.uniqueResult();

						if (result != null && !result.trim().isEmpty()) {
							uploaderName = result.trim();
						}
					} catch (Exception e) {
						System.out.println("⚠️ Cannot find uploader for file ID: " + file.getFileId()
								+ " (uploaded_by: " + file.getUploadedBy() + ")");
						System.out.println("⚠️ Error: " + e.getMessage());
					}
				}

				fileList.add(new FileWithUploader(file, uploaderName));
			}

			return fileList;
		} catch (Exception e) {
			System.err.println("❌ Error fetching files: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	// อัปโหลดไฟล์ใหม่
	public void saveFile(int projectId, String fileType, String fileName, MultipartFile file, String videoLink,
			String uploadedByStudentId, ServletContext context) {

		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			DocumentFile doc = new DocumentFile();
			Project project = session.get(Project.class, projectId);
			doc.setProject(project);
			doc.setFiletype(fileType);
			doc.setFilename(fileName);

			// ตั้งค่า uploadedBy
			doc.setUploadedBy(uploadedByStudentId);

			LocalDateTime localDateTime = LocalDateTime.now();
			Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
			doc.setSendDate(date);
			doc.setStatus("อัปโหลดสำเร็จ");

			new File(UPLOAD_BASE_PATH).mkdirs();

			if ("file".equals(fileType) && file != null && !file.isEmpty()) {
				try {
					String originalFilename = file.getOriginalFilename();
					String extension = "";
					if (originalFilename != null && originalFilename.contains(".")) {
						extension = originalFilename.substring(originalFilename.lastIndexOf("."));
					}

					String safeFilename = uploadedByStudentId + "_" + fileName + extension;
					String fullPath = UPLOAD_BASE_PATH + File.separator + safeFilename;

					System.out.println("📂 Saving file to: " + fullPath);

					file.transferTo(new File(fullPath));
					doc.setFilepath(safeFilename);

				} catch (IOException e) {
					e.printStackTrace();
					doc.setStatus("เกิดข้อผิดพลาด");
				}
			} else if ("video".equals(fileType)) {
				doc.setFilepath(videoLink);
			}

			Query<Integer> maxQuery = session
					.createQuery("SELECT MAX(fileno) FROM DocumentFile WHERE project.projectId = :pid", Integer.class);
			maxQuery.setParameter("pid", projectId);
			Integer maxNo = maxQuery.uniqueResult();
			doc.setFileno(maxNo == null ? 1 : maxNo + 1);

			session.save(doc);
			session.getTransaction().commit();

			System.out.println("✅ File saved successfully with uploader: " + uploadedByStudentId);

		} catch (Exception e) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	// ดึงไฟล์ตาม ID
	public DocumentFile getFileById(int fileId) {
		Session session = null;
		try {
			session = HibernateConnection.doHibernateConnection().openSession();
			DocumentFile file = session.get(DocumentFile.class, fileId);
			return file;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	// ลบไฟล์
	public void deleteFile(int fileId, ServletContext context) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			DocumentFile file = session.get(DocumentFile.class, fileId);

			if (file == null) {
				throw new RuntimeException("ไม่พบไฟล์ที่ต้องการลบ");
			}

			// ลบไฟล์จาก storage (เฉพาะไฟล์ PDF)
			if ("file".equals(file.getFiletype())) {
				String filePath = file.getFilepath();
				if (filePath != null && !filePath.isEmpty()) {
					File physicalFile = new File(UPLOAD_BASE_PATH + File.separator + filePath);
					if (physicalFile.exists()) {
						boolean deleted = physicalFile.delete();
						System.out.println("🗑️ Deleted physical file: " + physicalFile.getAbsolutePath()
								+ " - Success: " + deleted);
					}
				}
			}

			// ลบข้อมูลจากฐานข้อมูล
			session.delete(file);
			session.getTransaction().commit();

			System.out.println("✅ File deleted successfully - ID: " + fileId);

		} catch (Exception e) {
			System.err.println("❌ Error deleting file: " + e.getMessage());
			e.printStackTrace();
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			throw new RuntimeException("เกิดข้อผิดพลาดในการลบไฟล์: " + e.getMessage());
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}
}