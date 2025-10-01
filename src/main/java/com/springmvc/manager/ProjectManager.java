package com.springmvc.manager;

import com.springmvc.model.DocumentFile;
import com.springmvc.model.HibernateConnection;
import com.springmvc.model.Project;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ProjectManager {

	public List<String> getAllSemesters() {
		List<String> semesters = null;
		Session session = null;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT DISTINCT semester FROM Project ORDER BY semester DESC";
			semesters = session.createQuery(hql, String.class).getResultList();

		} catch (Exception ex) {
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
			if (keyword == null) {
				keyword = "";
			}
			keyword = keyword.toLowerCase().trim();

			String hql = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.student496s s "
					+ "LEFT JOIN FETCH p.advisor a " + "WHERE LOWER(p.proj_NameTh) LIKE :kw "
					+ "OR LOWER(p.proj_NameEn) LIKE :kw " + "OR LOWER(p.keywordTh) LIKE :kw "
					+ "OR LOWER(p.keywordEn) LIKE :kw "
					+ "OR LOWER(CONCAT(a.adv_prefix, ' ', a.adv_firstName, ' ', a.adv_lastName)) LIKE :kw "
					+ "OR LOWER(CONCAT(s.stu_prefix, ' ', s.stu_firstName, ' ', s.stu_lastName)) LIKE :kw";

			var query = session.createQuery(hql, Project.class);
			query.setParameter("kw", "%" + keyword + "%"); // ใช้ keyword ที่เช็คแล้ว

			projects = query.getResultList();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		return projects;
	}

	public List<Project> filterProjects(String projectType, List<String> advisorIds, List<String> semesters,
			List<String> languages, List<String> databases, String testingStatus, String startYear, String endYear) {

		List<Project> projects = null;
		Session session = null;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

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
			if (languages != null && !languages.isEmpty()) {
				hql.append(" AND lang.langName IN (:languages) AND lang.langType = 'PROGRAMMING'");
			}
			if (databases != null && !databases.isEmpty()) {
				hql.append(" AND lang.langName IN (:databases) AND lang.langType = 'DBMS'");
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
			if (languages != null && !languages.isEmpty()) {
				query.setParameter("languages", languages);
			}
			if (databases != null && !databases.isEmpty()) {
				query.setParameter("databases", databases);
			}
			if (testingStatus != null && !testingStatus.isEmpty()) {
				query.setParameter("testingStatus", testingStatus);
			}
			if (startYear != null && !startYear.isEmpty() && endYear != null && !endYear.isEmpty()) {
				query.setParameter("startYear", startYear);
				query.setParameter("endYear", endYear);
			}

			projects = query.getResultList();

		} catch (Exception ex) {
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

			String hql = "SELECT p FROM Project p " + "LEFT JOIN FETCH p.student496s "
					+ "WHERE p.projectId = :projectId";

			project = session.createQuery(hql, Project.class).setParameter("projectId", projectId).uniqueResult();

			if (project != null) {
				project.getProjectLangDetails().size();
				project.getDocumentFiles().size();
			}

		} catch (Exception e) {
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
		Session session = null;
		List<Project> projects = new ArrayList<>();
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.student496s "
					+ "WHERE p.semester = :semester ORDER BY p.projectId ASC";

			projects = session.createQuery(hql, Project.class).setParameter("semester", semester).getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return projects;
	}

	public String getLatestSemester() {
		String latestSemester = null;
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			latestSemester = (String) session
					.createQuery("SELECT DISTINCT semester FROM Project ORDER BY semester DESC").setMaxResults(1)
					.uniqueResult();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return latestSemester != null ? latestSemester : "2/2567";
	}

	public List<Object[]> getStudentProjectsByAdvisorAndSemester(String advisorId, String semester, int offset,
			int limit) {
		List<Object[]> results = null;
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT s.stuId, s.stu_firstName, s.stu_lastName, p.proj_NameTh, p.projectId, p.approveStatus "
					+ "FROM Student496 s JOIN s.project p "
					+ "WHERE p.advisor.advisorId = :advisorId AND p.semester = :semester";

			results = session.createQuery(hql, Object[].class).setParameter("advisorId", advisorId)
					.setParameter("semester", semester).setFirstResult(offset).setMaxResults(limit).list();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return results;
	}

	public int countProjectsByAdvisorAndSemester(String advisorId, String semester) {
		int count = 0;
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT COUNT(*) FROM Student496 s JOIN s.project p "
					+ "WHERE p.advisor.advisorId = :advisorId AND p.semester = :semester";

			Long result = (Long) session.createQuery(hql).setParameter("advisorId", advisorId)
					.setParameter("semester", semester).uniqueResult();
			count = result != null ? result.intValue() : 0;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return count;
	}

	public Project getProjectDetail(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.advisor a "
					+ "LEFT JOIN FETCH p.student496s s " + "WHERE p.projectId = :pid";

			Project project = session.createQuery(hql, Project.class).setParameter("pid", projectId).uniqueResult();

			return project;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public Project getProjectWithFiles(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.documentFiles df "
					+ "WHERE p.projectId = :pid";

			Project p = session.createQuery(hql, Project.class).setParameter("pid", projectId).uniqueResult();

			if (p != null && p.getDocumentFiles() != null) {
				p.getDocumentFiles().size();
			}

			return p;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public DocumentFile findFirstVideoDoc(int projectId, boolean onlyPublished) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			StringBuilder hql = new StringBuilder("SELECT df FROM DocumentFile df "
					+ "WHERE df.project.projectId = :pid " + "AND df.filetype = :vtype ");

			if (onlyPublished) {
				hql.append("AND df.status = :pub ");
			}

			hql.append("ORDER BY df.fileno ASC, df.fileId ASC");

			var q = session.createQuery(hql.toString(), DocumentFile.class).setParameter("pid", projectId)
					.setParameter("vtype", "video");

			if (onlyPublished) {
				q.setParameter("pub", "Published");
			}

			q.setMaxResults(1);
			DocumentFile video = q.uniqueResult();

			return video;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public List<Object[]> getAllStudentProjectsBySemester(String semester, int offset, int limit) {
		List<Object[]> results = new ArrayList<>();
		Session session = null;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT s.stuId, s.stu_firstName, s.stu_lastName, p.proj_NameTh, p.projectId "
					+ "FROM Student496 s JOIN s.project p " + "WHERE p.semester = :semester";

			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("semester", semester);
			query.setFirstResult(offset);
			query.setMaxResults(limit);

			results = query.list();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		return results;
	}

	public int countAllProjectsBySemester(String semester) {
		Session session = null;
		int count = 0;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT COUNT(p) FROM Project p WHERE p.semester = :semester";

			Long result = (Long) session.createQuery(hql).setParameter("semester", semester).uniqueResult();
			count = result != null ? result.intValue() : 0;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		return count;
	}

	public List<DocumentFile> getPublishedFilesByProjectId(int projectId) {
		List<DocumentFile> files = new ArrayList<>();
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			// ใช้ publishStatus = 1
			String hql = "FROM DocumentFile df WHERE df.project.projectId = :pid AND df.publishStatus = 1 ORDER BY df.fileno ASC";
			Query<DocumentFile> query = session.createQuery(hql, DocumentFile.class);
			query.setParameter("pid", projectId);
			files = query.list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return files;
	}

	public Project findProjectForEditAbstract(int projectId) {
		Project project = null;
		Session session = null;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			// ลบ LEFT JOIN FETCH p.typeDB
			String hql1 = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.student496s "
					+ "WHERE p.projectId = :projectId";

			project = session.createQuery(hql1, Project.class).setParameter("projectId", projectId).setMaxResults(1)
					.uniqueResult();

			if (project != null) {
				// ดึง projectLangDetails
				String hql2 = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.projectLangDetails pld "
						+ "LEFT JOIN FETCH pld.programmingLang " + "WHERE p.projectId = :projectId";
				session.createQuery(hql2, Project.class).setParameter("projectId", projectId).uniqueResult();

				// ดึง documentFiles
				String hql3 = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.documentFiles "
						+ "WHERE p.projectId = :projectId";
				session.createQuery(hql3, Project.class).setParameter("projectId", projectId).uniqueResult();

				// Force initialize collections
				project.getStudent496s().size();
				project.getProjectLangDetails().size();
				project.getDocumentFiles().size();

				System.out.println("✅ Loaded Project: ID=" + project.getProjectId() + ", NameTH="
						+ project.getProj_NameTh() + ", Students=" + project.getStudent496s().size() + ", LangDetails="
						+ project.getProjectLangDetails().size() + ", Files=" + project.getDocumentFiles().size());
			} else {
				System.out.println("⚠️ Project not found with ID = " + projectId);
			}

		} catch (Exception e) {
			System.err.println("❌ Error in findProjectForEditAbstract: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		return project;
	}

	public boolean hasStudentEditedAbstract(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT COUNT(p) FROM Project p WHERE p.projectId = :projectId "
					+ "AND (p.abstractTh IS NOT NULL AND TRIM(p.abstractTh) != '' "
					+ "OR p.abstractEn IS NOT NULL AND TRIM(p.abstractEn) != '')";

			Long count = (Long) session.createQuery(hql).setParameter("projectId", projectId).uniqueResult();

			return count != null && count > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return true; // ถ้าเกิดข้อผิดพลาด ให้ถือว่าแก้ไขแล้ว (เพื่อความปลอดภัย)
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public boolean deleteProjectAndStudents(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			// 1. ลบ ProjectLangDetail ก่อน (เนื่องจากมี foreign key)
			String deleteLangDetailHql = "DELETE FROM ProjectLangDetail pld WHERE pld.project.projectId = :projectId";
			session.createQuery(deleteLangDetailHql).setParameter("projectId", projectId).executeUpdate();

			// 2. ลบ DocumentFile
			String deleteDocumentHql = "DELETE FROM DocumentFile df WHERE df.project.projectId = :projectId";
			session.createQuery(deleteDocumentHql).setParameter("projectId", projectId).executeUpdate();

			// 3. ลบ Student496 (นักศึกษาที่เกี่ยวข้องกับโครงงาน)
			String deleteStudentHql = "DELETE FROM Student496 s WHERE s.project.projectId = :projectId";
			session.createQuery(deleteStudentHql).setParameter("projectId", projectId).executeUpdate();

			// 4. ลบ Project สุดท้าย
			String deleteProjectHql = "DELETE FROM Project p WHERE p.projectId = :projectId";
			session.createQuery(deleteProjectHql).setParameter("projectId", projectId).executeUpdate();

			session.getTransaction().commit();
			return true;

		} catch (Exception e) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
			return false;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public boolean projectExists(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			Long count = (Long) session.createQuery("SELECT COUNT(p) FROM Project p WHERE p.projectId = :projectId")
					.setParameter("projectId", projectId).uniqueResult();

			return count != null && count > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public List<Project> getAllProjects() {
		List<Project> projects = null;
		Session session = null;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.student496s s "
					+ "LEFT JOIN FETCH p.advisor a " + "ORDER BY p.projectId DESC";

			projects = session.createQuery(hql, Project.class).getResultList();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		return projects != null ? projects : new ArrayList<>();
	}
}
