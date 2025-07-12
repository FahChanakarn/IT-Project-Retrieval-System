package com.springmvc.manager;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.springmvc.model.HibernateConnection;
import com.springmvc.model.ProgrammingLang;

public class ProgrammingLangManager {

	public List<ProgrammingLang> getAllProgrammingLanguages() {
        List<ProgrammingLang> programmingLangs = null;
        try {
            SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Query ข้อมูลจาก ProgrammingLang
            String hql = "FROM ProgrammingLang";
            programmingLangs = session.createQuery(hql, ProgrammingLang.class).getResultList();

            session.getTransaction().commit();
            session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return programmingLangs;
    }

}
