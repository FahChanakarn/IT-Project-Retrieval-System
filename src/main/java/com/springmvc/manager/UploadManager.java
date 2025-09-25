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
import java.util.Date;
import java.util.List;

public class UploadManager {

	// ✅ ใช้ path เดียวกันทั้งการอัปโหลดและแก้ไข
	private static final String UPLOAD_BASE_PATH = "D:/Project496Uploads/uploadsFile";

	public List<DocumentFile> getFilesByProject(int projectId) {
		SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
		Session session = sessionFactory.openSession();

		Query<DocumentFile> query = session.createQuery(
				"FROM DocumentFile WHERE project.projectId = :pid ORDER BY file_no ASC", DocumentFile.class);
		query.setParameter("pid", projectId);

		List<DocumentFile> list = query.list();
		session.close();
		return list;
	}

	public void saveFile(int projectId, String fileType, String fileName, MultipartFile file, String videoLink,
			ServletContext context) {

		SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
		Session session = sessionFactory.openSession();
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

		// ✅ เซ็ต path จริงบนเครื่อง
		new File(UPLOAD_BASE_PATH).mkdirs(); // เผื่อยังไม่มี

		if ("file".equals(fileType) && file != null && !file.isEmpty()) {
			try {
				// ✅ ตั้งชื่อไฟล์ไม่ซ้ำ
				String safeFilename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
				String fullPath = UPLOAD_BASE_PATH + File.separator + safeFilename;

				// ✅ debug log
				System.out.println("📂 Saving file to: " + fullPath);

				file.transferTo(new File(fullPath));

				// ✅ เก็บแค่ชื่อไฟล์ไว้ (ไม่ใช่ path จริง)
				doc.setFilepath(safeFilename);

			} catch (IOException e) {
				e.printStackTrace();
				doc.setStatus("เกิดข้อผิดพลาด");
			}
		} else if ("video".equals(fileType)) {
			doc.setFilepath(videoLink); // เก็บลิงก์โดยตรง
		}

		// ลำดับไฟล์
		Query<Integer> maxQuery = session
				.createQuery("SELECT MAX(fileno) FROM DocumentFile WHERE project.projectId = :pid", Integer.class);
		maxQuery.setParameter("pid", projectId);
		Integer maxNo = maxQuery.uniqueResult();
		doc.setFileno(maxNo == null ? 1 : maxNo + 1);

		session.save(doc);
		session.getTransaction().commit();
		session.close();
	}

	public DocumentFile getFileById(int fileId) {
		Session session = HibernateConnection.doHibernateConnection().openSession();
		DocumentFile file = session.get(DocumentFile.class, fileId);
		session.close();
		return file;
	}

	// ✅ แก้ไข method updateFileOrVideo
	public void updateFileOrVideo(int id, String filename, String videoLink, MultipartFile newFile,
			HttpServletRequest request) {
		Session session = HibernateConnection.doHibernateConnection().openSession();
		try {
			session.beginTransaction();
			DocumentFile file = session.get(DocumentFile.class, id);

			if (file == null) {
				throw new RuntimeException("ไม่พบไฟล์ที่ต้องการแก้ไข");
			}

			// อัปเดตชื่อไฟล์
			file.setFilename(filename);

			if ("video".equals(file.getFiletype())) {
				// ✅ แก้ไขวิดีโอ - อัปเดตลิงก์
				file.setFilepath(videoLink);
				System.out.println("🎥 Updated video link: " + videoLink);

			} else if ("file".equals(file.getFiletype()) && newFile != null && !newFile.isEmpty()) {
				// ✅ แก้ไขไฟล์ PDF - อัปโหลดไฟล์ใหม่

				// สร้างโฟลเดอร์ถ้ายังไม่มี
				File uploadDir = new File(UPLOAD_BASE_PATH);
				if (!uploadDir.exists()) {
					uploadDir.mkdirs();
				}

				// ลบไฟล์เก่า (ถ้ามี)
				String oldFilePath = file.getFilepath();
				if (oldFilePath != null && !oldFilePath.isEmpty()) {
					File oldFile = new File(UPLOAD_BASE_PATH + File.separator + oldFilePath);
					if (oldFile.exists()) {
						boolean deleted = oldFile.delete();
						System.out.println(
								"🗑️ Deleted old file: " + oldFile.getAbsolutePath() + " - Success: " + deleted);
					}
				}

				// บันทึกไฟล์ใหม่
				String safeFilename = System.currentTimeMillis() + "_" + newFile.getOriginalFilename();
				String fullPath = UPLOAD_BASE_PATH + File.separator + safeFilename;

				try {
					newFile.transferTo(new File(fullPath));
					file.setFilepath(safeFilename); // อัปเดต path ใหม่

					System.out.println("📄 Updated PDF file: " + fullPath);
					System.out.println("💾 New filepath in DB: " + safeFilename);

				} catch (IOException e) {
					System.err.println("❌ Error saving new file: " + e.getMessage());
					throw new RuntimeException("ไม่สามารถบันทึกไฟล์ใหม่ได้: " + e.getMessage());
				}
			}

			// อัปเดต timestamp
			LocalDateTime localDateTime = LocalDateTime.now();
			Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
			file.setSendDate(date);

			session.merge(file); // ใช้ merge แทน update เพื่อความปลอดภัย
			session.getTransaction().commit();

			System.out.println("✅ File updated successfully - ID: " + id);

		} catch (Exception e) {
			System.err.println("❌ Error updating file: " + e.getMessage());
			e.printStackTrace();
			if (session.getTransaction().isActive()) {
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