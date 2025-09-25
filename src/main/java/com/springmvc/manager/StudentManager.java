package com.springmvc.manager;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.springmvc.model.HibernateConnection;
import com.springmvc.model.Student;

public class StudentManager {

	public List<Student> getStudentsWithoutProject() {
		Transaction tx = null;
		try (Session session = HibernateConnection.doHibernateConnection().openSession()) {
			tx = session.beginTransaction();

			List<Student> students = session.createQuery(
					"FROM Student s WHERE s.stuId NOT IN (SELECT s496.stuId FROM Student496 s496) ORDER BY s.stuId",
					Student.class).list();

			tx.commit();
			return students;
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			return null;
		}
	}

	public Student findById(String stuId) {
		Transaction tx = null;
		try (Session session = HibernateConnection.doHibernateConnection().openSession()) {
			tx = session.beginTransaction();

			Student student = session.get(Student.class, stuId);

			tx.commit();
			return student;
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			return null;
		}
	}

	public Student findByStuIdAndPassword(String stuId, String password) {
		System.out.println("Attempt login: " + stuId + " / " + password);

		try (Session session = HibernateConnection.doHibernateConnection().openSession()) {
			Query<Student> query = session.createQuery("FROM Student WHERE stuId = :stuId AND stu_password = :password",
					Student.class);
			query.setParameter("stuId", stuId);
			query.setParameter("password", password);

			List<Student> list = query.list();
			if (!list.isEmpty())
				return list.get(0);
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
