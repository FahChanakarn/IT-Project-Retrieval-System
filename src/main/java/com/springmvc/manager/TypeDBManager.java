package com.springmvc.manager;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.springmvc.model.HibernateConnection;
import com.springmvc.model.TypeDB;

public class TypeDBManager {

	public List<TypeDB> getAllTypeDBs() {
        List<TypeDB> typeDBs = null;
        try {
            SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            String hql = "FROM TypeDB";
            typeDBs = session.createQuery(hql, TypeDB.class).getResultList();

            session.getTransaction().commit();
            session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return typeDBs;
    }

}
