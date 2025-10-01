package com.springmvc.manager;

import com.springmvc.model.HibernateConnection;
import com.springmvc.model.ProgrammingLang;
import com.springmvc.model.Project;
import com.springmvc.model.ProjectLangDetail;
import com.springmvc.model.ProjectLangDetailId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ProgrammingLangManager {

	// ดึงข้อมูลภาษาทั้งหมด
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
			if (session != null)
				session.close();
		}
		return langs;
	}

	public List<ProgrammingLang> getLanguagesByType(ProgrammingLang.LangType type) {
		Session session = null;
		List<ProgrammingLang> langs = new ArrayList<>();
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			if (type != null) {
				langs = session.createQuery("FROM ProgrammingLang WHERE langType = :type", ProgrammingLang.class)
						.setParameter("type", type).getResultList();
			} else {
				langs = session.createQuery("FROM ProgrammingLang", ProgrammingLang.class).getResultList();
			}
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return langs;
	}

	// ดึงข้อมูลภาษา ตาม id
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
			if (session != null)
				session.close();
		}
		return lang;
	}

	// สร้าง ProjectLangDetail ใหม่ (สำหรับ Many-to-Many)
	public ProjectLangDetail createProjectLangDetail(Project project, int langId) {
		ProgrammingLang lang = findProgrammingLangById(langId);
		if (lang == null)
			return null;

		ProjectLangDetailId id = new ProjectLangDetailId(project.getProjectId(), lang.getLangId());

		ProjectLangDetail detail = new ProjectLangDetail();
		detail.setId(id);
		detail.setProject(project);
		detail.setProgrammingLang(lang);
		detail.setDescription(""); // ถ้าอยากใส่รายละเอียดเพิ่ม สามารถแก้ไขได้

		return detail;
	}

	// ✅ เพิ่มภาษาที่กรอกเอง (otherLanguages) เข้า Project
	public void addOtherLanguageToProject(Project project, String langName) {
		Session session = null;
		Transaction tx = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			// ตรวจสอบว่าภาษามีอยู่แล้วในฐานข้อมูลหรือยัง
			Query<ProgrammingLang> query = session.createQuery("FROM ProgrammingLang WHERE langName = :name",
					ProgrammingLang.class);
			query.setParameter("name", langName);
			ProgrammingLang lang = query.uniqueResult();

			if (lang == null) {
				// สร้าง ProgrammingLang ใหม่
				lang = new ProgrammingLang();
				lang.setLangName(langName);
				lang.setLangType(ProgrammingLang.LangType.PROGRAMMING);
				session.save(lang);
			}

			// สร้าง ProjectLangDetail และเพิ่มเข้า project
			ProjectLangDetailId id = new ProjectLangDetailId(project.getProjectId(), lang.getLangId());
			ProjectLangDetail detail = new ProjectLangDetail();
			detail.setId(id);
			detail.setProject(project);
			detail.setProgrammingLang(lang);
			detail.setDescription("");

			project.getProjectLangDetails().add(detail);

			session.update(project);
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
	}

	// ตรวจสอบว่าภาษาใน project ถูกบันทึกแล้วหรือยัง
	public boolean existsProjectLangDetail(int projectId, int langId) {
		Session session = null;
		boolean exists = false;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			Query<ProjectLangDetail> query = session.createQuery(
					"FROM ProjectLangDetail WHERE id.projectId = :pid AND id.langId = :lid", ProjectLangDetail.class);
			query.setParameter("pid", projectId);
			query.setParameter("lid", langId);
			exists = query.uniqueResult() != null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
		return exists;
	}

	// สร้าง ProjectLangDetail ใหม่และบันทึกลง DB
	public void createProjectLangDetailAndSave(Project project, int langId) {
		Session session = null;
		Transaction tx = null;
		try {
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

			session.save(detail); // บันทึกลง DB
			tx.commit();

			// เพิ่มเข้า project object ด้วย (เพื่อใช้ JSP)
			project.getProjectLangDetails().add(detail);

		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
	}
}
