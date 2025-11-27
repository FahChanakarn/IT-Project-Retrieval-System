package com.springmvc.model;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateConnection {
	private static SessionFactory sessionFactory;

	// Dynamic database configuration
	private static String getDbUrl() {
		// Check if running in Docker by looking for environment variables
		String dbHost = System.getenv("DB_HOST");
		if (dbHost != null) {
			// Running in Docker
			return "jdbc:mysql://" + dbHost + ":3306/project_retrieval_system?characterEncoding=UTF-8";
		} else {
			// Running locally
			return "jdbc:mysql://localhost:3306/project_retrieval_system?characterEncoding=UTF-8";
		}
	}

	private static String getDbUser() {
		String dbUser = System.getenv("DB_USER");
		return dbUser != null ? dbUser : "root";
	}

	private static String getDbPassword() {
		String dbPassword = System.getenv("DB_PASSWORD");
		return dbPassword != null ? dbPassword : "1234";
	}

	// ✅ Singleton Pattern - สร้าง SessionFactory ครั้งเดียว
	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null || sessionFactory.isClosed()) {
			synchronized (HibernateConnection.class) {
				if (sessionFactory == null || sessionFactory.isClosed()) {
					try {
						Properties database = new Properties();
						database.setProperty("hibernate.hbm2ddl.auto", "update");
						database.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
						database.setProperty("hibernate.connection.username", getDbUser());
						database.setProperty("hibernate.connection.password", getDbPassword());
						database.setProperty("hibernate.connection.url", getDbUrl());
						database.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");

						// ✅ เพิ่ม Connection Pool Settings - Optimized for Production
						database.setProperty("hibernate.c3p0.min_size", "10");
						database.setProperty("hibernate.c3p0.max_size", "50");
						database.setProperty("hibernate.c3p0.timeout", "1800"); // 30 minutes
						database.setProperty("hibernate.c3p0.max_statements", "100");
						database.setProperty("hibernate.c3p0.idle_test_period", "300"); // 5 minutes
						database.setProperty("hibernate.c3p0.acquire_increment", "5");
						database.setProperty("hibernate.c3p0.max_idle_time", "1800"); // 30 minutes
						database.setProperty("hibernate.c3p0.maxConnectionAge", "3600"); // 1 hour
						database.setProperty("hibernate.c3p0.preferredTestQuery", "SELECT 1");
						database.setProperty("hibernate.c3p0.testConnectionOnCheckout", "false");
						database.setProperty("hibernate.c3p0.testConnectionOnCheckin", "true");
						database.setProperty("hibernate.c3p0.unreturnedConnectionTimeout", "300"); // 5 minutes
						database.setProperty("hibernate.c3p0.debugUnreturnedConnectionStackTraces", "true");
						database.setProperty("hibernate.c3p0.numHelperThreads", "6");

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