package com.springmvc.manager;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.springmvc.model.HibernateConnection;
import com.springmvc.model.Student;

public class StudentManager {

	public List<Student> getAllStudents() {
		Session session = HibernateConnection.doHibernateConnection().openSession();
		List<Student> list = session.createQuery("FROM Student", Student.class).list();
		session.close();
		return list;
	}

	public Student findById(String stuId) {
		Session session = HibernateConnection.doHibernateConnection().openSession();
		Student student = session.get(Student.class, stuId);
		session.close();
		return student;
	}

	public Student findByStuIdAndPassword(String stuId, String password) {
		Session session = HibernateConnection.doHibernateConnection().openSession();
		Query<Student> query = session
				.createQuery("FROM Student WHERE stuId = :stuId AND stu_password = :password", Student.class);
		query.setParameter("stuId", stuId);
		query.setParameter("password", password);

		Student student = query.uniqueResult();
		session.close();
		return student;
	}
}
