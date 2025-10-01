package com.springmvc.model;
 
import java.util.Properties;
 
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
 
public class HibernateConnection {
	public static SessionFactory sessionFactory;
	static String url = "jdbc:mysql://localhost:3306/project496?characterEncoding=UTF-8";
	static String uname = "root";
	static String pwd = "1234";
	
	public static SessionFactory doHibernateConnection(){
		Properties database = new Properties();
		database.setProperty("hibernate.hbm2ddl.auto", "update"); //หลังจากสร้างตารางแล้วให้เอาออก
		database.setProperty("hibernate.connection.driver_class","com.mysql.jdbc.Driver");
		database.setProperty("hibernate.connection.username",uname);
		database.setProperty("hibernate.connection.password",pwd);
		database.setProperty("hibernate.connection.url",url);
		database.setProperty("hibernate.dialect","org.hibernate.dialect.MySQL5InnoDBDialect");
		Configuration cfg = new Configuration()
							.setProperties(database)
							.addPackage("com.springmvc.model")
							.addAnnotatedClass(Advisor.class)
							.addAnnotatedClass(Student.class)
							.addAnnotatedClass(Student496.class)
							.addAnnotatedClass(Project.class)
							.addAnnotatedClass(TypeDB.class)
							.addAnnotatedClass(DocumentFile.class)
							.addAnnotatedClass(ProgrammingLang.class)
							.addAnnotatedClass(ProjectLangDetailId.class)
							.addAnnotatedClass(ProjectLangDetail.class)
							.addAnnotatedClass(DownloadToken.class);
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties());
		sessionFactory = cfg.buildSessionFactory(ssrb.build());
		return sessionFactory;
	}
}