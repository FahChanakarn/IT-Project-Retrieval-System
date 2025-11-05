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
import com.springmvc.model.Advisor;
import com.springmvc.model.Tools;
import com.springmvc.model.Project;

@Controller
public class HomeController {

	private static final int PROJECTS_PER_PAGE = 10;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(@RequestParam(value = "page", defaultValue = "1") int page, HttpSession session) {
		AdvisorManager advisorManager = new AdvisorManager();
		ProjectManager projectManager = new ProjectManager();
		ToolsManager toolsManager = new ToolsManager();

		List<Advisor> activeAdvisors = advisorManager.getActiveAdvisors();
		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing", "Web and Mobile");
		List<String> semesters = projectManager.getAllSemesters();

		List<Tools> programmingLangs = toolsManager.getToolsByType(Tools.ToolsType.PROGRAMMING);
		List<Tools> testingTools = toolsManager.getToolsByType(Tools.ToolsType.Testing);
		List<Tools> dbmsLangs = toolsManager.getToolsByType(Tools.ToolsType.DBMS);

		List<Project> allProjects = projectManager.getAllProjects();

		// ✅ กรองเฉพาะโครงงานที่มีบทคัดย่อและเรียงลำดับ
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
				: List.of();

		ModelAndView mav = new ModelAndView("index");
		mav.addObject("advisors", activeAdvisors);
		mav.addObject("projectTypes", projectTypes);
		mav.addObject("semesters", semesters);
		mav.addObject("programmingLangs", programmingLangs);
		mav.addObject("testingTools", testingTools);
		mav.addObject("dbmsLangs", dbmsLangs);
		mav.addObject("projects", projects);
		mav.addObject("currentPage", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalProjects", totalProjects);

		Boolean showPopup = (Boolean) session.getAttribute("showWelcomePopup");
		if (showPopup != null && showPopup) {
			mav.addObject("showWelcomePopup", true);
			session.removeAttribute("showWelcomePopup");
		}

		return mav;
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public ModelAndView backhome() {
		return new ModelAndView("redirect:/");
	}

	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/searchProjects";
	}
}