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

		if ("file".equals(fileType) && file != null && !file.isEmpty()) {
			try {
				String uploadDir = context.getRealPath("/assets/uploadsFile");
				new File(uploadDir).mkdirs(); // สร้างโฟลเดอร์ถ้ายังไม่มี

				// สร้างชื่อไฟล์ใหม่เพื่อเลี่ยงชื่อซ้ำ
				String safeFilename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
				String fullPath = uploadDir + File.separator + safeFilename;

				file.transferTo(new File(fullPath));

				// เก็บ path แบบ relative สำหรับเปิดดูผ่าน browser
				doc.setFilepath("assets/images/uploadsFile/" + safeFilename);

			} catch (IOException e) {
				e.printStackTrace();
				doc.setStatus("เกิดข้อผิดพลาด");
			}

		} else if ("video".equals(fileType)) {
			doc.setFilepath(videoLink);
		}

		// หาลำดับ
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
}
