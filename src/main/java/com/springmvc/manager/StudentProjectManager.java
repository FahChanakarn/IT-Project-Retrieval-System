package com.springmvc.manager;

import com.springmvc.model.HibernateConnection;

import com.springmvc.model.Project;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentProjectManager {

	// ✅ ดึงโครงงานทั้งหมดตามภาคเรียน (สำหรับ Admin - รายการโครงงานทั้งหมด)
	public List<Map<String, Object>> getAllProjectGroupsBySemester(String semester) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT p.projectId, p.proj_NameTh, p.approveStatus, p.testing_status, p.projectType, "
					+ "s.stuId, CONCAT(st.stu_prefix, st.stu_firstName, ' ', st.stu_lastName) " + "FROM Project p "
					+ "JOIN p.student496s s " + "JOIN Student st ON s.stuId = st.stuId "
					+ "WHERE p.semester = :semester " + "ORDER BY p.projectId, s.stuId";

			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("semester", semester);
			List<Object[]> results = query.list();

			Map<Integer, Map<String, Object>> projectMap = new LinkedHashMap<>();

			for (Object[] row : results) {
				Integer projectId = (Integer) row[0];
				String projectName = (String) row[1];
				String approvalStatus = (String) row[2];
				String testingStatus = (String) row[3];
				String projectType = (String) row[4];
				String studentId = (String) row[5];
				String studentFullName = (String) row[6];

				if (!projectMap.containsKey(projectId)) {
					Map<String, Object> projectGroup = new HashMap<>();
					projectGroup.put("projectId", projectId);
					projectGroup.put("projectName", projectName);
					projectGroup.put("approveStatus", approvalStatus);
					projectGroup.put("testingStatus", testingStatus);
					projectGroup.put("projectType", projectType);
					projectGroup.put("students", new ArrayList<Map<String, String>>());
					projectMap.put(projectId, projectGroup);
				}

				Map<String, String> studentInfo = new HashMap<>();
				String[] nameParts = studentFullName.split(" ", 2);
				studentInfo.put("studentId", studentId);
				studentInfo.put("prefix", nameParts.length > 0 ? nameParts[0] : "");
				studentInfo.put("firstName", nameParts.length > 1 ? nameParts[1].split(" ")[0] : "");
				studentInfo.put("lastName",
						nameParts.length > 1 && nameParts[1].contains(" ")
								? nameParts[1].substring(nameParts[1].indexOf(" ") + 1)
								: "");

				((List<Map<String, String>>) projectMap.get(projectId).get("students")).add(studentInfo);
			}

			return new ArrayList<>(projectMap.values());

		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	// ✅ ดึงโครงงานของอาจารย์ที่ปรึกษาตามภาคเรียน (สำหรับ Advisor)
	public List<Map<String, Object>> getProjectGroupsByAdvisorAndSemester(String string, String semester) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT p.projectId, p.proj_NameTh, p.approveStatus, p.testing_status, p.projectType, "
					+ "s.stuId, CONCAT(st.stu_prefix, st.stu_firstName, ' ', st.stu_lastName) " + "FROM Project p "
					+ "JOIN p.student496s s " + "JOIN Student st ON s.stuId = st.stuId "
					+ "WHERE p.advisor.advisorId = :advisorId AND p.semester = :semester "
					+ "ORDER BY p.projectId, s.stuId";

			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("advisorId", string);
			query.setParameter("semester", semester);
			List<Object[]> results = query.list();

			Map<Integer, Map<String, Object>> projectMap = new LinkedHashMap<>();

			for (Object[] row : results) {
				Integer projectId = (Integer) row[0];
				String projectName = (String) row[1];
				String approvalStatus = (String) row[2];
				String testingStatus = (String) row[3];
				String projectType = (String) row[4];
				String studentId = (String) row[5];
				String studentFullName = (String) row[6];

				if (!projectMap.containsKey(projectId)) {
					Map<String, Object> projectGroup = new HashMap<>();
					projectGroup.put("projectId", projectId);
					projectGroup.put("projectName", projectName);
					projectGroup.put("approveStatus", approvalStatus);
					projectGroup.put("testingStatus", testingStatus);
					projectGroup.put("projectType", projectType);
					projectGroup.put("students", new ArrayList<Map<String, String>>());
					projectMap.put(projectId, projectGroup);
				}

				Map<String, String> studentInfo = new HashMap<>();
				String[] nameParts = studentFullName.split(" ", 2);
				studentInfo.put("studentId", studentId);
				studentInfo.put("prefix", nameParts.length > 0 ? nameParts[0] : "");
				studentInfo.put("firstName", nameParts.length > 1 ? nameParts[1].split(" ")[0] : "");
				studentInfo.put("lastName",
						nameParts.length > 1 && nameParts[1].contains(" ")
								? nameParts[1].substring(nameParts[1].indexOf(" ") + 1)
								: "");

				((List<Map<String, String>>) projectMap.get(projectId).get("students")).add(studentInfo);
			}

			return new ArrayList<>(projectMap.values());

		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	// ✅ ดึงภาคเรียนล่าสุด
	public String getLatestSemester() {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT DISTINCT p.semester FROM Project p ORDER BY p.semester DESC";
			Query<String> query = session.createQuery(hql, String.class);
			query.setMaxResults(1);

			String result = query.uniqueResult();
			return result != null ? result : "2/2568";

		} catch (Exception e) {
			e.printStackTrace();
			return "2/2568";
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	// ✅ อนุมัติการอัปโหลดโครงงาน
	public boolean approveProjectUpload(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			Project project = session.get(Project.class, projectId);
			if (project != null) {
				project.setApproveStatus("approved");
				session.update(project);
				session.getTransaction().commit();
				return true;
			}
			return false;

		} catch (Exception e) {
			if (session != null && session.getTransaction() != null) {
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

	// ✅ อัปเดตสถานะการทดสอบระบบ
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
			return false;

		} catch (Exception e) {
			if (session != null && session.getTransaction() != null) {
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

	// ✅ FIX: ดึงข้อมูลโครงงานสำหรับแก้ไข Abstract พร้อม Eager Fetch ทุก Collection
	public Project findProjectForEditAbstract(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			// โหลด Project ก่อน
			Project project = session.get(Project.class, projectId);

			// Initialize ทุก collection ที่อาจจะมีการเข้าถึงใน View
			if (project != null) {
				// Initialize student496s
				Hibernate.initialize(project.getStudent496s());

				// Initialize documentFiles
				Hibernate.initialize(project.getDocumentFiles());

				// Initialize tools (ManyToMany)
				Hibernate.initialize(project.getTools());

				// Initialize advisor
				if (project.getAdvisor() != null) {
					Hibernate.initialize(project.getAdvisor());
				}
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

	// ✅ ตรวจสอบว่าโครงงานมีอยู่หรือไม่
	public boolean projectExists(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT COUNT(p) FROM Project p WHERE p.projectId = :projectId";
			Long count = session.createQuery(hql, Long.class).setParameter("projectId", projectId).uniqueResult();

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

	// ✅ ลบโครงงานและนักศึกษา
	public boolean deleteProjectAndStudents(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			String deleteStudents = "DELETE FROM Student496 s WHERE s.project.projectId = :projectId";
			session.createQuery(deleteStudents).setParameter("projectId", projectId).executeUpdate();

			Project project = session.get(Project.class, projectId);
			if (project != null) {
				session.delete(project);
			}

			session.getTransaction().commit();
			return true;

		} catch (Exception e) {
			if (session != null && session.getTransaction() != null) {
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

	// ✅ ดึง Project ตาม ID
	public Project findProjectById(int projectId) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			return session.get(Project.class, projectId);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}
}