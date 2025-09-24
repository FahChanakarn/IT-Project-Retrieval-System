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
import com.springmvc.manager.TypeDBManager;
import com.springmvc.model.Advisor;
import com.springmvc.model.ProgrammingLang;
import com.springmvc.model.TypeDB;

@Controller
public class HomeController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
		AdvisorManager advisorManager = new AdvisorManager();
		ProjectManager projectManager = new ProjectManager();
		ProgrammingLangManager prolangManager = new ProgrammingLangManager();
		TypeDBManager typeDBManager = new TypeDBManager();

		List<Advisor> activeAdvisors = advisorManager.getActiveAdvisors();
		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing");
		List<String> semesters = projectManager.getAllSemesters();
		List<ProgrammingLang> programmingLangs = prolangManager.getAllProgrammingLanguages();
		List<TypeDB> typeDBs = typeDBManager.getAllTypeDBs();

		ModelAndView mav = new ModelAndView("Home");

		mav.addObject("advisors", activeAdvisors);
		mav.addObject("projectTypes", projectTypes);
		mav.addObject("semesters", semesters);
		mav.addObject("programmingLangs", programmingLangs);
		mav.addObject("typeDBs", typeDBs);
		return mav;
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public ModelAndView backhome() {
		return new ModelAndView("Home");
	}

	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate(); // 🔴 ลบ session ทั้งหมดออก
		return "redirect:/searchProjects"; // 🔁 กลับไปหน้า home หรือหน้า login
	}

}
