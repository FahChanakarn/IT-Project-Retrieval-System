package com.springmvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.springmvc.manager.AdvisorManager;
import com.springmvc.model.Advisor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginAdminController {

	@RequestMapping(value = "/loginAdmin", method = RequestMethod.GET)
	public ModelAndView showLoginPage() {
		return new ModelAndView("loginAdmin");
	}

	@RequestMapping(value = "/loginAdmin", method = RequestMethod.POST)
	public ModelAndView loginAdmin(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpServletRequest request) {

		AdvisorManager advisorManager = new AdvisorManager();
		Advisor admin = advisorManager.findByEmailAndPassword(email, password);

		if (admin != null && "อาจารย์ประสานงาน".equals(admin.getAdv_position())) {
			HttpSession session = request.getSession();
			session.setAttribute("admin", admin); // เก็บใน session เป็น admin

			ModelAndView mav = new ModelAndView("loginAdmin");
			mav.addObject("loginSuccess", true);
			return mav;
		} else {
			ModelAndView mav = new ModelAndView("loginAdmin");
			mav.addObject("loginFailed", true);
			return mav;
		}
	}
}
