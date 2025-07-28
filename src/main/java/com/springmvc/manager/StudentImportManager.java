package com.springmvc.manager;

import com.springmvc.model.HibernateConnection;
import com.springmvc.model.Student;
import org.apache.poi.ss.usermodel.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class StudentImportManager {

	public int importStudentsFromExcel(MultipartFile file) throws Exception {
		List<Student> students = new ArrayList<>();

		// อ่าน Excel
		try (InputStream is = file.getInputStream()) {
			Workbook workbook = WorkbookFactory.create(is);
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rows = sheet.iterator();

			// ข้าม header
			if (rows.hasNext())
				rows.next();

			while (rows.hasNext()) {
				Row row = rows.next();
				Student student = new Student();

				student.setStuId(getCellValue(row.getCell(0)));
				student.setStu_prefix(getCellValue(row.getCell(1)));
				student.setStu_firstName(getCellValue(row.getCell(2)));
				student.setStu_lastName(getCellValue(row.getCell(3)));
				student.setStu_password(getCellValue(row.getCell(4)));

				students.add(student);
			}
			workbook.close();
		}

		// บันทึกลงฐานข้อมูล
		int inserted = 0;
		SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		try {
			for (Student student : students) {
				session.saveOrUpdate(student); // ใช้ saveOrUpdate ป้องกัน duplicate key
				inserted++;
			}
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			session.close();
		}

		return inserted;
	}

	private String getCellValue(Cell cell) {
		if (cell == null)
			return "";
		return switch (cell.getCellType()) {
		case STRING -> cell.getStringCellValue().trim();
		case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
		case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
		default -> "";
		};
	}
}
