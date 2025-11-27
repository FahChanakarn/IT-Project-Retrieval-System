package com.springmvc.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.springmvc.model.HibernateConnection;

/**
 * Application Lifecycle Listener
 * ทำหน้าที่ปิด resources เมื่อ Tomcat shutdown หรือ undeploy
 */
@WebListener
public class AppContextListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    System.out.println("🚀 Application is starting up...");
    // เริ่มต้น SessionFactory ตอน startup
    HibernateConnection.getSessionFactory();
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    System.out.println("🔴 Application is shutting down...");

    try {
      // 1. ปิด Hibernate SessionFactory
      HibernateConnection.shutdown();
      System.out.println("✅ Hibernate SessionFactory closed");
    } catch (Exception e) {
      System.err.println("❌ Error closing SessionFactory: " + e.getMessage());
    }

    try {
      // 2. ปิด MySQL JDBC Cleanup Thread
      AbandonedConnectionCleanupThread.checkedShutdown();
      System.out.println("✅ MySQL AbandonedConnectionCleanupThread shut down");
    } catch (Exception e) {
      System.err.println("❌ Error shutting down MySQL cleanup thread: " + e.getMessage());
    }

		try {
			// 3. De-register JDBC drivers (ป้องกัน memory leak)
			java.util.Enumeration<java.sql.Driver> drivers = java.sql.DriverManager.getDrivers();
			while (drivers.hasMoreElements()) {
				try {
					java.sql.Driver driver = drivers.nextElement();
					java.sql.DriverManager.deregisterDriver(driver);
					System.out.println("✅ Deregistered JDBC driver: " + driver);
				} catch (Exception e) {
					System.err.println("❌ Error deregistering driver: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.err.println("❌ Error deregistering JDBC drivers: " + e.getMessage());
		}    System.out.println("✅ Application cleanup completed");
  }
}
