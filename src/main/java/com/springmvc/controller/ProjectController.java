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
import com.springmvc.model.Advisor;
import com.springmvc.model.ProgrammingLang;
import com.springmvc.model.Project;

@Controller
public class ProjectController {

	private static final int PROJECTS_PER_PAGE = 10;

	@RequestMapping(value = "/searchProjects", method = RequestMethod.GET)
	public ModelAndView searchProjects(@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "page", defaultValue = "1") int page) {

		ProjectManager projectManager = new ProjectManager();
		AdvisorManager advisorManager = new AdvisorManager();
		ProgrammingLangManager programmingLangManager = new ProgrammingLangManager();

		List<Advisor> activeAdvisors = advisorManager.getActiveAdvisors();
		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing", "Web and Mobile");
		List<String> semesters = projectManager.getAllSemesters();

		// แยกภาษาตาม type
		List<ProgrammingLang> programmingLangs = programmingLangManager
				.getLanguagesByType(ProgrammingLang.LangType.PROGRAMMING);
		List<ProgrammingLang> dbmsLangs = programmingLangManager.getLanguagesByType(ProgrammingLang.LangType.DBMS);

		// ค้นหาโปรเจคทั้งหมด
		List<Project> allProjects = projectManager.searchProjects(keyword);
		int totalProjects = allProjects != null ? allProjects.size() : 0;
		int totalPages = (int) Math.ceil((double) totalProjects / PROJECTS_PER_PAGE);

		// ตรวจสอบหน้าที่ขอมา
		if (page < 1)
			page = 1;
		if (page > totalPages && totalPages > 0)
			page = totalPages;

		// คำนวณ index สำหรับ subList
		int fromIndex = (page - 1) * PROJECTS_PER_PAGE;
		int toIndex = Math.min(fromIndex + PROJECTS_PER_PAGE, totalProjects);

		List<Project> projects = (allProjects != null && !allProjects.isEmpty())
				? allProjects.subList(fromIndex, toIndex)
				: allProjects;

		ModelAndView mav = new ModelAndView("Home");
		mav.addObject("projects", projects);
		mav.addObject("advisors", activeAdvisors);
		mav.addObject("projectTypes", projectTypes);
		mav.addObject("semesters", semesters);
		mav.addObject("programmingLangs", programmingLangs);
		mav.addObject("dbmsLangs", dbmsLangs);
		mav.addObject("keyword", keyword);
		mav.addObject("currentPage", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalProjects", totalProjects);

		return mav;
	}

	@RequestMapping(value = "/filterProjects", method = RequestMethod.GET)
	public ModelAndView filterProjects(@RequestParam(value = "projectType", required = false) String projectType,
			@RequestParam(value = "advisorIds", required = false) List<String> advisorIds,
			@RequestParam(value = "semesters", required = false) List<String> semesters,
			@RequestParam(value = "languages", required = false) List<String> languages,
			@RequestParam(value = "databases", required = false) List<String> databases,
			@RequestParam(value = "testingStatus", required = false) String testingStatus,
			@RequestParam(value = "startYear", required = false) String startYear,
			@RequestParam(value = "endYear", required = false) String endYear,
			@RequestParam(value = "page", defaultValue = "1") int page) {

		ProjectManager projectManager = new ProjectManager();

		// กรองโปรเจคทั้งหมด
		List<Project> allProjects = projectManager.filterProjects(projectType, advisorIds, semesters, languages,
				databases, testingStatus, startYear, endYear);

		int totalProjects = allProjects != null ? allProjects.size() : 0;
		int totalPages = (int) Math.ceil((double) totalProjects / PROJECTS_PER_PAGE);

		// ตรวจสอบหน้าที่ขอมา
		if (page < 1)
			page = 1;
		if (page > totalPages && totalPages > 0)
			page = totalPages;

		// คำนวณ index สำหรับ subList
		int fromIndex = (page - 1) * PROJECTS_PER_PAGE;
		int toIndex = Math.min(fromIndex + PROJECTS_PER_PAGE, totalProjects);

		List<Project> projects = (allProjects != null && !allProjects.isEmpty())
				? allProjects.subList(fromIndex, toIndex)
				: allProjects;

		AdvisorManager advisorManager = new AdvisorManager();
		ProgrammingLangManager programmingLangManager = new ProgrammingLangManager();

		List<Advisor> activeAdvisors = advisorManager.getActiveAdvisors();
		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing", "Web and Mobile");
		List<String> allSemesters = projectManager.getAllSemesters();

		// แยกภาษาตาม type
		List<ProgrammingLang> programmingLangs = programmingLangManager
				.getLanguagesByType(ProgrammingLang.LangType.PROGRAMMING);
		List<ProgrammingLang> dbmsLangs = programmingLangManager.getLanguagesByType(ProgrammingLang.LangType.DBMS);

		ModelAndView mav = new ModelAndView("Home");
		mav.addObject("projects", projects);
		mav.addObject("advisors", activeAdvisors);
		mav.addObject("projectTypes", projectTypes);
		mav.addObject("semesters", allSemesters);
		mav.addObject("programmingLangs", programmingLangs);
		mav.addObject("dbmsLangs", dbmsLangs);

		// ส่งค่าที่เลือกกลับไป JSP
		mav.addObject("selectedProjectType", projectType);
		mav.addObject("selectedAdvisorIds", advisorIds);
		mav.addObject("selectedSemesters", semesters);
		mav.addObject("selectedLanguages", languages);
		mav.addObject("selectedDatabases", databases);
		mav.addObject("selectedTestingStatus", testingStatus);
		mav.addObject("selectedStartYear", startYear);
		mav.addObject("selectedEndYear", endYear);

		// ส่งข้อมูล pagination
		mav.addObject("currentPage", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalProjects", totalProjects);

		return mav;
	}

	@RequestMapping(value = "/viewAbstract", method = RequestMethod.GET)
	public ModelAndView viewAbstract(@RequestParam("projectId") int projectId) {
		ProjectManager projectManager = new ProjectManager();
		Project project = projectManager.getProjectDetail(projectId);

		if (project == null) {
			ModelAndView mv = new ModelAndView("Home");
			mv.addObject("projects", projectManager.searchProjects(null));
			mv.addObject("error", "ไม่พบโครงงานที่ต้องการดูรายละเอียด");
			return mv;
		}

		ModelAndView mav = new ModelAndView("ViewAbstract");
		mav.addObject("project", project);
		return mav;
	}
}