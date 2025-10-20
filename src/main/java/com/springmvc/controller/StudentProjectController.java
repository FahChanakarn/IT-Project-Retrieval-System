package com.springmvc.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import com.springmvc.manager.ProjectManager;
import com.springmvc.manager.DocumentFileManager;
import com.springmvc.model.Advisor;
import com.springmvc.model.Project;
import com.springmvc.model.DocumentFile;

@Controller
@RequestMapping("/admin")
public class StudentProjectController {

	private ProjectManager projectManager = new ProjectManager();

	// ============== รายการโครงงานของนักศึกษาในที่ปรึกษา (Admin View)
	// ==============
	@RequestMapping("/myAdviseeProjects")
	public ModelAndView listMyAdviseeProjects(@RequestParam(defaultValue = "1") int page,
			@RequestParam(value = "semester", required = false) String semester, HttpSession session) {

		Advisor admin = (Advisor) session.getAttribute("admin");
		if (admin == null) {
			return new ModelAndView("redirect:/loginAdmin");
		}

		if (semester == null || semester.isEmpty()) {
			semester = projectManager.getLatestSemester();
		}

		// ✅ ดึงข้อมูลแบบ Group by Project
		List<Map<String, Object>> allProjectGroups = projectManager
				.getProjectGroupsByAdvisorAndSemester(admin.getAdvisorId(), semester);

		// Pagination
		int pageSize = 10;
		int totalProjects = allProjectGroups.size();
		int totalPages = (int) Math.ceil((double) totalProjects / pageSize);

		int start = (page - 1) * pageSize;
		int end = Math.min(start + pageSize, totalProjects);

		List<Map<String, Object>> paginatedGroups = allProjectGroups.subList(start, end);

		// สร้าง semester list
		List<String> semesterList = new ArrayList<>();
		int currentYear = Calendar.getInstance().get(Calendar.YEAR) + 543;
		for (int y = currentYear; y >= 2562; y--) {
			semesterList.add("2/" + y);
		}

		ModelAndView mav = new ModelAndView("listProjectStudent");
		mav.addObject("projectGroups", paginatedGroups);
		mav.addObject("currentPage", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("selectedSemester", semester);
		mav.addObject("semesterList", semesterList);
		mav.addObject("userRole", "admin");
		return mav;
	}

	// ============== รายการโครงงานทั้งหมด (Admin) ==============
	@RequestMapping("/listProjects")
	public ModelAndView listStudentProjects(@RequestParam(defaultValue = "1") int page,
			@RequestParam(value = "semester", required = false) String semester,
			@RequestParam(value = "success", required = false) String successMessage,
			@RequestParam(value = "error", required = false) String errorMessage, HttpSession session) {

		Advisor admin = (Advisor) session.getAttribute("admin");
		if (admin == null) {
			return new ModelAndView("redirect:/loginAdmin");
		}

		if (semester == null || semester.isEmpty()) {
			semester = projectManager.getLatestSemester();
		}

		// ✅ ดึงข้อมูลแบบ Group by Project (ทั้งหมด)
		List<Map<String, Object>> allProjectGroups = projectManager.getAllProjectGroupsBySemester(semester);

		// Pagination
		int pageSize = 10;
		int totalProjects = allProjectGroups.size();
		int totalPages = (int) Math.ceil((double) totalProjects / pageSize);

		int start = (page - 1) * pageSize;
		int end = Math.min(start + pageSize, totalProjects);

		List<Map<String, Object>> paginatedGroups = allProjectGroups.subList(start, end);

		// สร้าง semester list
		int currentYear = Calendar.getInstance().get(Calendar.YEAR) + 543;
		List<String> semesterList = new ArrayList<>();
		for (int y = currentYear; y >= 2562; y--) {
			semesterList.add("2/" + y);
		}

		ModelAndView mav = new ModelAndView("listStudentProject");
		mav.addObject("projectGroups", paginatedGroups);
		mav.addObject("currentPage", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("selectedSemester", semester);
		mav.addObject("semesterList", semesterList);

		if (successMessage != null) {
			mav.addObject("successMessage", successMessage);
		}
		if (errorMessage != null) {
			mav.addObject("errorMessage", errorMessage);
		}

		return mav;
	}

	@PostMapping("/approveUploadAjax")
	@ResponseBody
	public String approveUploadAjax(@RequestParam("projectId") int projectId, HttpSession session) {
		if (session.getAttribute("admin") == null) {
			return "unauthorized";
		}

		try {
			boolean success = projectManager.approveProjectUpload(projectId);
			return success ? "success" : "fail";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	@PostMapping("/updateTestingStatusAjax")
	@ResponseBody
	public String updateTestingStatusAjax(@RequestParam("projectId") int projectId,
			@RequestParam("testingStatus") String testingStatus, HttpSession session) {
		if (session.getAttribute("admin") == null) {
			return "unauthorized";
		}

		try {
			boolean success = projectManager.updateTestingStatus(projectId, testingStatus);
			return success ? "success" : "fail";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	@PostMapping("/document/togglePublish")
	@ResponseBody
	public String togglePublish(@RequestParam("fileId") Integer fileId, @RequestParam("published") Boolean published,
			HttpSession session) {

		if (session.getAttribute("admin") == null) {
			return "unauthorized";
		}

		try {
			DocumentFile file = DocumentFileManager.findById(fileId);
			if (file != null) {
				file.setPublishStatus(published);
				DocumentFileManager.updateFile(file);
				return "success";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "fail";
	}

	@RequestMapping("/viewProjectDetail")
	public ModelAndView viewProjectDetail(@RequestParam("projectId") int projectId, HttpSession session) {

		Advisor admin = (Advisor) session.getAttribute("admin");
		if (admin == null) {
			return new ModelAndView("redirect:/loginAdmin");
		}

		try {
			Project project = projectManager.findProjectForEditAbstract(projectId);

			if (project == null) {
				ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
				mav.addObject("error", "ไม่พบข้อมูลโครงงาน");
				return mav;
			}

			ModelAndView mav = new ModelAndView("viewProjectDetail");
			mav.addObject("project", project);
			mav.addObject("userRole", "admin");
			return mav;

		} catch (Exception e) {
			e.printStackTrace();
			ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
			mav.addObject("error", "เกิดข้อผิดพลาดในการดึงข้อมูล");
			return mav;
		}
	}

	@PostMapping("/deleteProject")
	@ResponseBody
	public String deleteProject(@RequestParam("projectId") int projectId, HttpSession session) {
		if (session.getAttribute("admin") == null) {
			return "unauthorized";
		}

		try {
			boolean success = projectManager.deleteProjectAndStudents(projectId);
			return success ? "success" : "fail";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	@RequestMapping(value = "/removeStudent", method = RequestMethod.POST)
	public ModelAndView removeStudent(@RequestParam("projectId") int projectId, HttpSession session) {

		try {
			Advisor admin = (Advisor) session.getAttribute("admin");
			if (admin == null) {
				return new ModelAndView("redirect:/loginAdmin");
			}

			if (!projectManager.projectExists(projectId)) {
				ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
				mav.addObject("error", "ไม่พบข้อมูลโครงงาน");
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
			e.printStackTrace();
			ModelAndView mav = new ModelAndView("redirect:/admin/listProjects");
			mav.addObject("error", "เกิดข้อผิดพลาดในระบบ: " + e.getMessage());
			return mav;
		}
	}
}