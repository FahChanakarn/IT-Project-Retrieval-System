package com.springmvc.manager;

import com.springmvc.model.DocumentFile;
import com.springmvc.model.HibernateConnection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

public class DocumentFileManager {

	// หาไฟล์จาก ID
	public static DocumentFile findById(int fileId) {
		Session session = null;
		DocumentFile file = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			file = session.get(DocumentFile.class, fileId);

			session.getTransaction().commit();
		} catch (Exception e) {
			if (session != null && session.getTransaction().isActive())
				session.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return file;
	}

	// อัปเดตไฟล์
	public static void updateFile(DocumentFile file) {
		Session session = null;
		Transaction tx = null;
		try {
			SessionFactory sf = HibernateConnection.doHibernateConnection();
			session = sf.openSession();
			tx = session.beginTransaction();

			session.merge(file);

			tx.commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive())
				tx.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}

	// บันทึกไฟล์ใหม่
	public static void saveFile(DocumentFile file) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			session.save(file);

			session.getTransaction().commit();
		} catch (Exception e) {
			if (session != null && session.getTransaction().isActive())
				session.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}

	// ดึงไฟล์ทั้งหมดของโปรเจ็กต์
	public static List<DocumentFile> getFilesByProject(int projectId) {
		Session session = null;
		List<DocumentFile> files = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			files = session.createQuery("FROM DocumentFile df WHERE df.project.projectId = :pid", DocumentFile.class)
					.setParameter("pid", projectId).getResultList();

			session.getTransaction().commit();
		} catch (Exception e) {
			if (session != null && session.getTransaction().isActive())
				session.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return files;
	}
}