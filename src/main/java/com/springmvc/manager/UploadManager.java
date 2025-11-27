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

import com.springmvc.controller.UploadController;

public class UploadManager {

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

	public List<FileWithUploader> getFilesByProject(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			Query<DocumentFile> fileQuery = session.createQuery(
					"FROM DocumentFile WHERE project.projectId = :pid ORDER BY fileno ASC", DocumentFile.class);
			fileQuery.setParameter("pid", projectId);
			List<DocumentFile> files = fileQuery.list();

			List<FileWithUploader> fileList = new ArrayList<>();

			for (DocumentFile file : files) {
				String uploaderName = "-";

				if (file.getUploadedBy() != null && !file.getUploadedBy().trim().isEmpty()) {
					try {
						String sql = "SELECT stu_firstname FROM student WHERE stu_id = :stuId";

						Query<String> nameQuery = session.createNativeQuery(sql);
						nameQuery.setParameter("stuId", file.getUploadedBy());

						String result = nameQuery.uniqueResult();

						if (result != null && !result.trim().isEmpty()) {
							uploaderName = result.trim();
						}
					} catch (Exception e) {
						// Silent fail - use default "-"
					}
				}

				fileList.add(new FileWithUploader(file, uploaderName));
			}

			return fileList;
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

			doc.setUploadedBy(uploadedByStudentId);

			LocalDateTime localDateTime = LocalDateTime.now();
			Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
			doc.setSendDate(date);
			doc.setStatus("อัปโหลดสำเร็จ");

			new File(UploadController.BASE_UPLOAD_PATH).mkdirs();

			if ("file".equals(fileType) && file != null && !file.isEmpty()) {
				try {
					String originalFilename = file.getOriginalFilename();
					String extension = "";
					if (originalFilename != null && originalFilename.contains(".")) {
						extension = originalFilename.substring(originalFilename.lastIndexOf("."));
					}

					String safeFilename = uploadedByStudentId + "_" + fileName + extension;
					String fullPath = UploadController.BASE_UPLOAD_PATH + File.separator + safeFilename;

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

			if ("file".equals(file.getFiletype())) {
				String filePath = file.getFilepath();
				if (filePath != null && !filePath.isEmpty()) {
					File physicalFile = new File(UploadController.BASE_UPLOAD_PATH + File.separator + filePath);
					if (physicalFile.exists()) {
						physicalFile.delete();
					}
				}
			}

			session.delete(file);
			session.getTransaction().commit();

		} catch (Exception e) {
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