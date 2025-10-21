package com.springmvc.controller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.AdvisorManager;
import com.springmvc.manager.ToolsManager;
import com.springmvc.manager.ProjectManager;
import com.springmvc.manager.UploadManager;
import com.springmvc.model.Advisor;
import com.springmvc.model.DocumentFile;
import com.springmvc.model.Tools;
import com.springmvc.model.Project;

@Controller
public class ProjectController {

	private static final int PROJECTS_PER_PAGE = 10;

	@RequestMapping(value = "/searchProjects", method = RequestMethod.GET)
	public ModelAndView searchProjects(@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "page", defaultValue = "1") int page) {

		ProjectManager projectManager = new ProjectManager();
		AdvisorManager advisorManager = new AdvisorManager();
		ToolsManager toolsManager = new ToolsManager();

		List<Advisor> activeAdvisors = advisorManager.getActiveAdvisors();
		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing", "Web and Mobile");
		List<String> semesters = projectManager.getAllSemesters();

		List<Tools> programmingLangs = toolsManager.getToolsByType(Tools.ToolsType.PROGRAMMING);
		List<Tools> testingTools = toolsManager.getToolsByType(Tools.ToolsType.Testing);
		List<Tools> dbmsLangs = toolsManager.getToolsByType(Tools.ToolsType.DBMS);

		// ค้นหาโปรเจคทั้งหมด
		List<Project> allProjects = projectManager.searchProjects(keyword);

		// ✅ กรองเฉพาะโครงงานที่มีบทคัดย่อทั้งภาษาไทยและภาษาอังกฤษ
		List<Project> projectsWithAbstract = allProjects.stream().filter(p -> {
			boolean hasTh = p.getAbstractTh() != null && !p.getAbstractTh().trim().isEmpty();
			boolean hasEn = p.getAbstractEn() != null && !p.getAbstractEn().trim().isEmpty();
			return hasTh && hasEn;
		})
				// ✅ เรียงลำดับ: 1. Semester (ล่าสุดก่อน) 2. ชื่อโครงงานภาษาไทย (ก-ฮ)
				.sorted(Comparator.comparing(Project::getSemester, Comparator.reverseOrder())
						.thenComparing(p -> p.getProj_NameTh() != null ? p.getProj_NameTh() : ""))
				.collect(Collectors.toList());

		int totalProjects = projectsWithAbstract.size();
		int totalPages = (int) Math.ceil((double) totalProjects / PROJECTS_PER_PAGE);

		if (page < 1)
			page = 1;
		if (page > totalPages && totalPages > 0)
			page = totalPages;

		int fromIndex = (page - 1) * PROJECTS_PER_PAGE;
		int toIndex = Math.min(fromIndex + PROJECTS_PER_PAGE, totalProjects);

		List<Project> projects = (projectsWithAbstract != null && !projectsWithAbstract.isEmpty())
				? projectsWithAbstract.subList(fromIndex, toIndex)
				: projectsWithAbstract;

		ModelAndView mav = new ModelAndView("Home");
		mav.addObject("projects", projects);
		mav.addObject("advisors", activeAdvisors);
		mav.addObject("projectTypes", projectTypes);
		mav.addObject("semesters", semesters);
		mav.addObject("programmingLangs", programmingLangs);
		mav.addObject("testingTools", testingTools);
		mav.addObject("dbmsLangs", dbmsLangs);
		mav.addObject("keyword", keyword);
		mav.addObject("currentPage", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalProjects", totalProjects);

		return mav;
	}

	@RequestMapping(value = "/filterProjects", method = RequestMethod.POST)
	public ModelAndView filterProjects(@RequestParam(value = "projectType", required = false) String projectType,
			@RequestParam(value = "advisorIds", required = false) List<String> advisorIds,
			@RequestParam(value = "semesters", required = false) List<String> semesters,
			@RequestParam(value = "languages", required = false) List<String> languages,
			@RequestParam(value = "testingTools", required = false) List<String> testingTools,
			@RequestParam(value = "databases", required = false) List<String> databases,
			@RequestParam(value = "testingStatus", required = false) String testingStatus,
			@RequestParam(value = "startYear", required = false) String startYear,
			@RequestParam(value = "endYear", required = false) String endYear,
			@RequestParam(value = "page", defaultValue = "1") int page, HttpSession session) {

		ProjectManager projectManager = new ProjectManager();

		// ✅ เก็บ filter criteria ใน session
		session.setAttribute("filterProjectType", projectType);
		session.setAttribute("filterAdvisorIds", advisorIds);
		session.setAttribute("filterSemesters", semesters);
		session.setAttribute("filterLanguages", languages);
		session.setAttribute("filterTestingTools", testingTools);
		session.setAttribute("filterDatabases", databases);
		session.setAttribute("filterTestingStatus", testingStatus);
		session.setAttribute("filterStartYear", startYear);
		session.setAttribute("filterEndYear", endYear);

		// ✅ แก้ไข: ถ้าเลือก "Web" หรือ "Mobile App" ให้รวม "Web and Mobile" ด้วย
		String adjustedProjectType = projectType;
		if ("Web".equals(projectType) || "Mobile App".equals(projectType)) {
			adjustedProjectType = null;
		}

		List<Project> allProjects = projectManager.filterProjects(adjustedProjectType, advisorIds, semesters, languages,
				testingTools, databases, testingStatus, startYear, endYear);

		// ✅ กรองด้วย Java ถ้าเลือก Web หรือ Mobile App
		if ("Web".equals(projectType)) {
			allProjects = allProjects.stream()
					.filter(p -> "Web".equals(p.getProjectType()) || "Web and Mobile".equals(p.getProjectType()))
					.collect(Collectors.toList());
		} else if ("Mobile App".equals(projectType)) {
			allProjects = allProjects.stream()
					.filter(p -> "Mobile App".equals(p.getProjectType()) || "Web and Mobile".equals(p.getProjectType()))
					.collect(Collectors.toList());
		}

		// ✅ กรองเฉพาะโครงงานที่มีบทคัดย่อและเรียงลำดับ
		List<Project> projectsWithAbstract = allProjects.stream().filter(p -> {
			boolean hasTh = p.getAbstractTh() != null && !p.getAbstractTh().trim().isEmpty();
			boolean hasEn = p.getAbstractEn() != null && !p.getAbstractEn().trim().isEmpty();
			return hasTh && hasEn;
		}).sorted(Comparator.comparing(Project::getSemester, Comparator.reverseOrder())
				.thenComparing(p -> p.getProj_NameTh() != null ? p.getProj_NameTh() : "")).collect(Collectors.toList());

		int totalProjects = projectsWithAbstract.size();
		int totalPages = (int) Math.ceil((double) totalProjects / PROJECTS_PER_PAGE);

		if (page < 1)
			page = 1;
		if (page > totalPages && totalPages > 0)
			page = totalPages;

		int fromIndex = (page - 1) * PROJECTS_PER_PAGE;
		int toIndex = Math.min(fromIndex + PROJECTS_PER_PAGE, totalProjects);

		List<Project> projects = (projectsWithAbstract != null && !projectsWithAbstract.isEmpty())
				? projectsWithAbstract.subList(fromIndex, toIndex)
				: projectsWithAbstract;

		AdvisorManager advisorManager = new AdvisorManager();
		ToolsManager toolsManager = new ToolsManager();

		List<Advisor> activeAdvisors = advisorManager.getActiveAdvisors();
		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing", "Web and Mobile");
		List<String> allSemesters = projectManager.getAllSemesters();

		List<Tools> programmingLangs = toolsManager.getToolsByType(Tools.ToolsType.PROGRAMMING);
		List<Tools> testingToolsList = toolsManager.getToolsByType(Tools.ToolsType.Testing);
		List<Tools> dbmsLangs = toolsManager.getToolsByType(Tools.ToolsType.DBMS);

		ModelAndView mav = new ModelAndView("Home");
		mav.addObject("projects", projects);
		mav.addObject("advisors", activeAdvisors);
		mav.addObject("projectTypes", projectTypes);
		mav.addObject("semesters", allSemesters);
		mav.addObject("programmingLangs", programmingLangs);
		mav.addObject("testingTools", testingToolsList);
		mav.addObject("dbmsLangs", dbmsLangs);

		mav.addObject("selectedProjectType", projectType);
		mav.addObject("selectedAdvisorIds", advisorIds);
		mav.addObject("selectedSemesters", semesters);
		mav.addObject("selectedLanguages", languages);
		mav.addObject("selectedTestingTools", testingTools);
		mav.addObject("selectedDatabases", databases);
		mav.addObject("selectedTestingStatus", testingStatus);
		mav.addObject("selectedStartYear", startYear);
		mav.addObject("selectedEndYear", endYear);

		mav.addObject("currentPage", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalProjects", totalProjects);

		return mav;
	}

	@RequestMapping(value = "/filterProjects", method = RequestMethod.GET)
	public ModelAndView filterProjectsGet(@RequestParam(value = "page", defaultValue = "1") int page,
			HttpSession session) {

		String projectType = (String) session.getAttribute("filterProjectType");
		List<String> advisorIds = (List<String>) session.getAttribute("filterAdvisorIds");
		List<String> semesters = (List<String>) session.getAttribute("filterSemesters");
		List<String> languages = (List<String>) session.getAttribute("filterLanguages");
		List<String> testingTools = (List<String>) session.getAttribute("filterTestingTools");
		List<String> databases = (List<String>) session.getAttribute("filterDatabases");
		String testingStatus = (String) session.getAttribute("filterTestingStatus");
		String startYear = (String) session.getAttribute("filterStartYear");
		String endYear = (String) session.getAttribute("filterEndYear");

		ProjectManager projectManager = new ProjectManager();

		String adjustedProjectType = projectType;
		if ("Web".equals(projectType) || "Mobile App".equals(projectType)) {
			adjustedProjectType = null;
		}

		List<Project> allProjects = projectManager.filterProjects(adjustedProjectType, advisorIds, semesters, languages,
				testingTools, databases, testingStatus, startYear, endYear);

		if ("Web".equals(projectType)) {
			allProjects = allProjects.stream()
					.filter(p -> "Web".equals(p.getProjectType()) || "Web and Mobile".equals(p.getProjectType()))
					.collect(Collectors.toList());
		} else if ("Mobile App".equals(projectType)) {
			allProjects = allProjects.stream()
					.filter(p -> "Mobile App".equals(p.getProjectType()) || "Web and Mobile".equals(p.getProjectType()))
					.collect(Collectors.toList());
		}

		List<Project> projectsWithAbstract = allProjects.stream().filter(p -> {
			boolean hasTh = p.getAbstractTh() != null && !p.getAbstractTh().trim().isEmpty();
			boolean hasEn = p.getAbstractEn() != null && !p.getAbstractEn().trim().isEmpty();
			return hasTh && hasEn;
		}).sorted(Comparator.comparing(Project::getSemester, Comparator.reverseOrder())
				.thenComparing(p -> p.getProj_NameTh() != null ? p.getProj_NameTh() : "")).collect(Collectors.toList());

		int totalProjects = projectsWithAbstract.size();
		int totalPages = (int) Math.ceil((double) totalProjects / PROJECTS_PER_PAGE);

		if (page < 1)
			page = 1;
		if (page > totalPages && totalPages > 0)
			page = totalPages;

		int fromIndex = (page - 1) * PROJECTS_PER_PAGE;
		int toIndex = Math.min(fromIndex + PROJECTS_PER_PAGE, totalProjects);

		List<Project> projects = (projectsWithAbstract != null && !projectsWithAbstract.isEmpty())
				? projectsWithAbstract.subList(fromIndex, toIndex)
				: projectsWithAbstract;

		AdvisorManager advisorManager = new AdvisorManager();
		ToolsManager toolsManager = new ToolsManager();

		List<Advisor> activeAdvisors = advisorManager.getActiveAdvisors();
		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing", "Web and Mobile");
		List<String> allSemesters = projectManager.getAllSemesters();

		List<Tools> programmingLangs = toolsManager.getToolsByType(Tools.ToolsType.PROGRAMMING);
		List<Tools> testingToolsList = toolsManager.getToolsByType(Tools.ToolsType.Testing);
		List<Tools> dbmsLangs = toolsManager.getToolsByType(Tools.ToolsType.DBMS);

		ModelAndView mav = new ModelAndView("Home");
		mav.addObject("projects", projects);
		mav.addObject("advisors", activeAdvisors);
		mav.addObject("projectTypes", projectTypes);
		mav.addObject("semesters", allSemesters);
		mav.addObject("programmingLangs", programmingLangs);
		mav.addObject("testingTools", testingToolsList);
		mav.addObject("dbmsLangs", dbmsLangs);

		mav.addObject("selectedProjectType", projectType);
		mav.addObject("selectedAdvisorIds", advisorIds);
		mav.addObject("selectedSemesters", semesters);
		mav.addObject("selectedLanguages", languages);
		mav.addObject("selectedTestingTools", testingTools);
		mav.addObject("selectedDatabases", databases);
		mav.addObject("selectedTestingStatus", testingStatus);
		mav.addObject("selectedStartYear", startYear);
		mav.addObject("selectedEndYear", endYear);

		mav.addObject("currentPage", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalProjects", totalProjects);

		return mav;
	}

	@RequestMapping(value = "/viewAbstract", method = RequestMethod.POST)
	public ModelAndView viewAbstractPost(@RequestParam("projectId") int projectId, HttpSession session) {
		session.setAttribute("viewingProjectId", projectId);

		return new ModelAndView("redirect:/viewAbstract");
	}

	@RequestMapping(value = "/viewAbstract", method = RequestMethod.GET)
	public ModelAndView viewAbstract(HttpSession session) {
		ProjectManager projectManager = new ProjectManager();
		UploadManager uploadManager = new UploadManager();

		Integer projectId = (Integer) session.getAttribute("viewingProjectId");

		if (projectId == null) {
			ModelAndView mv = new ModelAndView("Home");
			mv.addObject("projects", projectManager.searchProjects(null));
			mv.addObject("error", "กรุณาเลือกโครงงานที่ต้องการดู");
			return mv;
		}

		Project project = projectManager.getProjectDetail(projectId);

		if (project == null) {
			ModelAndView mv = new ModelAndView("Home");
			mv.addObject("projects", projectManager.searchProjects(null));
			mv.addObject("error", "ไม่พบโครงงานที่ต้องการดูรายละเอียด");
			return mv;
		}

		List<DocumentFile> uploadList = uploadManager.getFilesByProject(projectId);

		ModelAndView mav = new ModelAndView("ViewAbstract");
		mav.addObject("project", project);
		mav.addObject("uploadList", uploadList);
		return mav;
	}
}