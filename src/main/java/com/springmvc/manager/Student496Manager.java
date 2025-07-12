package com.springmvc.manager;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.springmvc.model.HibernateConnection;
import com.springmvc.model.Student496;

public class Student496Manager {

	public List<Student496> getAllStudents() {
		Session session = HibernateConnection.doHibernateConnection().openSession();
		List<Student496> list = session.createQuery("FROM Student496", Student496.class).list();
		session.close();
		return list;
	}

	public Student496 findById(String stuId) {
		Session session = HibernateConnection.doHibernateConnection().openSession();
		Student496 student = session.get(Student496.class, stuId);
		session.close();
		return student;
	}

	public Student496 findByStuIdAndPassword(String stuId, String password) {
		Session session = HibernateConnection.doHibernateConnection().openSession();
		Query<Student496> query = session
				.createQuery("FROM Student496 WHERE stuId = :stuId AND stu_password = :password", Student496.class);
		query.setParameter("stuId", stuId);
		query.setParameter("password", password);

		Student496 student = query.uniqueResult();
		session.close();
		return student;
	}

	public void updateStudent(Student496 student) {
		Session session = HibernateConnection.doHibernateConnection().openSession();
		session.beginTransaction();
		session.update(student);
		session.getTransaction().commit();
		session.close();
	}

}
