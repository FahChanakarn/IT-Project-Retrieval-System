package com.springmvc.manager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.springmvc.model.Advisor;
import com.springmvc.model.HibernateConnection;

import java.util.ArrayList;
import java.util.List;

public class AdvisorManager {

	public boolean addAdvisor(Advisor advisor) {
		boolean isSuccess = false;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			Session session = sessionFactory.openSession();
			session.beginTransaction();

			// ใช้ prefix เดียว T เสมอ
			String prefix = "T";
			String lastId = getLastAdvisorIdWithPrefix(prefix);
			int nextNumber = 1;

			if (lastId != null) {
				try {
					nextNumber = Integer.parseInt(lastId.substring(1)) + 1;
				} catch (NumberFormatException e) {
					e.printStackTrace(); // ป้องกันเลขผิดรูปแบบ
				}
			}

			String newId = prefix + nextNumber;
			advisor.setAdvisorId(newId);

			// ตั้งค่า default
			advisor.setAdv_status("ปฏิบัติราชการ");
			advisor.setAdv_position("อาจารย์ที่ปรึกษา");

			session.save(advisor);
			session.getTransaction().commit();
			session.close();
			isSuccess = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}

	public List<Advisor> getAllAdvisors() {
		List<Advisor> advisors = new ArrayList<>();
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			advisors = session.createQuery("FROM Advisor", Advisor.class).getResultList();
			session.getTransaction().commit();
			session.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return advisors;
	}

	public Advisor getAdvisorById(String advisorId) {
		Advisor advisor = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			advisor = session.get(Advisor.class, advisorId); // ดึงข้อมูลตาม ID
			session.getTransaction().commit();
			session.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return advisor;
	}

	public boolean updateAdvisor(Advisor advisor) {
		boolean isSuccess = false;
		Session session = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			session = sessionFactory.openSession();
			session.beginTransaction();

			Advisor existing = session.get(Advisor.class, advisor.getAdvisorId());
			if (existing != null) {
				existing.setAdv_prefix(advisor.getAdv_prefix());
				existing.setAdv_firstName(advisor.getAdv_firstName());
				existing.setAdv_lastName(advisor.getAdv_lastName());
				existing.setAdv_position(advisor.getAdv_position());
				existing.setAdv_email(advisor.getAdv_email());
				existing.setAdv_password(advisor.getAdv_password());

				session.update(existing);
				session.getTransaction().commit();
				isSuccess = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
		return isSuccess;
	}

	public boolean deleteAdvisor(String advisorId) {
		boolean isSuccess = false;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			Advisor advisor = session.get(Advisor.class, advisorId);
			if (advisor != null) {
				session.delete(advisor); // ลบข้อมูลออกจากฐานข้อมูล
				session.getTransaction().commit();
				isSuccess = true;
			}
			session.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}

	public List<Advisor> getActiveAdvisors() {
		List<Advisor> advisors = null;
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			Session session = sessionFactory.openSession();
			session.beginTransaction();

			String hql = "FROM Advisor WHERE adv_status = 'ปฏิบัติราชการ'";
			advisors = session.createQuery(hql, Advisor.class).getResultList();

			session.getTransaction().commit();
			session.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return advisors;
	}

	public Advisor findByEmailAndPassword(String email, String password) {
		Advisor advisor = null;
		SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
		Session session = null;

		try {
			session = sessionFactory.openSession();
			session.beginTransaction();

			String hql = "FROM Advisor WHERE adv_email = :email AND adv_password = :password";
			advisor = session.createQuery(hql, Advisor.class).setParameter("email", email)
					.setParameter("password", password).uniqueResult();

			session.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		return advisor;
	}

	public void toggleStatus(String advisorId) {
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			Session session = sessionFactory.openSession();
			session.beginTransaction();

			Advisor advisor = session.get(Advisor.class, advisorId);
			if (advisor != null) {
				String currentStatus = advisor.getAdv_status();
				String newStatus = currentStatus.equals("ปฏิบัติราชการ") ? "ลาศึกษาต่อ" : "ปฏิบัติราชการ";
				advisor.setAdv_status(newStatus);
				session.update(advisor);
			}

			session.getTransaction().commit();
			session.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String generateNextAdvisorId(String prefix) {
		String nextId = prefix + "1"; // default ถ้ายังไม่มีข้อมูลเลย
		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			Session session = sessionFactory.openSession();
			session.beginTransaction();

			String hql = "SELECT advisorId FROM Advisor WHERE advisorId LIKE :prefix ORDER BY LENGTH(advisorId) DESC, advisorId DESC";
			List<String> ids = session.createQuery(hql, String.class).setParameter("prefix", prefix + "%")
					.getResultList();

			if (!ids.isEmpty()) {
				String lastId = ids.get(0);
				int number = Integer.parseInt(lastId.substring(1));
				nextId = prefix + (number + 1);
			}

			session.getTransaction().commit();
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nextId;
	}

	public String getLastAdvisorIdWithPrefix(String prefix) {
		String lastId = null;

		try {
			SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
			Session session = sessionFactory.openSession();
			session.beginTransaction();

			String hql = "SELECT a.advisorId FROM Advisor a WHERE a.advisorId LIKE :prefix ORDER BY a.advisorId DESC";
			lastId = session.createQuery(hql, String.class).setParameter("prefix", prefix + "%").setMaxResults(1)
					.uniqueResult();

			session.getTransaction().commit();
			session.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return lastId;
	}

}
