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
		String uploadBasePath = "D:/Project496Uploads/uploadsFile"; // ปรับตามที่คุณสร้างไว้
		new File(uploadBasePath).mkdirs(); // เผื่อยังไม่มี

		if ("file".equals(fileType) && file != null && !file.isEmpty()) {
			try {
				// ✅ ตั้งชื่อไฟล์ไม่ซ้ำ
				String safeFilename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
				String fullPath = uploadBasePath + File.separator + safeFilename;

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

	public void updateFileOrVideo(int id, String filename, String videoLink, MultipartFile newFile,
			HttpServletRequest request) {
		Session session = HibernateConnection.doHibernateConnection().openSession();
		try {
			session.beginTransaction();
			DocumentFile file = session.get(DocumentFile.class, id);
			file.setFilename(filename);

			if (file.getFiletype().equals("video")) {
				file.setFilepath(videoLink);
			} else if (newFile != null && !newFile.isEmpty()) {
				String uploadPath = request.getServletContext().getRealPath("/uploads/");
				String filePath = uploadPath + File.separator + newFile.getOriginalFilename();
				newFile.transferTo(new File(filePath));
				file.setFilepath("uploads/" + newFile.getOriginalFilename());
			}

			session.update(file);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.close();
		}
	}

}
