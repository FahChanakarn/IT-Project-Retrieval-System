package com.springmvc.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.AdvisorManager;
import com.springmvc.manager.ProgrammingLangManager;
import com.springmvc.manager.ProjectManager;
import com.springmvc.model.Advisor;
import com.springmvc.model.ProgrammingLang;

@Controller
public class HomeController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
		AdvisorManager advisorManager = new AdvisorManager();
		ProjectManager projectManager = new ProjectManager();
		ProgrammingLangManager programmingLangManager = new ProgrammingLangManager();

		List<Advisor> activeAdvisors = advisorManager.getActiveAdvisors();
		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing", "Web and Mobile");
		List<String> semesters = projectManager.getAllSemesters();

		// ใช้ method ใหม่แยก type จาก manager
		List<ProgrammingLang> programmingLangs = programmingLangManager
				.getLanguagesByType(ProgrammingLang.LangType.PROGRAMMING);
		List<ProgrammingLang> dbmsLangs = programmingLangManager.getLanguagesByType(ProgrammingLang.LangType.DBMS);

		ModelAndView mav = new ModelAndView("Home");
		mav.addObject("advisors", activeAdvisors);
		mav.addObject("projectTypes", projectTypes);
		mav.addObject("semesters", semesters);
		mav.addObject("programmingLangs", programmingLangs);
		mav.addObject("dbmsLangs", dbmsLangs);

		return mav;
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public ModelAndView backhome() {
		return new ModelAndView("Home");
	}

	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/searchProjects";
	}
}
