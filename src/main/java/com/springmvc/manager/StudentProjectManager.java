package com.springmvc.manager;

import com.springmvc.model.HibernateConnection;
import com.springmvc.model.Student496;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class StudentProjectManager {

	// ✅ แก้ไข: ดึงนักศึกษาและโครงงานตามเทอม (แก้ Connection Leak)
	public List<Object[]> getStudentProjectsByTerm(String term) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			// ✅ แก้ไข HQL: term อยู่ใน Project
			String hql = "SELECT s, p FROM Student496 s " + "LEFT JOIN s.project p " + "WHERE p.semester = :term "
					+ "ORDER BY s.stuId ASC";

			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("term", term);

			return query.list();

		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	// ✅ เพิ่ม: ดึงเฉพาะนักศึกษาตามเทอม (สำหรับหน้า login)
	public List<Student496> getStudentsByTerm(String term) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			// ✅ แก้ไข: term/semester อยู่ใน Project
			String hql = "SELECT s FROM Student496 s " + "JOIN s.project p " + "WHERE p.semester = :term "
					+ "ORDER BY s.stuId ASC";

			Query<Student496> query = session.createQuery(hql, Student496.class);
			query.setParameter("term", term);

			return query.list();

		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	// ✅ เพิ่ม: คำนวณเทอมปัจจุบันอัตโนมัติ
	public static String getCurrentTerm() {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		int month = cal.get(java.util.Calendar.MONTH) + 1; // 1-12
		int year = cal.get(java.util.Calendar.YEAR) + 543; // แปลงเป็น พ.ศ.

		// เทอม 1: มิ.ย. - ต.ค. (6-10)
		if (month >= 6 && month <= 10) {
			return "1/" + year;
		}
		// เทอม 2: พ.ย. - มี.ค. (11-3)
		else if (month >= 11 || month <= 3) {
			// ถ้าเดือน 1-3 ใช้ปีเดิม, ถ้า 11-12 ใช้ปีถัดไป
			return "2/" + (month <= 3 ? year : year + 1);
		}
		// เทอม 3/Summer: เม.ย. - พ.ค. (4-5)
		else {
			return "3/" + year;
		}
	}

	// ✅ เพิ่ม: นับจำนวนนักศึกษาตามเทอม
	public int countStudentsByTerm(String term) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			// ✅ แก้ไข: term/semester อยู่ใน Project
			String hql = "SELECT COUNT(s) FROM Student496 s " + "JOIN s.project p " + "WHERE p.semester = :term";

			Long count = session.createQuery(hql, Long.class).setParameter("term", term).uniqueResult();

			return count != null ? count.intValue() : 0;

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	// ✅ เพิ่ม: ดึงเทอมทั้งหมดที่มีในระบบ
	public List<String> getAllTerms() {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			// ✅ ดึงจาก Project.semester
			String hql = "SELECT DISTINCT p.semester FROM Project p ORDER BY p.semester DESC";

			return session.createQuery(hql, String.class).list();

		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}
}