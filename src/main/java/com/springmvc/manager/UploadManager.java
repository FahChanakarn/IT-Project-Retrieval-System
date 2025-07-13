// UploadManager.java (แก้ไขแล้วให้สอดคล้องกับ document_file table)
package com.springmvc.manager;

import com.springmvc.model.DocumentFile;
import com.springmvc.model.HibernateConnection;
import com.springmvc.model.Project;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.web.multipart.MultipartFile;

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

		Query<DocumentFile> query = session.createQuery("FROM DocumentFile WHERE project.projectId = :pid ORDER BY file_no ASC",
				DocumentFile.class);
		query.setParameter("pid", projectId);

		List<DocumentFile> list = query.list();
		session.close();
		return list;
	}

	public void saveFile(int projectId, String fileType, String fileName, MultipartFile file, String videoLink) {
		SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		DocumentFile doc = new DocumentFile();
		Project project = session.get(Project.class, projectId); // ต้องอยู่ใน session เปิดอยู่
		doc.setProject(project);
		doc.setFiletype(fileType);
		doc.setFilename(fileName);
		
		LocalDateTime localDateTime = LocalDateTime.now();
		Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		doc.setSendDate(date);
		doc.setStatus("อัปโหลดสำเร็จ");

		if ("ไฟล์เอกสาร".equals(fileType) && file != null && !file.isEmpty()) {
			try {
				String filePath = "uploads/" + file.getOriginalFilename(); // ปรับ path ตามจริง
				file.transferTo(new File(filePath));
				doc.setFilepath(filePath);
			} catch (IOException e) {
				e.printStackTrace();
				doc.setStatus("เกิดข้อผิดพลาด");
			}
		} else if ("ลิงก์วิดีโอ".equals(fileType)) {
			doc.setFilepath(videoLink);
		}

		// หาลำดับล่าสุด
		Query<Integer> maxQuery = session.createQuery("SELECT MAX(fileno) FROM DocumentFile WHERE project.projectId = :pid",
				Integer.class);
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
