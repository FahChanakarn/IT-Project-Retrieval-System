package com.springmvc.manager;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.web.multipart.MultipartFile;

import com.springmvc.model.*;

public class ImportStudentManager {

	private final String registeredSubject496 = "10306496";

	public String importFromExcel(MultipartFile file, String semester) {
		try (InputStream inputStream = file.getInputStream()) {
			return importFromExcel(inputStream, semester);
		} catch (Exception e) {
			e.printStackTrace();
			return "เกิดข้อผิดพลาด: " + e.getMessage();
		}
	}

	public String importFromExcel(InputStream excelFile, String semester) {
		int insertedCount = 0;
		int skippedCount = 0;

		try (Workbook workbook = new XSSFWorkbook(excelFile)) {
			Sheet sheet = workbook.getSheetAt(0);

			SessionFactory factory = HibernateConnection.doHibernateConnection();
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();

			for (int i = 1; i <= sheet.getLastRowNum(); i++) { // ข้าม header
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				String stuId = getStringCell(row.getCell(0)).trim().replaceAll("\\s+", "");
				String fullName = getStringCell(row.getCell(1));
				String advisorFullName = getStringCell(row.getCell(2));
				String projectNameTh = getStringCell(row.getCell(3));
				String projectType = getStringCell(row.getCell(4));
				String password = getStringCell(row.getCell(5));

				// --- จัดการ Advisor ---
				if (advisorFullName == null || advisorFullName.isEmpty()) {
					skippedCount++;
					System.out.println("Skipped: Advisor empty for student - " + stuId);
					continue;
				}

				// ตัดคำนำหน้าที่ไม่จำเป็น
				String[] unwantedPrefixes = { "ผศ.ดร.", "อ.ดร.", "อ." };
				for (String p : unwantedPrefixes) {
					if (advisorFullName.startsWith(p)) {
						advisorFullName = advisorFullName.substring(p.length()).trim();
					}
				}

				// แยก firstname / lastname ของ Advisor
				String[] nameParts = advisorFullName.split("\\s+");
				String advisorLastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";
				String advisorFirstName = nameParts.length > 1
						? String.join(" ", java.util.Arrays.copyOf(nameParts, nameParts.length - 1))
						: nameParts[0];

				Advisor advisor = (Advisor) session
						.createQuery("FROM Advisor WHERE adv_firstName = :first AND adv_lastName = :last")
						.setParameter("first", advisorFirstName).setParameter("last", advisorLastName).uniqueResult();

				if (advisor == null) {
					skippedCount++;
					System.out.println("Skipped: Advisor not found - " + advisorFullName);
					continue;
				}

				// --- แยก prefix / firstname / lastname ของ Student ---
				int lastSpace = fullName.lastIndexOf(" ");
				String lastName = lastSpace >= 0 ? fullName.substring(lastSpace + 1).trim() : "";
				String firstAndPrefix = lastSpace >= 0 ? fullName.substring(0, lastSpace).trim() : fullName;

				String prefix = "";
				String firstName = firstAndPrefix;
				String[] prefixes = { "นาย", "นางสาว" };
				for (String p : prefixes) {
					if (firstAndPrefix.startsWith(p)) {
						prefix = p;
						firstName = firstAndPrefix.substring(p.length()).trim();
						break;
					}
				}

				// --- ตรวจสอบ Student496 ซ้ำ ---
				if (session.get(Student496.class, stuId) != null) {
					skippedCount++;
					System.out.println("Skipped: Student496 exists - " + stuId);
					continue;
				}

				// --- สร้าง Project ---
				Project project = new Project();
				project.setProj_NameTh(projectNameTh);
				project.setProj_NameEn("-");
				project.setProjectType(projectType);
				project.setSemester(semester);
				project.setApproveStatus("0");
				project.setTesting_status("0");
				project.setAdvisor(advisor);
				project.setTypeDB(null); // ไม่ต้องใส่ TypeDB
				session.save(project);

				// --- สร้าง Student496 (รวม Student ด้วย) ---
				Student496 stu496 = new Student496();
				stu496.setStuId(stuId);
				stu496.setStu_prefix(prefix);
				stu496.setStu_firstName(firstName);
				stu496.setStu_lastName(lastName);
				stu496.setStu_password(password);
				stu496.setStu_image(null);
				stu496.setRegisteredSubject(registeredSubject496);
				stu496.setProject(project);

				session.save(stu496); // Hibernate จะ insert ทั้ง Student และ Student496
				insertedCount++;

				// --- flush/clear ทุก 20 แถว ป้องกัน memory เต็ม ---
				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}
			}

			tx.commit();
			session.close();
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
			return "เกิดข้อผิดพลาดในการนำเข้าข้อมูล: " + e.getMessage();
		}

		return "นำเข้าข้อมูลสำเร็จ " + insertedCount + " แถว, ข้าม " + skippedCount
				+ " แถวที่มีรหัสซ้ำหรือข้อมูลไม่ครบ";
	}

	private String getStringCell(Cell cell) {
		return cell != null ? cell.toString().trim() : "";
	}
}
