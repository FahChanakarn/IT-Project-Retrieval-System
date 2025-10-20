package com.springmvc.manager;

import com.springmvc.model.DocumentFile;
import com.springmvc.model.HibernateConnection;
import com.springmvc.model.Project;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UploadManager {

	private static final String UPLOAD_BASE_PATH = "D:/Project496Uploads/uploadsFile";

	public List<DocumentFile> getFilesByProject(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			Query<DocumentFile> query = session.createQuery(
					"FROM DocumentFile WHERE project.projectId = :pid ORDER BY file_no ASC", DocumentFile.class);
			query.setParameter("pid", projectId);

			List<DocumentFile> list = query.list();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

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

			LocalDateTime localDateTime = LocalDateTime.now();
			Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
			doc.setSendDate(date);
			doc.setStatus("อัปโหลดสำเร็จ");

			new File(UPLOAD_BASE_PATH).mkdirs();

			if ("file".equals(fileType) && file != null && !file.isEmpty()) {
				try {
					// ดึง extension จาก original file
					String originalFilename = file.getOriginalFilename();
					String extension = "";
					if (originalFilename != null && originalFilename.contains(".")) {
						extension = originalFilename.substring(originalFilename.lastIndexOf("."));
					}

					// สร้างชื่อไฟล์: รหัสนศ.ของคนอัปโหลด + ชื่อที่กรอก + extension
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

	public void updateFileOrVideo(int id, String filename, String videoLink, MultipartFile newFile,
			String uploadedByStudentId, HttpServletRequest request) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			DocumentFile file = session.get(DocumentFile.class, id);

			if (file == null) {
				throw new RuntimeException("ไม่พบไฟล์ที่ต้องการแก้ไข");
			}

			file.setFilename(filename);

			if ("video".equals(file.getFiletype())) {
				file.setFilepath(videoLink);
				System.out.println("🎥 Updated video link: " + videoLink);

			} else if ("file".equals(file.getFiletype()) && newFile != null && !newFile.isEmpty()) {
				File uploadDir = new File(UPLOAD_BASE_PATH);
				if (!uploadDir.exists()) {
					uploadDir.mkdirs();
				}

				String oldFilePath = file.getFilepath();
				if (oldFilePath != null && !oldFilePath.isEmpty()) {
					File oldFile = new File(UPLOAD_BASE_PATH + File.separator + oldFilePath);
					if (oldFile.exists()) {
						boolean deleted = oldFile.delete();
						System.out.println(
								"🗑️ Deleted old file: " + oldFile.getAbsolutePath() + " - Success: " + deleted);
					}
				}

				// ดึง extension จาก original file
				String originalFilename = newFile.getOriginalFilename();
				String extension = "";
				if (originalFilename != null && originalFilename.contains(".")) {
					extension = originalFilename.substring(originalFilename.lastIndexOf("."));
				}

				// สร้างชื่อไฟล์: รหัสนศ.ของคนอัปโหลด + ชื่อที่กรอก + extension
				String safeFilename = uploadedByStudentId + "_" + filename + extension;
				String fullPath = UPLOAD_BASE_PATH + File.separator + safeFilename;

				try {
					newFile.transferTo(new File(fullPath));
					file.setFilepath(safeFilename);

					System.out.println("📄 Updated PDF file: " + fullPath);
					System.out.println("💾 New filepath in DB: " + safeFilename);

				} catch (IOException e) {
					System.err.println("❌ Error saving new file: " + e.getMessage());
					throw new RuntimeException("ไม่สามารถบันทึกไฟล์ใหม่ได้: " + e.getMessage());
				}
			}

			LocalDateTime localDateTime = LocalDateTime.now();
			Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
			file.setSendDate(date);

			session.merge(file);
			session.getTransaction().commit();

			System.out.println("✅ File updated successfully - ID: " + id);

		} catch (Exception e) {
			System.err.println("❌ Error updating file: " + e.getMessage());
			e.printStackTrace();
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			throw new RuntimeException("เกิดข้อผิดพลาดในการแก้ไขไฟล์: " + e.getMessage());
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}
}