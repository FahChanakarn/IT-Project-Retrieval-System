package com.springmvc.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

	@RequestMapping("/myAdviseeProjects")
	public ModelAndView listStudentProjects(@RequestParam(defaultValue = "1") int page,
			@RequestParam(value = "semester", required = false) String semester, HttpSession session) {
		Advisor advisor = (Advisor) session.getAttribute("advisor");
		if (advisor == null) {
			return new ModelAndView("redirect:/loginAdvisor");
		}

		if (semester == null || semester.isEmpty()) {
			semester = projectManager.getLatestSemester();
		}

		// ✅ ดึงข้อมูลแบบ Group by Project
		List<Map<String, Object>> allProjectGroups = projectManager
				.getProjectGroupsByAdvisorAndSemester(advisor.getAdvisorId(), semester);

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
		return mav;
	}

	@PostMapping("/approveUploadAjax")
	@ResponseBody
	public String approveUploadAjax(@RequestParam("projectId") int projectId, HttpSession session) {
	    // ✅ เช็คทั้ง advisor และ admin
	    if (session.getAttribute("advisor") == null && session.getAttribute("admin") == null) {
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
	    // ✅ เช็คทั้ง advisor และ admin
	    if (session.getAttribute("advisor") == null && session.getAttribute("admin") == null) {
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
	public String togglePublish(@RequestParam("fileId") Integer fileId, @RequestParam("published") Boolean published, HttpSession session) {
	    if (session.getAttribute("advisor") == null && session.getAttribute("admin") == null) {
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

	@RequestMapping(value = "/viewProjectDetail", method = RequestMethod.POST)
	public ModelAndView viewProjectDetailPost(@RequestParam("projectId") int projectId, HttpSession session) {
		// ✅ เช็คทั้ง advisor และ admin
		Advisor advisor = (Advisor) session.getAttribute("advisor");
		Advisor admin = (Advisor) session.getAttribute("admin");

		if (advisor == null && admin == null) {
			// ถ้าไม่ใช่ทั้ง advisor และ admin ให้ redirect ไป loginAdvisor
			return new ModelAndView("redirect:/loginAdvisor");
		}

		// เก็บ projectId ใน session
		session.setAttribute("advisorViewingProjectId", projectId);

		// Redirect ไปยัง GET method
		return new ModelAndView("redirect:/advisor/viewProjectDetail");
	}

	@RequestMapping(value = "/viewProjectDetail", method = RequestMethod.GET)
	public ModelAndView viewProjectDetail(HttpSession session) {
		// ✅ เช็คทั้ง advisor และ admin
		Advisor advisor = (Advisor) session.getAttribute("advisor");
		Advisor admin = (Advisor) session.getAttribute("admin");

		if (advisor == null && admin == null) {
			return new ModelAndView("redirect:/loginAdvisor");
		}

		// ดึง projectId จาก session
		Integer projectId = (Integer) session.getAttribute("advisorViewingProjectId");

		if (projectId == null) {
			return new ModelAndView("redirect:/advisor/myAdviseeProjects");
		}

		Project project = projectManager.findProjectById(projectId);

		if (project == null) {
			return new ModelAndView("redirect:/advisor/myAdviseeProjects");
		}

		ModelAndView mav = new ModelAndView("viewProjectDetail");
		mav.addObject("project", project);
		return mav;
	}
}