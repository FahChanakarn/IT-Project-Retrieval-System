package com.springmvc.controller;

import com.springmvc.manager.AdvisorManager;
import com.springmvc.model.Advisor;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginAdvisorController {

	@RequestMapping(value = "/loginAdvisor", method = RequestMethod.GET)
	public String showLoginPage() {
		return "loginAdvisor";
	}

	@RequestMapping(value = "/loginAdvisor", method = RequestMethod.POST)
	public ModelAndView loginAdvisor(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpSession session) {

		AdvisorManager advisorManager = new AdvisorManager();
		Advisor advisor = advisorManager.findByEmailAndPassword(email, password);

		if (advisor != null && "อาจารย์ที่ปรึกษา".equalsIgnoreCase(advisor.getAdv_position())) {
			session.setAttribute("advisor", advisor);

			ModelAndView mav = new ModelAndView("loginAdvisor");
			mav.addObject("loginSuccess", true);
			return mav;
		} else {
			ModelAndView mav = new ModelAndView("loginAdvisor");
			mav.addObject("loginFailed", true);
			return mav;
		}
	}

}
