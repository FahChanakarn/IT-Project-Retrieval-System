package com.springmvc.manager;

import com.springmvc.model.HibernateConnection;
import com.springmvc.model.Project;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ProjectManager {

	public List<String> getAllProjectTypes() {
		List<String> projectTypes = null;
		Session session = null;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			String hql = "SELECT DISTINCT projectType FROM Project";
			projectTypes = session.createQuery(hql, String.class).getResultList();

			session.getTransaction().commit();
		} catch (Exception ex) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return projectTypes;
	}

	public List<String> getAllSemesters() {
		List<String> semesters = null;
		Session session = null;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			String hql = "SELECT DISTINCT semester FROM Project ORDER BY semester DESC";
			semesters = session.createQuery(hql, String.class).getResultList();

			session.getTransaction().commit();
		} catch (Exception ex) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return semesters;
	}

	public List<Project> searchProjects(String keyword) {
		List<Project> projects = null;
		Session session = null;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			String hql = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.student496s s "
					+ "LEFT JOIN FETCH p.advisor a " + "WHERE " + "LOWER(p.proj_NameTh) LIKE :kw "
					+ "OR LOWER(p.proj_NameEn) LIKE :kw " + "OR LOWER(p.keywordTh) LIKE :kw "
					+ "OR LOWER(p.keywordEn) LIKE :kw "
					+ "OR LOWER(CONCAT(a.adv_prefix, ' ', a.adv_firstName, ' ', a.adv_lastName)) LIKE :kw "
					+ "OR LOWER(CONCAT(s.stu_prefix, ' ', s.stu_firstName, ' ', s.stu_lastName)) LIKE :kw";

			var query = session.createQuery(hql, Project.class);
			query.setParameter("kw", "%" + keyword.toLowerCase().trim() + "%");

			projects = query.getResultList();
			session.getTransaction().commit();
		} catch (Exception ex) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		return projects;
	}

	public List<Project> filterProjects(String projectType, List<String> advisorIds, List<String> semesters,
			List<Integer> typeDBIds, List<String> languages, String testingStatus, String startYear, String endYear) {

		List<Project> projects = null;
		Session session = null;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			StringBuilder hql = new StringBuilder("SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.student496s "
					+ "LEFT JOIN p.projectLangDetails pld " + "LEFT JOIN pld.programmingLang lang " + "WHERE 1=1");

			if (projectType != null && !projectType.isEmpty()) {
				hql.append(" AND p.projectType = :projectType");
			}
			if (advisorIds != null && !advisorIds.isEmpty()) {
				hql.append(" AND p.advisor.advisorId IN (:advisorIds)");
			}
			if (semesters != null && !semesters.isEmpty()) {
				hql.append(" AND p.semester IN (:semesters)");
			}
			if (typeDBIds != null && !typeDBIds.isEmpty()) {
				hql.append(" AND p.typeDB.typeId IN (:typeDBIds)");
			}
			if (languages != null && !languages.isEmpty()) {
				hql.append(" AND lang.langName IN (:languages)");
			}
			if (testingStatus != null && !testingStatus.isEmpty()) {
				hql.append(" AND p.testing_status = :testingStatus");
			}
			if (startYear != null && !startYear.isEmpty() && endYear != null && !endYear.isEmpty()) {
				hql.append(
						" AND FUNCTION('substring', p.semester, LOCATE('/', p.semester) + 1) BETWEEN :startYear AND :endYear");
			}

			var query = session.createQuery(hql.toString(), Project.class);

			if (projectType != null && !projectType.isEmpty()) {
				query.setParameter("projectType", projectType);
			}
			if (advisorIds != null && !advisorIds.isEmpty()) {
				query.setParameter("advisorIds", advisorIds);
			}
			if (semesters != null && !semesters.isEmpty()) {
				query.setParameterList("semesters", semesters);
			}
			if (typeDBIds != null && !typeDBIds.isEmpty()) {
				query.setParameter("typeDBIds", typeDBIds);
			}
			if (languages != null && !languages.isEmpty()) {
				query.setParameter("languages", languages);
			}
			if (testingStatus != null && !testingStatus.isEmpty()) {
				query.setParameter("testingStatus", testingStatus);
			}

			if (startYear != null && !startYear.isEmpty() && endYear != null && !endYear.isEmpty()) {
				query.setParameter("startYear", startYear);
				query.setParameter("endYear", endYear);
			}

			projects = query.getResultList();
			session.getTransaction().commit();
		} catch (Exception ex) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		return projects;
	}

	public Project findProjectById(int projectId) {
		Project project = null;
		Session session = null;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			String hql = "SELECT p FROM Project p " + "LEFT JOIN FETCH p.student496s "
					+ "WHERE p.projectId = :projectId";
			project = session.createQuery(hql, Project.class).setParameter("projectId", projectId).uniqueResult();

			if (project != null) {
				project.getProjectLangDetails().size(); // force load
			}

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
		return project;
	}

	public void updateProject(Project project) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();
			session.update(project);
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

	public List<Project> getProjectsBySemester(String semester) {
		List<Project> projectList = new ArrayList<>();
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			Session session = sessionFactory.openSession();
			session.beginTransaction();

			Query<Project> query = session.createQuery("FROM Project WHERE semester = :semester", Project.class);
			query.setParameter("semester", semester);
			projectList = query.list();

			session.getTransaction().commit();
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projectList;
	}

	public String getLatestSemester() {
		String latestSemester = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			Session session = sessionFactory.openSession();

			latestSemester = (String) session
					.createQuery("SELECT DISTINCT semester FROM Project ORDER BY semester DESC").setMaxResults(1)
					.uniqueResult();

			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return latestSemester != null ? latestSemester : "2/2567"; // fallback เผื่อว่าง
	}

}
