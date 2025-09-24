package com.springmvc.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.DocumentFileManager;
import com.springmvc.manager.ProjectManager;
import com.springmvc.model.Advisor;
import com.springmvc.model.DocumentFile;
import com.springmvc.model.Project;

@Controller
@RequestMapping("/advisor")
public class AdvisorProjectController {

	private ProjectManager projectManager = new ProjectManager();

	@RequestMapping("/listProjects")
	public ModelAndView listStudentProjects(@RequestParam(defaultValue = "1") int page,
			@RequestParam(value = "semester", required = false) String semester, HttpSession session) {
		Advisor advisor = (Advisor) session.getAttribute("advisor");
		if (advisor == null) {
			return new ModelAndView("redirect:/loginAdvisor");
		}

		if (semester == null || semester.isEmpty()) {
			semester = projectManager.getLatestSemester();
		}

		int pageSize = 10;
		int offset = (page - 1) * pageSize;

		List<Object[]> projectList = projectManager.getStudentProjectsByAdvisorAndSemester(advisor.getAdvisorId(),
				semester, offset, pageSize);

		int totalRecords = projectManager.countProjectsByAdvisorAndSemester(advisor.getAdvisorId(), semester);
		int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

		List<String> semesterList = new ArrayList<>();
		int currentYear = Calendar.getInstance().get(Calendar.YEAR) + 543;
		for (int y = currentYear; y >= 2562; y--) {
			semesterList.add("2/" + y);
		}

		ModelAndView mav = new ModelAndView("listProjectStudent");
		mav.addObject("projects", projectList);
		mav.addObject("currentPage", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("selectedSemester", semester);
		mav.addObject("semesterList", semesterList);
		return mav;
	}

	@PostMapping("/approveUploadAjax")
	@ResponseBody
	public String approveUploadAjax(@RequestParam("projectId") int projectId) {
		Project project = projectManager.findProjectById(projectId);
		if (project != null) {
			project.setApproveStatus("approved");
			project.setApproveDate(new java.util.Date()); // เพิ่มวันที่อนุมัติ
			projectManager.updateProject(project);
			return "success"; // ส่งกลับไปให้ JS
		}
		return "fail";
	}

	@RequestMapping("/viewProjectDetail")
	public ModelAndView viewProjectDetail(@RequestParam("projectId") int projectId) {
		Project project = projectManager.findProjectById(projectId);
		ModelAndView mav = new ModelAndView("viewProjectDetail");
		mav.addObject("project", project);
		return mav;
	}

	@PostMapping("/document/togglePublish")
	@ResponseBody
	public String togglePublish(@RequestParam("fileId") Integer fileId, @RequestParam("published") Boolean published) {
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

	/*
	 * public List<String> generateSemesterList(int startYear, int endYear) {
	 * List<String> list = new ArrayList<>(); for (int year = endYear; year >=
	 * startYear; year--) { list.add("2/" + year); list.add("1/" + year); } return
	 * list; }
	 */

}
