package com.springmvc.manager;

import com.springmvc.model.HibernateConnection;
import com.springmvc.model.ProgrammingLang;
import com.springmvc.model.ProgrammingLang.LangType;
import com.springmvc.model.Project;
import com.springmvc.model.ProjectLangDetail;
import com.springmvc.model.ProjectLangDetailId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProgrammingLangManager {

	public List<ProgrammingLang> getAllProgrammingLanguages() {
		Session session = null;
		List<ProgrammingLang> langs = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			String hql = "FROM ProgrammingLang";
			Query<ProgrammingLang> query = session.createQuery(hql, ProgrammingLang.class);
			langs = query.list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return langs;
	}

	public List<ProgrammingLang> getLanguagesByType(LangType langType) {
		List<ProgrammingLang> languages = new ArrayList<>();
		Session session = null;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "FROM ProgrammingLang WHERE langType = :type ORDER BY langName ASC";
			languages = session.createQuery(hql, ProgrammingLang.class).setParameter("type", langType).getResultList();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		return languages;
	}

	public ProgrammingLang findProgrammingLangById(int id) {
		Session session = null;
		ProgrammingLang lang = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			lang = session.get(ProgrammingLang.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return lang;
	}

	public ProjectLangDetail createProjectLangDetail(Project project, int langId) {
		ProgrammingLang lang = findProgrammingLangById(langId);
		if (lang == null)
			return null;

		ProjectLangDetailId id = new ProjectLangDetailId(project.getProjectId(), lang.getLangId());

		ProjectLangDetail detail = new ProjectLangDetail();
		detail.setId(id);
		detail.setProject(project);
		detail.setProgrammingLang(lang);
		detail.setDescription("");

		return detail;
	}

	public void addOtherLanguageToProject(Project project, String langName) {
		Session session = null;
		Transaction tx = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			Query<ProgrammingLang> query = session.createQuery("FROM ProgrammingLang WHERE langName = :name",
					ProgrammingLang.class);
			query.setParameter("name", langName);
			ProgrammingLang lang = query.uniqueResult();

			if (lang == null) {
				lang = new ProgrammingLang();
				lang.setLangName(langName);
				lang.setLangType(ProgrammingLang.LangType.PROGRAMMING);
				session.save(lang);
				session.flush();
			}

			ProjectLangDetailId id = new ProjectLangDetailId(project.getProjectId(), lang.getLangId());
			ProjectLangDetail existing = session.get(ProjectLangDetail.class, id);

			if (existing == null) {
				ProjectLangDetail detail = new ProjectLangDetail();
				detail.setId(id);
				detail.setProject(project);
				detail.setProgrammingLang(lang);
				detail.setDescription("");
				session.save(detail);
			}

			tx.commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public boolean existsProjectLangDetail(int projectId, int langId) {
		Session session = null;
		boolean exists = false;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			Query<Long> query = session.createQuery(
					"SELECT COUNT(*) FROM ProjectLangDetail WHERE id.projectId = :pid AND id.langId = :lid",
					Long.class);
			query.setParameter("pid", projectId);
			query.setParameter("lid", langId);

			Long count = query.uniqueResult();
			exists = (count != null && count > 0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return exists;
	}

	public void createProjectLangDetailAndSave(Project project, int langId) {
		Session session = null;
		Transaction tx = null;
		try {
			if (existsProjectLangDetail(project.getProjectId(), langId)) {
				return;
			}

			ProgrammingLang lang = findProgrammingLangById(langId);
			if (lang == null)
				return;

			ProjectLangDetailId id = new ProjectLangDetailId(project.getProjectId(), lang.getLangId());

			ProjectLangDetail detail = new ProjectLangDetail();
			detail.setId(id);
			detail.setProject(project);
			detail.setProgrammingLang(lang);
			detail.setDescription("");

			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			session.save(detail);
			tx.commit();

		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	// ✅ Method ใหม่: ลบภาษาที่ไม่ได้เลือก
	public void removeUnselectedLanguages(int projectId, Set<Integer> selectedLangIds) {
		Session session = null;
		Transaction tx = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			// ดึงภาษาทั้งหมดที่ project นี้มี (เฉพาะ PROGRAMMING และ DBMS)
			String hql = "FROM ProjectLangDetail pld " + "WHERE pld.id.projectId = :projectId "
					+ "AND pld.programmingLang.langType IN ('PROGRAMMING', 'DBMS')";

			List<ProjectLangDetail> allDetails = session.createQuery(hql, ProjectLangDetail.class)
					.setParameter("projectId", projectId).getResultList();

			// ลบภาษาที่ไม่ได้อยู่ใน selectedLangIds
			for (ProjectLangDetail detail : allDetails) {
				int langId = detail.getProgrammingLang().getLangId();
				if (!selectedLangIds.contains(langId)) {
					session.delete(detail);
				}
			}

			tx.commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}
}