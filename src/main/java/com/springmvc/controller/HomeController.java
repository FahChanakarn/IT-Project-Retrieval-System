package com.springmvc.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpSession;
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
public class HomeController {

	private static final int PROJECTS_PER_PAGE = 10;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(@RequestParam(value = "page", defaultValue = "1") int page, HttpSession session) {
		AdvisorManager advisorManager = new AdvisorManager();
		ProjectManager projectManager = new ProjectManager();
		ProgrammingLangManager programmingLangManager = new ProgrammingLangManager();

		List<Advisor> activeAdvisors = advisorManager.getActiveAdvisors();
		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing", "Web and Mobile");
		List<String> semesters = projectManager.getAllSemesters();

		List<ProgrammingLang> programmingLangs = programmingLangManager
				.getLanguagesByType(ProgrammingLang.LangType.PROGRAMMING);
		List<ProgrammingLang> dbmsLangs = programmingLangManager.getLanguagesByType(ProgrammingLang.LangType.DBMS);

		// ดึงโปรเจคทั้งหมด
		List<Project> allProjects = projectManager.getAllProjects();

		// ✅ กรองเฉพาะโครงงานที่มีบทคัดย่อทั้งภาษาไทยและภาษาอังกฤษ
		List<Project> projectsWithAbstract = allProjects.stream().filter(p -> {
			// Debug: แสดงข้อมูลของแต่ละ project
			System.out.println("Project ID: " + p.getProjectId());
			System.out.println("Abstract TH: [" + p.getAbstractTh() + "]");
			System.out.println("Abstract EN: [" + p.getAbstractEn() + "]");

			boolean hasTh = p.getAbstractTh() != null && !p.getAbstractTh().trim().isEmpty();
			boolean hasEn = p.getAbstractEn() != null && !p.getAbstractEn().trim().isEmpty();

			System.out.println("Has TH: " + hasTh + ", Has EN: " + hasEn);
			System.out.println("---");

			return hasTh && hasEn;
		}).toList();

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
				: List.of(); // ถ้าไม่มีโปรเจค ให้เป็น empty list

		ModelAndView mav = new ModelAndView("Home");
		mav.addObject("advisors", activeAdvisors);
		mav.addObject("projectTypes", projectTypes);
		mav.addObject("semesters", semesters);
		mav.addObject("programmingLangs", programmingLangs);
		mav.addObject("dbmsLangs", dbmsLangs);
		mav.addObject("projects", projects);
		mav.addObject("currentPage", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalProjects", totalProjects);

		// ✅ แสดง popup ถ้าต้องการ
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