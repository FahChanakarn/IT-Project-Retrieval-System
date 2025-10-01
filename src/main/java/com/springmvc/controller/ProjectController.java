package com.springmvc.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.AdvisorManager;
import com.springmvc.manager.ProgrammingLangManager;
import com.springmvc.manager.ProjectManager;
import com.springmvc.manager.TypeDBManager;
import com.springmvc.model.Advisor;
import com.springmvc.model.ProgrammingLang;
import com.springmvc.model.Project;
import com.springmvc.model.TypeDB;

@Controller
public class ProjectController {

	@RequestMapping(value = "/searchProjects", method = RequestMethod.GET)
	public ModelAndView searchProjects(@RequestParam(value = "keyword", required = false) String keyword) {
		ProjectManager projectManager = new ProjectManager();
		AdvisorManager advisorManager = new AdvisorManager();
		ProgrammingLangManager programmingLangManager = new ProgrammingLangManager();
		TypeDBManager typeDBManager = new TypeDBManager();

		List<Advisor> activeAdvisors = advisorManager.getActiveAdvisors();
		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing", "Web and Mobile");
		List<String> semesters = projectManager.getAllSemesters();
		List<ProgrammingLang> programmingLangs = programmingLangManager.getAllProgrammingLanguages();
		List<TypeDB> typeDBs = typeDBManager.getAllTypeDBs();

		List<Project> projects = projectManager.searchProjects(keyword);

		ModelAndView mav = new ModelAndView("Home");
		mav.addObject("projects", projects);
		mav.addObject("advisors", activeAdvisors);
		mav.addObject("projectTypes", projectTypes); // ✅ ส่งไป JSP
		mav.addObject("semesters", semesters);
		mav.addObject("programmingLangs", programmingLangs);
		mav.addObject("typeDBs", typeDBs);
		mav.addObject("keyword", keyword);

		return mav;
	}

	@RequestMapping(value = "/filterProjects", method = RequestMethod.GET)
	public ModelAndView filterProjects(@RequestParam(value = "projectType", required = false) String projectType,
			@RequestParam(value = "advisorIds", required = false) List<String> advisorIds,
			@RequestParam(value = "semesters", required = false) List<String> semesters,
			@RequestParam(value = "typeDBIds", required = false) List<Integer> typeDBIds,
			@RequestParam(value = "languages", required = false) List<String> languages,
			@RequestParam(value = "testingStatus", required = false) String testingStatus,
			@RequestParam(value = "startYear", required = false) String startYear,
			@RequestParam(value = "endYear", required = false) String endYear) {

		ProjectManager projectManager = new ProjectManager();
		List<Project> projects = projectManager.filterProjects(projectType, advisorIds, semesters, typeDBIds, languages,
				testingStatus, startYear, endYear);

		AdvisorManager advisorManager = new AdvisorManager();
		ProgrammingLangManager programmingLangManager = new ProgrammingLangManager();
		TypeDBManager typeDBManager = new TypeDBManager();

		List<Advisor> activeAdvisors = advisorManager.getActiveAdvisors();
		// 🔹 ใช้ค่าคงที่เหมือนกัน
		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing", "Web and Mobile");
		List<String> allSemesters = projectManager.getAllSemesters();
		List<ProgrammingLang> programmingLangs = programmingLangManager.getAllProgrammingLanguages();
		List<TypeDB> typeDBs = typeDBManager.getAllTypeDBs();

		ModelAndView mav = new ModelAndView("Home");
		mav.addObject("projects", projects);
		mav.addObject("advisors", activeAdvisors);
		mav.addObject("projectTypes", projectTypes);
		mav.addObject("semesters", allSemesters);
		mav.addObject("programmingLangs", programmingLangs);
		mav.addObject("typeDBs", typeDBs);

		// ส่งค่าที่เลือกกลับไป JSP
		mav.addObject("selectedProjectType", projectType);
		mav.addObject("selectedAdvisorIds", advisorIds);
		mav.addObject("selectedSemesters", semesters);
		mav.addObject("selectedTypeDBIds", typeDBIds);
		mav.addObject("selectedLanguages", languages);
		mav.addObject("selectedTestingStatus", testingStatus);
		mav.addObject("selectedStartYear", startYear);
		mav.addObject("selectedEndYear", endYear);

		return mav;
	}

	@RequestMapping(value = "/viewAbstract", method = RequestMethod.GET)
	public ModelAndView viewAbstract(@RequestParam("projectId") int projectId) {
		ProjectManager projectManager = new ProjectManager();
		Project project = projectManager.getProjectDetail(projectId);

		// ถ้าไม่เจอ project ให้กลับหน้า Home พร้อมข้อความ (ปรับตามระบบคุณได้)
		if (project == null) {
			ModelAndView mv = new ModelAndView("Home");
			mv.addObject("projects", projectManager.searchProjects(null)); // โหลดรายการทั้งหมด/ล่าสุด
			mv.addObject("error", "ไม่พบโครงงานที่ต้องการดูรายละเอียด");
			return mv;
		}

		ModelAndView mav = new ModelAndView("ViewAbstract"); // ชื่อ JSP: viewAbstract.jsp
		mav.addObject("project", project);
		return mav;
	}
}
