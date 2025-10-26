package com.springmvc.manager;

import com.springmvc.model.DocumentFile;
import com.springmvc.model.HibernateConnection;
import com.springmvc.model.Project;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProjectManager {

	public List<String> getAllSemesters() {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT DISTINCT semester FROM Project ORDER BY semester DESC";
			return session.createQuery(hql, String.class).getResultList();

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public List<Project> searchProjects(String keyword) {
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
			query.setParameter("kw", "%" + keyword + "%");

			return query.getResultList();

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public List<Project> filterProjects(String projectType, List<String> advisorIds, List<String> semesters,
			List<String> languages, List<String> testingTools, List<String> databases, String testingStatus,
			String startYear, String endYear) {

		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			StringBuilder hql = new StringBuilder("SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.student496s "
					+ "LEFT JOIN p.tools t " + "WHERE 1=1");

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
				hql.append(
						" AND EXISTS (SELECT 1 FROM Project p2 JOIN p2.tools t2 WHERE p2.projectId = p.projectId AND t2.toolsName IN (:languages) AND t2.toolType = 'PROGRAMMING')");
			}
			// ✅ เพิ่มเงื่อนไขสำหรับ testingTools
			if (testingTools != null && !testingTools.isEmpty()) {
				hql.append(
						" AND EXISTS (SELECT 1 FROM Project p3 JOIN p3.tools t3 WHERE p3.projectId = p.projectId AND t3.toolsName IN (:testingTools) AND t3.toolType = 'Testing')");
			}
			if (databases != null && !databases.isEmpty()) {
				hql.append(
						" AND EXISTS (SELECT 1 FROM Project p4 JOIN p4.tools t4 WHERE p4.projectId = p.projectId AND t4.toolsName IN (:databases) AND t4.toolType = 'DBMS')");
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
				query.setParameterList("languages", languages);
			}
			// ✅ เพิ่ม parameter binding สำหรับ testingTools
			if (testingTools != null && !testingTools.isEmpty()) {
				query.setParameterList("testingTools", testingTools);
			}
			if (databases != null && !databases.isEmpty()) {
				query.setParameterList("databases", databases);
			}
			if (testingStatus != null && !testingStatus.isEmpty()) {
				query.setParameter("testingStatus", testingStatus);
			}
			if (startYear != null && !startYear.isEmpty() && endYear != null && !endYear.isEmpty()) {
				query.setParameter("startYear", startYear);
				query.setParameter("endYear", endYear);
			}

			return query.getResultList();

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public Project findProjectById(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			// Query 1: Project + Advisor + Students
			String hql1 = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.advisor "
					+ "LEFT JOIN FETCH p.student496s " + "WHERE p.projectId = :projectId";

			Project project = session.createQuery(hql1, Project.class).setParameter("projectId", projectId)
					.uniqueResult();

			if (project != null) {
				// Query 2: Tools
				String hql2 = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.tools "
						+ "WHERE p.projectId = :projectId";
				session.createQuery(hql2, Project.class).setParameter("projectId", projectId).uniqueResult();

				// Query 3: DocumentFiles
				String hql3 = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.documentFiles "
						+ "WHERE p.projectId = :projectId";
				session.createQuery(hql3, Project.class).setParameter("projectId", projectId).uniqueResult();

				// Force initialize
				project.getStudent496s().size();
				project.getTools().size();
				project.getDocumentFiles().size();
			}

			return project;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
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
			if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public List<Project> getProjectsBySemester(String semester) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.student496s "
					+ "WHERE p.semester = :semester ORDER BY p.projectId ASC";

			return session.createQuery(hql, Project.class).setParameter("semester", semester).getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public String getLatestSemester() {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String latestSemester = (String) session
					.createQuery("SELECT DISTINCT semester FROM Project ORDER BY semester DESC").setMaxResults(1)
					.uniqueResult();

			return latestSemester != null ? latestSemester : "2/2567";

		} catch (Exception e) {
			e.printStackTrace();
			return "2/2567";
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public List<Object[]> getStudentProjectsByAdvisorAndSemester(String advisorId, String semester, int offset,
			int limit) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT s.stuId, s.stu_firstName, s.stu_lastName, p.proj_NameTh, p.projectId, p.approveStatus "
					+ "FROM Student496 s JOIN s.project p "
					+ "WHERE p.advisor.advisorId = :advisorId AND p.semester = :semester";

			return session.createQuery(hql, Object[].class).setParameter("advisorId", advisorId)
					.setParameter("semester", semester).setFirstResult(offset).setMaxResults(limit).list();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public int countProjectsByAdvisorAndSemester(String advisorId, String semester) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT COUNT(*) FROM Student496 s JOIN s.project p "
					+ "WHERE p.advisor.advisorId = :advisorId AND p.semester = :semester";

			Long result = (Long) session.createQuery(hql).setParameter("advisorId", advisorId)
					.setParameter("semester", semester).uniqueResult();

			return result != null ? result.intValue() : 0;

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public Project getProjectDetail(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			// Query 1: ดึง Project + Advisor + Students
			String hql1 = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.advisor "
					+ "LEFT JOIN FETCH p.student496s " + "WHERE p.projectId = :pid";

			Project project = session.createQuery(hql1, Project.class).setParameter("pid", projectId).uniqueResult();

			if (project != null) {
				// Query 2: ดึง Tools แยกต่างหาก
				String hql2 = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.tools "
						+ "WHERE p.projectId = :pid";
				session.createQuery(hql2, Project.class).setParameter("pid", projectId).uniqueResult();

				// Force initialize
				project.getStudent496s().size();
				project.getTools().size();

				System.out.println("✅ getProjectDetail: Students=" + project.getStudent496s().size() + ", Tools="
						+ project.getTools().size());
			}

			return project;

		} catch (Exception ex) {
			System.err.println("❌ Error in getProjectDetail: " + ex.getMessage());
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
			if (session != null) {
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
			return q.uniqueResult();

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public List<Object[]> getAllStudentProjectsBySemester(String semester, int offset, int limit) {
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

			return query.list();

		} catch (Exception ex) {
			ex.printStackTrace();
			return new ArrayList<>();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public int countAllProjectsBySemester(String semester) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT COUNT(p) FROM Project p WHERE p.semester = :semester";

			Long result = (Long) session.createQuery(hql).setParameter("semester", semester).uniqueResult();

			return result != null ? result.intValue() : 0;

		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public List<DocumentFile> getPublishedFilesByProjectId(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "FROM DocumentFile df WHERE df.project.projectId = :pid AND df.publishStatus = 1 ORDER BY df.fileno ASC";
			Query<DocumentFile> query = session.createQuery(hql, DocumentFile.class);
			query.setParameter("pid", projectId);

			return query.list();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public Project findProjectForEditAbstract(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			// Query 1: Project + Advisor + Students
			String hql1 = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.advisor "
					+ "LEFT JOIN FETCH p.student496s " + "WHERE p.projectId = :projectId";

			Project project = session.createQuery(hql1, Project.class).setParameter("projectId", projectId)
					.uniqueResult();

			if (project != null) {
				// Query 2: Tools
				String hql2 = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.tools "
						+ "WHERE p.projectId = :projectId";
				session.createQuery(hql2, Project.class).setParameter("projectId", projectId).uniqueResult();

				// Query 3: DocumentFiles
				String hql3 = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.documentFiles "
						+ "WHERE p.projectId = :projectId";
				session.createQuery(hql3, Project.class).setParameter("projectId", projectId).uniqueResult();

				// Force initialize
				project.getStudent496s().size();
				project.getTools().size();
				project.getDocumentFiles().size();

				System.out.println("✅ findProjectForEditAbstract: Students=" + project.getStudent496s().size()
						+ ", Tools=" + project.getTools().size() + ", Files=" + project.getDocumentFiles().size());
			}

			return project;

		} catch (Exception e) {
			System.err.println("❌ Error in findProjectForEditAbstract: " + e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
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
			return true;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	// ✅ แก้ไข deleteProjectAndStudents ให้ลบ project_tools แทน projectLangDetails
	public boolean deleteProjectAndStudents(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			// ✅ ไม่ต้องลบ project_tools เพราะ cascade จะจัดการให้
			// Hibernate จะลบ relationship ใน project_tools อัตโนมัติ

			String deleteDocumentHql = "DELETE FROM DocumentFile df WHERE df.project.projectId = :projectId";
			session.createQuery(deleteDocumentHql).setParameter("projectId", projectId).executeUpdate();

			String deleteStudentHql = "DELETE FROM Student496 s WHERE s.project.projectId = :projectId";
			session.createQuery(deleteStudentHql).setParameter("projectId", projectId).executeUpdate();

			String deleteProjectHql = "DELETE FROM Project p WHERE p.projectId = :projectId";
			session.createQuery(deleteProjectHql).setParameter("projectId", projectId).executeUpdate();

			session.getTransaction().commit();
			return true;

		} catch (Exception e) {
			if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
			return false;
		} finally {
			if (session != null) {
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
			if (session != null) {
				session.close();
			}
		}
	}

	public List<Project> getAllProjects() {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.student496s s "
					+ "LEFT JOIN FETCH p.advisor a " + "ORDER BY p.projectId DESC";

			return session.createQuery(hql, Project.class).getResultList();

		} catch (Exception ex) {
			ex.printStackTrace();
			return new ArrayList<>();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public List<Map<String, Object>> getProjectGroupsByAdvisorAndSemester(String advisorId, String semester) {
		Session session = null;
		List<Map<String, Object>> projectGroups = new ArrayList<>();

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			// Query ดึงโครงงานพร้อมนักศึกษา - เพิ่ม projectType
			String hql = "SELECT p.projectId, p.proj_NameTh, p.approveStatus, p.testing_status, p.projectType, "
					+ "s.stuId, s.stu_prefix, s.stu_firstName, s.stu_lastName " + "FROM Project p "
					+ "LEFT JOIN p.student496s s " + "WHERE p.advisor.advisorId = :advisorId "
					+ "AND p.semester = :semester " + "ORDER BY s.stuId ASC";

			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("advisorId", advisorId);
			query.setParameter("semester", semester);
			List<Object[]> results = query.list();

			// จัดกลุ่มข้อมูล
			Map<Integer, Map<String, Object>> projectMap = new java.util.LinkedHashMap<>();

			for (Object[] row : results) {
				Integer projectId = (Integer) row[0];
				String projectName = (String) row[1];
				String approveStatus = (String) row[2];
				String testingStatus = (String) row[3];
				String projectType = (String) row[4]; // เพิ่มตัวนี้
				String studentId = (String) row[5];
				String prefix = (String) row[6];
				String firstName = (String) row[7];
				String lastName = (String) row[8];

				// ถ้ายังไม่มี Project นี้ใน Map
				if (!projectMap.containsKey(projectId)) {
					Map<String, Object> projectData = new java.util.HashMap<>();
					projectData.put("projectId", projectId);
					projectData.put("projectName", projectName);
					projectData.put("approveStatus", approveStatus);
					projectData.put("testingStatus", testingStatus != null ? testingStatus : "0");
					projectData.put("projectType", projectType); // เพิ่มตัวนี้
					projectData.put("students", new ArrayList<Map<String, String>>());

					projectMap.put(projectId, projectData);
				}

				// เพิ่มนักศึกษาเข้าไปใน List
				Map<String, String> studentData = new java.util.HashMap<>();
				studentData.put("studentId", studentId);
				studentData.put("prefix", prefix);
				studentData.put("firstName", firstName);
				studentData.put("lastName", lastName);

				@SuppressWarnings("unchecked")
				List<Map<String, String>> students = (List<Map<String, String>>) projectMap.get(projectId)
						.get("students");
				students.add(studentData);
			}

			// แปลง Map เป็น List
			projectGroups.addAll(projectMap.values());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		return projectGroups;
	}

	public List<Map<String, Object>> getAllProjectGroupsBySemester(String semester) {
		Session session = null;
		List<Map<String, Object>> projectGroups = new ArrayList<>();

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT p.projectId, p.proj_NameTh, p.approveStatus, p.testing_status, "
					+ "s.stuId, s.stu_prefix, s.stu_firstName, s.stu_lastName " + "FROM Project p "
					+ "JOIN p.student496s s " + "WHERE p.semester = :semester " + "ORDER BY s.stuId ASC";

			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("semester", semester);
			List<Object[]> results = query.list();

			// จัดกลุ่มข้อมูล (เหมือนเดิม)
			Map<Integer, Map<String, Object>> projectMap = new java.util.LinkedHashMap<>();

			for (Object[] row : results) {
				Integer projectId = (Integer) row[0];
				String projectName = (String) row[1];
				String approveStatus = (String) row[2];
				String testingStatus = (String) row[3];
				String studentId = (String) row[4];
				String prefix = (String) row[5];
				String firstName = (String) row[6];
				String lastName = (String) row[7];

				if (!projectMap.containsKey(projectId)) {
					Map<String, Object> projectData = new java.util.HashMap<>();
					projectData.put("projectId", projectId);
					projectData.put("projectName", projectName);
					projectData.put("approveStatus", approveStatus);
					projectData.put("testingStatus", testingStatus != null ? testingStatus : "0");
					projectData.put("students", new ArrayList<Map<String, String>>());

					projectMap.put(projectId, projectData);
				}

				Map<String, String> studentData = new java.util.HashMap<>();
				studentData.put("studentId", studentId);
				studentData.put("prefix", prefix);
				studentData.put("firstName", firstName);
				studentData.put("lastName", lastName);

				@SuppressWarnings("unchecked")
				List<Map<String, String>> students = (List<Map<String, String>>) projectMap.get(projectId)
						.get("students");
				students.add(studentData);
			}

			projectGroups.addAll(projectMap.values());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		return projectGroups;
	}

	/**
	 * อนุมัติการอัปโหลดไฟล์ (ระดับโครงงาน)
	 */
	public boolean approveProjectUpload(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			Project project = session.get(Project.class, projectId);
			if (project != null) {
				project.setApproveStatus("approved");
				project.setApproveDate(new java.util.Date());
				session.update(project);
				session.getTransaction().commit();
				return true;
			}

			session.getTransaction().rollback();
			return false;

		} catch (Exception e) {
			if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
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

	/**
	 * อัปเดตสถานะการทดสอบระบบ (ระดับโครงงาน)
	 */
	public boolean updateTestingStatus(int projectId, String testingStatus) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			Project project = session.get(Project.class, projectId);
			if (project != null) {
				project.setTesting_status(testingStatus);
				session.update(project);
				session.getTransaction().commit();
				return true;
			}

			session.getTransaction().rollback();
			return false;

		} catch (Exception e) {
			if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
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
}