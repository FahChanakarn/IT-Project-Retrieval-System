package com.springmvc.manager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

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
	private List<String> duplicateStudents = new ArrayList<>();

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
		duplicateStudents.clear();

		Map<String, Project> projectMap = new HashMap<>();
		Map<String, Set<String>> projectTypesFound = new HashMap<>();

		Session session = null;
		Transaction tx = null;

		try (Workbook workbook = new XSSFWorkbook(excelFile)) {
			Sheet sheet = workbook.getSheetAt(0);

			SessionFactory factory = HibernateConnection.doHibernateConnection();
			session = factory.openSession();
			tx = session.beginTransaction();

			System.out.println("========== Pass 1: Scanning Excel ==========");
			// Pass 1: สแกนทั้งไฟล์เพื่อรวบรวมประเภทที่พบในแต่ละโปรเจค
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				String projectNameTh = getStringCell(row.getCell(3)).trim();
				String studentType = getStringCell(row.getCell(4)).trim();
				String advisorFullName = getStringCell(row.getCell(2)).trim();

				if (projectNameTh.isEmpty() || advisorFullName.isEmpty())
					continue;

				String[] unwantedPrefixes = { "ผศ.ดร.", "อ.ดร.", "อ." };
				for (String p : unwantedPrefixes) {
					if (advisorFullName.startsWith(p)) {
						advisorFullName = advisorFullName.substring(p.length()).trim();
					}
				}

				String projectKey = projectNameTh + "_" + semester + "_" + advisorFullName;

				if (!projectTypesFound.containsKey(projectKey)) {
					projectTypesFound.put(projectKey, new HashSet<>());
				}

				String normalizedType = normalizeType(studentType);
				if (!normalizedType.isEmpty()) {
					projectTypesFound.get(projectKey).add(normalizedType);
					System.out.println("Row " + (i + 1) + " | Project: " + projectNameTh + " | Original: '"
							+ studentType + "' | Normalized: " + normalizedType);
				}
			}

			System.out.println("\n========== Project Types Summary ==========");
			for (Map.Entry<String, Set<String>> entry : projectTypesFound.entrySet()) {
				String finalType = determineFinalProjectType(entry.getKey(), projectTypesFound);
				System.out.println("Project Key: " + entry.getKey());
				System.out.println("  - Found Types: " + entry.getValue());
				System.out.println("  - Final Type: " + finalType);
			}

			System.out.println("\n========== Pass 2: Importing Data ==========");
			// Pass 2: Import ข้อมูลจริง
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				String stuId = getStringCell(row.getCell(0)).trim().replaceAll("\\s+", "");
				String fullName = getStringCell(row.getCell(1));
				String advisorFullName = getStringCell(row.getCell(2));
				String projectNameTh = getStringCell(row.getCell(3));
				String studentType = getStringCell(row.getCell(4)).trim();

				String password = "mju" + stuId;

				if (stuId.isEmpty() || projectNameTh.isEmpty()) {
					skippedCount++;
					System.out.println("Skipped Row " + (i + 1) + ": Missing required data");
					continue;
				}

				if (advisorFullName == null || advisorFullName.isEmpty()) {
					skippedCount++;
					System.out.println("Skipped Row " + (i + 1) + ": Advisor empty for student - " + stuId);
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
					System.out.println("Skipped Row " + (i + 1) + ": Advisor not found - " + advisorFullName);
					continue;
				}

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

				if (session.get(Student496.class, stuId) != null) {
					skippedCount++;
					duplicateStudents.add(stuId);
					System.out.println("Skipped Row " + (i + 1) + ": Student496 exists - " + stuId);
					continue;
				}

				String projectKey = projectNameTh + "_" + semester + "_" + advisorFullName;
				Project project = projectMap.get(projectKey);

				if (project == null) {
					project = (Project) session.createQuery(
							"FROM Project WHERE proj_NameTh = :name AND semester = :sem AND advisor.advisorId = :advisorId")
							.setParameter("name", projectNameTh).setParameter("sem", semester)
							.setParameter("advisorId", advisor.getAdvisorId()).uniqueResult();

					if (project == null) {
						String finalProjectType = determineFinalProjectType(projectKey, projectTypesFound);

						project = new Project();
						project.setProj_NameTh(projectNameTh);
						project.setProj_NameEn(null);
						project.setProjectType(finalProjectType);
						project.setSemester(semester);
						project.setApproveStatus("0");
						project.setTesting_status("0");
						project.setAdvisor(advisor);
						session.save(project);
						session.flush();

						System.out.println("Created Project: " + projectNameTh + " | Type: " + finalProjectType);
					} else {
						System.out.println(
								"Using existing Project: " + projectNameTh + " | Type: " + project.getProjectType());
					}

					projectMap.put(projectKey, project);
				}

				Student496 stu496 = new Student496();
				stu496.setStuId(stuId);
				stu496.setStu_prefix(prefix);
				stu496.setStu_firstName(firstName);
				stu496.setStu_lastName(lastName);
				stu496.setStu_password(PasswordUtil.hashPassword(password));
				stu496.setStu_image(null);
				stu496.setRegisteredSubject(registeredSubject496);
				stu496.setProject(project);

				session.save(stu496);
				insertedCount++;
				System.out.println("Inserted Student: " + stuId + " | Project: " + projectNameTh);

				if (i % 20 == 0) {
					session.flush();
					session.clear();
					for (String key : new HashMap<>(projectMap).keySet()) {
						Project p = projectMap.get(key);
						projectMap.put(key, (Project) session.get(Project.class, p.getProjectId()));
					}
				}
			}

			tx.commit();

		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
			return "ERROR:" + e.getMessage();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		String result = "นำเข้าข้อมูลสำเร็จ " + insertedCount + " แถว, ข้าม " + skippedCount + " แถว";

		if (!duplicateStudents.isEmpty()) {
			result += "|DUPLICATE:" + String.join(",", duplicateStudents);
		}

		return result;
	}

	private String normalizeType(String type) {
		if (type == null || type.isEmpty())
			return "";

		String lower = type.toLowerCase().trim();

		if (lower.contains("mobile") || lower.contains("app") || lower.equals("m") || lower.contains("android")
				|| lower.contains("ios") || lower.contains("application")) {
			return "Mobile";
		} else if (lower.contains("web") || lower.equals("w") || lower.contains("website") || lower.contains("site")) {
			return "Web";
		}

		System.out.println("Warning: Unknown type - '" + type + "'");
		return "";
	}

	private String determineFinalProjectType(String projectKey, Map<String, Set<String>> projectTypesFound) {
		Set<String> types = projectTypesFound.get(projectKey);

		if (types == null || types.isEmpty()) {
			return "Web";
		}

		boolean hasWeb = types.contains("Web");
		boolean hasMobile = types.contains("Mobile");

		if (hasWeb && hasMobile) {
			return "Web and Mobile";
		} else if (hasMobile) {
			return "Mobile App";
		} else if (hasWeb) {
			return "Web";
		}

		return "Web";
	}

	private String getStringCell(Cell cell) {
		return cell != null ? cell.toString().trim() : "";
	}
}