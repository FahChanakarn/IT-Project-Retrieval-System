package com.springmvc.manager;

import com.springmvc.model.HibernateConnection;
import com.springmvc.model.TypeDB;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class TypeDBManager {

	public List<TypeDB> getAllTypeDBs() {
		Session session = null;
		List<TypeDB> typeDBs = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			String hql = "FROM TypeDB";
			Query<TypeDB> query = session.createQuery(hql, TypeDB.class);
			typeDBs = query.list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
		return typeDBs;
	}

	public TypeDB findTypeDBById(int id) {
		Session session = null;
		TypeDB typeDB = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			typeDB = session.get(TypeDB.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
		return typeDB;
	}
}
