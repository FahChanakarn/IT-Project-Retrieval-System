package com.springmvc.model;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateConnection {
	private static SessionFactory sessionFactory;

	static String url = "jdbc:mysql://localhost:3306/project496?characterEncoding=UTF-8";
	static String uname = "root";
	static String pwd = "1234";

	// ✅ Singleton Pattern - สร้าง SessionFactory ครั้งเดียว
	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null || sessionFactory.isClosed()) {
			synchronized (HibernateConnection.class) {
				if (sessionFactory == null || sessionFactory.isClosed()) {
					try {
						Properties database = new Properties();
						database.setProperty("hibernate.hbm2ddl.auto", "update");
						database.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
						database.setProperty("hibernate.connection.username", uname);
						database.setProperty("hibernate.connection.password", pwd);
						database.setProperty("hibernate.connection.url", url);
						database.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");

						// ✅ เพิ่ม Connection Pool Settings
						database.setProperty("hibernate.c3p0.min_size", "5");
						database.setProperty("hibernate.c3p0.max_size", "20");
						database.setProperty("hibernate.c3p0.timeout", "300");
						database.setProperty("hibernate.c3p0.max_statements", "50");
						database.setProperty("hibernate.c3p0.idle_test_period", "3000");

						Configuration cfg = new Configuration().setProperties(database)
								.addPackage("com.springmvc.model").addAnnotatedClass(Advisor.class)
								.addAnnotatedClass(Student.class).addAnnotatedClass(Student496.class)
								.addAnnotatedClass(Project.class).addAnnotatedClass(DocumentFile.class)
								.addAnnotatedClass(Tools.class);

						StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder()
								.applySettings(cfg.getProperties());
						sessionFactory = cfg.buildSessionFactory(ssrb.build());

						System.out.println("✅ SessionFactory created successfully (Singleton)");
					} catch (Throwable ex) {
						System.err.println("❌ Failed to create SessionFactory: " + ex);
						throw new ExceptionInInitializerError(ex);
					}
				}
			}
		}
		return sessionFactory;
	}

	// ✅ เพื่อความ backward compatible กับโค้ดเดิม
	public static SessionFactory doHibernateConnection() {
		return getSessionFactory();
	}

	// ✅ ปิด SessionFactory เมื่อจบโปรแกรม (เรียกใน ServletContextListener)
	public static void shutdown() {
		if (sessionFactory != null && !sessionFactory.isClosed()) {
			sessionFactory.close();
			System.out.println("🔴 SessionFactory closed");
		}
	}
}