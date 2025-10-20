package com.springmvc.manager;

import com.springmvc.model.HibernateConnection;
import com.springmvc.model.Tools;
import com.springmvc.model.Tools.ToolsType;
import com.springmvc.model.Project;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ToolsManager {

	public List<Tools> getAllTools() {
		Session session = null;
		List<Tools> tools = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			String hql = "FROM Tools ORDER BY toolType ASC, toolsName ASC";
			Query<Tools> query = session.createQuery(hql, Tools.class);
			tools = query.list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return tools;
	}

	public List<Tools> getToolsByType(ToolsType toolType) {
		List<Tools> tools = new ArrayList<>();
		Session session = null;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "FROM Tools WHERE toolType = :type ORDER BY toolsName ASC";
			tools = session.createQuery(hql, Tools.class).setParameter("type", toolType).getResultList();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		return tools;
	}

	public Tools findToolsById(int id) {
		Session session = null;
		Tools tool = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			tool = session.get(Tools.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return tool;
	}

	public void addToolsToProject(Project project, int toolsId) {
		Session session = null;
		Transaction tx = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			Project managedProject = session.get(Project.class, project.getProjectId());
			Tools tool = session.get(Tools.class, toolsId);

			if (managedProject != null && tool != null) {
				managedProject.getTools().add(tool);
				session.update(managedProject);
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

	public void addOtherToolsToProject(Project project, String toolsName) {
		Session session = null;
		Transaction tx = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			Query<Tools> query = session.createQuery("FROM Tools WHERE toolsName = :name", Tools.class);
			query.setParameter("name", toolsName);
			Tools tool = query.uniqueResult();

			if (tool == null) {
				tool = new Tools();
				tool.setToolsName(toolsName);
				tool.setToolType(Tools.ToolsType.PROGRAMMING);
				session.save(tool);
				session.flush();
			}

			Project managedProject = session.get(Project.class, project.getProjectId());
			if (managedProject != null && !managedProject.getTools().contains(tool)) {
				managedProject.getTools().add(tool);
				session.update(managedProject);
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

	public boolean existsProjectTools(int projectId, int toolsId) {
		Session session = null;
		boolean exists = false;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT COUNT(*) FROM Project p JOIN p.tools t "
					+ "WHERE p.projectId = :pid AND t.toolsId = :tid";

			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("pid", projectId);
			query.setParameter("tid", toolsId);

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

	public void removeUnselectedTools(int projectId, Set<Integer> selectedToolsIds) {
		Session session = null;
		Transaction tx = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			String hql = "SELECT DISTINCT p FROM Project p " + "LEFT JOIN FETCH p.tools " + "WHERE p.projectId = :pid";

			Project project = session.createQuery(hql, Project.class).setParameter("pid", projectId).uniqueResult();

			if (project != null) {
				project.getTools().removeIf(tool -> (tool.getToolType() == Tools.ToolsType.PROGRAMMING
						|| tool.getToolType() == Tools.ToolsType.DBMS || tool.getToolType() == Tools.ToolsType.Testing)
						&& !selectedToolsIds.contains(tool.getToolsId()));

				session.update(project);
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

	// ✅ ตรวจสอบว่ามีชื่อ Tools นี้แล้วหรือยัง
	public boolean isToolNameExists(String toolName) {
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();

			String hql = "SELECT COUNT(*) FROM Tools WHERE LOWER(toolsName) = LOWER(:name)";
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("name", toolName.trim());

			Long count = query.uniqueResult();
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

	// ✅ บันทึก Tools ใหม่
	public boolean saveTool(Tools tool) {
		Session session = null;
		Transaction tx = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			session.save(tool);

			tx.commit();
			return true;

		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
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