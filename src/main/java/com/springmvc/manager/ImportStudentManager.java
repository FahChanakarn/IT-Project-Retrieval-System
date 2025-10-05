package com.springmvc.manager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.web.multipart.MultipartFile;

import com.springmvc.model.*;
import com.springmvc.controller.PasswordUtil;

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

		// Map เก็บ Project ที่สร้างแล้ว (key = ชื่อโครงงาน + semester)
		Map<String, Project> projectMap = new HashMap<>();

		try (Workbook workbook = new XSSFWorkbook(excelFile)) {
			Sheet sheet = workbook.getSheetAt(0);

			SessionFactory factory = HibernateConnection.doHibernateConnection();
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				String stuId = getStringCell(row.getCell(0)).trim().replaceAll("\\s+", "");
				String fullName = getStringCell(row.getCell(1));
				String advisorFullName = getStringCell(row.getCell(2));
				String projectNameTh = getStringCell(row.getCell(3));
				String projectType = getStringCell(row.getCell(4));
				// ✅ สร้าง password อัตโนมัติ: mju + stuId
				String password = "mju" + stuId;

				// --- ตรวจสอบข้อมูลพื้นฐาน ---
				if (stuId.isEmpty() || projectNameTh.isEmpty()) {
					skippedCount++;
					System.out.println("Skipped: Missing required data - row " + i);
					continue;
				}

				// --- จัดการ Advisor ---
				if (advisorFullName == null || advisorFullName.isEmpty()) {
					skippedCount++;
					System.out.println("Skipped: Advisor empty for student - " + stuId);
					continue;
				}

				String[] unwantedPrefixes = { "ผศ.ดร.", "อ.ดร.", "อ." };
				for (String p : unwantedPrefixes) {
					if (advisorFullName.startsWith(p)) {
						advisorFullName = advisorFullName.substring(p.length()).trim();
					}
				}

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
				String[] prefixes = { "นาย", "นางสาว", "นาง" };
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

				// --- จัดการ Project (ตรวจสอบว่ามีอยู่แล้วหรือไม่) ---
				String projectKey = projectNameTh + "_" + semester;
				Project project = projectMap.get(projectKey);

				if (project == null) {
					// ตรวจสอบใน Database ก่อน
					project = (Project) session
							.createQuery("FROM Project WHERE proj_NameTh = :name AND semester = :sem")
							.setParameter("name", projectNameTh).setParameter("sem", semester).uniqueResult();

					if (project == null) {
						// สร้าง Project ใหม่
						project = new Project();
						project.setProj_NameTh(projectNameTh);
						project.setProj_NameEn(null);
						project.setProjectType(projectType);
						project.setSemester(semester);
						project.setApproveStatus("0");
						project.setTesting_status("0");
						project.setAdvisor(advisor);
						session.save(project);
						session.flush(); // บังคับให้ได้ projectId ทันที
					}

					projectMap.put(projectKey, project);
				}

				Student496 stu496 = new Student496();
				stu496.setStuId(stuId);
				stu496.setStu_prefix(prefix);
				stu496.setStu_firstName(firstName);
				stu496.setStu_lastName(lastName);
				stu496.setStu_password(PasswordUtil.hashPassword(password)); // ✅ เข้ารหัสด้วย BCrypt
				stu496.setStu_image(null);
				stu496.setRegisteredSubject(registeredSubject496);
				stu496.setProject(project);

				session.save(stu496);
				insertedCount++;

				// Flush ทุก 20 แถว
				if (i % 20 == 0) {
					session.flush();
					session.clear();
					// reload projectMap หลัง clear
					projectMap.clear();
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