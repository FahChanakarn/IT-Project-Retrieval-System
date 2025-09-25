package com.springmvc.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.springmvc.manager.ProjectManager;
import com.springmvc.model.Advisor;
import com.springmvc.model.Project;

@Controller
@RequestMapping("/admin")
public class StudentProjectController {

	private ProjectManager projectManager = new ProjectManager();

	@RequestMapping("/listProjects")
	public ModelAndView listStudentProjects(@RequestParam(defaultValue = "1") int page,
			@RequestParam(value = "semester", required = false) String semester,
			@RequestParam(value = "success", required = false) String successMessage,
			@RequestParam(value = "error", required = false) String errorMessage, HttpSession session) {

		Advisor admin = (Advisor) session.getAttribute("admin");
		if (admin == null) {
			return new ModelAndView("redirect:/loginAdmin");
		}

		int pageSize = 10;
		int offset = (page - 1) * pageSize;

		ProjectManager projectManager = new ProjectManager();

		// ดึงภาคเรียนล่าสุด ถ้าไม่ได้เลือก
		if (semester == null || semester.isEmpty()) {
			semester = projectManager.getLatestSemester();
		}

		// ดึง Project เป็น List<Project>
		List<Project> projects = projectManager.getProjectsBySemester(semester);

		int totalRecords = projects.size();
		int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

		// สร้าง semester list
		int currentYear = Calendar.getInstance().get(Calendar.YEAR) + 543;
		List<String> semesterList = new ArrayList<>();
		for (int y = currentYear; y >= 2562; y--) {
			semesterList.add("2/" + y);
		}

		// Pagination (ตัด subset ของ project ตามหน้า)
		int toIndex = Math.min(offset + pageSize, projects.size());
		List<Project> pageProjects = projects.subList(offset, toIndex);

		ModelAndView mav = new ModelAndView("listStudentProject");
		mav.addObject("studentProjects", pageProjects);
		mav.addObject("currentPage", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("selectedSemester", semester);
		mav.addObject("semesterList", semesterList);

		// เพิ่ม messages
		if (successMessage != null) {
			mav.addObject("successMessage", successMessage);
		}
		if (errorMessage != null) {
			mav.addObject("errorMessage", errorMessage);
		}

		return mav;
	}

	@RequestMapping("/viewProjectDetail")
	public ModelAndView viewProjectDetail(@RequestParam("projectId") int projectId, HttpSession session) {

		System.out.println("DEBUG: viewProjectDetail called with projectId = " + projectId);

		Advisor admin = (Advisor) session.getAttribute("admin");
		if (admin == null) {
			return new ModelAndView("redirect:/loginAdmin");
		}

		try {
			Project project = projectManager.findProjectForEditAbstract(projectId);

			if (project == null) {
				System.out.println("DEBUG: Project not found: " + projectId);
				ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
				mav.addObject("error", "ไม่พบข้อมูลโครงงาน");
				return mav;
			}

			System.out.println("DEBUG: Project found: " + project.getProj_NameTh());

			ModelAndView mav = new ModelAndView("viewProjectDetail");
			mav.addObject("project", project);
			return mav;

		} catch (Exception e) {
			System.out.println("DEBUG: Exception in viewProjectDetail: " + e.getMessage());
			e.printStackTrace();
			ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
			mav.addObject("error", "เกิดข้อผิดพลาดในการดึงข้อมูล");
			return mav;
		}
	}

	public List<String> generateSemesterList(int startYear, int endYear) {
		List<String> list = new ArrayList<>();
		for (int year = endYear; year >= startYear; year--) {
			list.add("2/" + year);
		}
		return list;
	}

	@RequestMapping(value = "/approveUpload", method = RequestMethod.POST)
	public ModelAndView approveUpload(@RequestParam("projectId") int projectId, HttpSession session) {

		System.out.println("DEBUG: approveUpload called with projectId = " + projectId);

		Advisor admin = (Advisor) session.getAttribute("admin");
		if (admin == null) {
			return new ModelAndView("redirect:/loginAdmin");
		}

		try {
			ProjectManager projectManager = new ProjectManager();
			Project project = projectManager.findProjectById(projectId);

			if (project == null) {
				System.out.println("DEBUG: Project not found for approval: " + projectId);
				ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
				mav.addObject("error", "ไม่พบข้อมูลโครงงาน");
				return mav;
			}

			// ตรวจสอบว่าอนุมัติแล้วหรือยัง
			if ("1".equals(project.getApproveStatus()) || "approved".equalsIgnoreCase(project.getApproveStatus())) {
				System.out.println("DEBUG: Project already approved: " + projectId);
				ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
				mav.addObject("error", "โครงงานนี้ได้รับการอนุมัติแล้ว");
				return mav;
			}

			// อนุมัติโครงงาน
			project.setApproveStatus("1"); // หรือ "approved" ตามที่ระบบใช้
			project.setApproveDate(new java.util.Date());

			projectManager.updateProject(project);

			System.out.println("DEBUG: Project approved successfully: " + projectId);

			ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
			mav.addObject("success", "อนุมัติโครงงานเรียบร้อยแล้ว");
			return mav;

		} catch (Exception e) {
			System.out.println("DEBUG: Exception in approveUpload: " + e.getMessage());
			e.printStackTrace();
			ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
			mav.addObject("error", "เกิดข้อผิดพลาดในการอนุมัติ");
			return mav;
		}
	}

	@RequestMapping(value = "/removeStudent", method = RequestMethod.POST)
	public ModelAndView removeStudent(@RequestParam("projectId") int projectId, HttpSession session) {

		try {
			Advisor admin = (Advisor) session.getAttribute("admin");
			if (admin == null) {
				return new ModelAndView("redirect:/loginAdmin");
			}

			ProjectManager projectManager = new ProjectManager();

			// ตรวจสอบว่าโครงงานมีอยู่จริง
			if (!projectManager.projectExists(projectId)) {
				ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
				mav.addObject("error", "ไม่พบข้อมูลโครงงาน");
				return mav;
			}

			// ตรวจสอบว่านักศึกษาแก้ไขบทคัดย่อแล้วหรือไม่
			boolean hasEditedAbstract = projectManager.hasStudentEditedAbstract(projectId);

			if (hasEditedAbstract) {
				ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
				mav.addObject("error", "ไม่สามารถลบได้ เนื่องจากนักศึกษาได้ทำการแก้ไขบทคัดย่อแล้ว");
				return mav;
			}

			boolean deleted = projectManager.deleteProjectAndStudents(projectId);

			ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
			if (deleted) {
				mav.addObject("success", "ลบข้อมูลเรียบร้อยแล้ว");
			} else {
				mav.addObject("error", "เกิดข้อผิดพลาดในการลบข้อมูล");
			}

			return mav;

		} catch (Exception e) {
			System.out.println("DEBUG: Exception occurred: " + e.getMessage());
			e.printStackTrace();
			ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
			mav.addObject("error", "เกิดข้อผิดพลาดในระบบ: " + e.getMessage());
			return mav;
		}
	}
}