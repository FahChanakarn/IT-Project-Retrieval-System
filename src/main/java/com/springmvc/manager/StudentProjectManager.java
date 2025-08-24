package com.springmvc.manager;

import com.springmvc.model.Project;
import com.springmvc.model.Student;
import com.springmvc.model.HibernateConnection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class StudentProjectManager {

	public List<Object[]> getStudentProjectsByTerm(String term) {
		SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
		Session session = sessionFactory.openSession();

		String hql = "SELECT s, p FROM Student s JOIN Project p ON s.projectId = p.projectId WHERE s.term = :term ORDER BY s.stuId ASC";
		Query<Object[]> query = session.createQuery(hql, Object[].class);
		query.setParameter("term", term);

		List<Object[]> result = query.list();
		session.close();
		return result;
	}
}
