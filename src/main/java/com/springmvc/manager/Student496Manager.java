package com.springmvc.manager;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.springmvc.model.HibernateConnection;
import com.springmvc.model.Student496;

public class Student496Manager {

	public List<Student496> getAllStudents() {
		Session session = null;
		try {
			session = HibernateConnection.doHibernateConnection().openSession();
			List<Student496> list = session.createQuery("FROM Student496", Student496.class).list();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public Student496 findById(String stuId) {
		Session session = null;
		try {
			session = HibernateConnection.doHibernateConnection().openSession();
			Student496 student = session.get(Student496.class, stuId);
			return student;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public Student496 findByStuId(String stuId) {
		return findById(stuId);
	}

	@Deprecated
	public Student496 findByStuIdAndPassword(String stuId, String password) {
		Session session = null;
		try {
			session = HibernateConnection.doHibernateConnection().openSession();
			Query<Student496> query = session
					.createQuery("FROM Student496 WHERE stuId = :stuId AND stu_password = :password", Student496.class);
			query.setParameter("stuId", stuId);
			query.setParameter("password", password);
			Student496 student = query.uniqueResult();
			return student;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public void updateStudent(Student496 student) {
		Session session = null;
		try {
			session = HibernateConnection.doHibernateConnection().openSession();
			session.beginTransaction();
			session.update(student);
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

	public boolean updateStudentSafe(Student496 student) {
		Session session = null;
		try {
			session = HibernateConnection.doHibernateConnection().openSession();
			session.beginTransaction();
			session.update(student);
			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
			return false;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public List<Student496> getStudentsBySemester(String semester) {
		Session session = null;
		try {
			session = HibernateConnection.doHibernateConnection().openSession();

			Query<Student496> query = session.createQuery(
					"FROM Student496 s JOIN FETCH s.project p WHERE p.semester = :semester", Student496.class);
			query.setParameter("semester", semester);

			List<Student496> list = query.list();

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
}