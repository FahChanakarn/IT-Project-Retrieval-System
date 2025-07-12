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

		if (admin != null && admin.getAdvisorId() != null && admin.getAdvisorId().startsWith("A")) {
			// ✅ ตรวจสอบว่าเป็น admin จริง
			HttpSession session = request.getSession();
			session.setAttribute("admin", admin); // เก็บใน session เป็น admin

			return new ModelAndView("redirect:/searchProjects");
		} else {
			ModelAndView mav = new ModelAndView("loginAdmin");
			mav.addObject("error", "ไม่สามารถเข้าสู่ระบบได้ กรุณาตรวจสอบอีเมล รหัสผ่าน หรือสิทธิ์ผู้ดูแลระบบ");
			return mav;
		}
	}
}
