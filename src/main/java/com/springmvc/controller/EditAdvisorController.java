package com.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.AdvisorManager;
import com.springmvc.model.Advisor;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/advisor")
public class EditAdvisorController {

	@RequestMapping(value = "/editProfile", method = RequestMethod.GET)
	public ModelAndView editProfile(HttpSession session) {
		Advisor advisor = (Advisor) session.getAttribute("advisor");
		if (advisor == null) {
			return new ModelAndView("redirect:/loginAdvisor");
		}

		AdvisorManager advisorManager = new AdvisorManager();
		Advisor advData = advisorManager.getAdvisorById(advisor.getAdvisorId());

		ModelAndView mav = new ModelAndView("editAdvisor");
		mav.addObject("advisor", advData);
		return mav;
	}

	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
	public ModelAndView updateProfile(Advisor advisor, HttpSession session) {
		Advisor sessionAdvisor = (Advisor) session.getAttribute("advisor");
		if (sessionAdvisor == null) {
			return new ModelAndView("redirect:/loginAdvisor");
		}

		// ป้องกันแก้ไขข้อมูลคนอื่น
		advisor.setAdvisorId(sessionAdvisor.getAdvisorId());

		// ถ้า password เว้นว่าง = ไม่เปลี่ยน
		if (advisor.getAdv_password() == null || advisor.getAdv_password().isEmpty()) {
			advisor.setAdv_password(sessionAdvisor.getAdv_password());
		}

		AdvisorManager advisorManager = new AdvisorManager();
		advisorManager.updateAdvisor(advisor);

		// อัปเดต session ด้วยข้อมูลใหม่
		session.setAttribute("advisor", advisorManager.getAdvisorById(advisor.getAdvisorId()));

		// redirect ไปหน้า profile
		return new ModelAndView("redirect:/advisor/profile");
	}

	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ModelAndView viewProfile(HttpSession session) {
		Advisor advisor = (Advisor) session.getAttribute("advisor");
		if (advisor == null) {
			return new ModelAndView("redirect:/loginAdvisor");
		}

		AdvisorManager advisorManager = new AdvisorManager();
		Advisor advData = advisorManager.getAdvisorById(advisor.getAdvisorId());

		ModelAndView mav = new ModelAndView("advisorProfile");
		mav.addObject("advisor", advData);
		return mav;
	}
}
