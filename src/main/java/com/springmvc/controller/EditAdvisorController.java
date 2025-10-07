package com.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	public ModelAndView updateProfile(@RequestParam("adv_prefix") String prefix,
			@RequestParam("adv_firstName") String firstName, @RequestParam("adv_lastName") String lastName,
			@RequestParam("adv_email") String email,
			@RequestParam(value = "adv_password", required = false) String password, HttpSession session,
			RedirectAttributes redirectAttributes) {

		System.out.println("=== updateProfile called ===");

		Advisor sessionAdvisor = (Advisor) session.getAttribute("advisor");
		if (sessionAdvisor == null) {
			return new ModelAndView("redirect:/loginAdvisor");
		}

		// สร้าง Advisor object ใหม่
		Advisor advisor = new Advisor();
		advisor.setAdvisorId(sessionAdvisor.getAdvisorId());
		advisor.setAdv_prefix(prefix);
		advisor.setAdv_firstName(firstName);
		advisor.setAdv_lastName(lastName);
		advisor.setAdv_email(email);

		// ถ้า password เว้นว่าง = ไม่เปลี่ยน
		if (password == null || password.trim().isEmpty()) {
			advisor.setAdv_password(sessionAdvisor.getAdv_password());
		} else {
			advisor.setAdv_password(password.trim());
		}

		try {
			AdvisorManager advisorManager = new AdvisorManager();
			advisorManager.updateAdvisor(advisor);

			// อัปเดต session ด้วยข้อมูลใหม่
			Advisor updatedAdvisor = advisorManager.getAdvisorById(advisor.getAdvisorId());
			session.setAttribute("advisor", updatedAdvisor);

			// เพิ่ม success message
			redirectAttributes.addFlashAttribute("success", "บันทึกข้อมูลสำเร็จ");

			System.out.println("=== Update successful ===");
		} catch (Exception e) {
			System.err.println("Error updating advisor: " + e.getMessage());
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "เกิดข้อผิดพลาดในการบันทึกข้อมูล");
		}

		// redirect กลับไปหน้าแก้ไขเดิม
		return new ModelAndView("redirect:/advisor/editProfile");
	}

	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ModelAndView viewProfile(HttpSession session) {
		Advisor advisor = (Advisor) session.getAttribute("advisor");
		if (advisor == null) {
			return new ModelAndView("redirect:/loginAdvisor");
		}

		AdvisorManager advisorManager = new AdvisorManager();
		Advisor advData = advisorManager.getAdvisorById(advisor.getAdvisorId());

		ModelAndView mav = new ModelAndView("editProfile");
		mav.addObject("advisor", advData);
		return mav;
	}
}