package com.springmvc.controller;

import com.springmvc.manager.AdvisorManager;
import com.springmvc.model.Advisor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdvisorController {

	@RequestMapping(value = "/editProfile", method = RequestMethod.GET)
	public ModelAndView editProfile(HttpSession session) {
		Advisor admin = (Advisor) session.getAttribute("admin");
		if (admin == null) {
			return new ModelAndView("redirect:/loginAdmin");
		}

		AdvisorManager advisorManager = new AdvisorManager();
		Advisor adminData = advisorManager.getAdvisorById(admin.getAdvisorId());

		ModelAndView mav = new ModelAndView("editAdvisor");
		mav.addObject("advisor", adminData);
		return mav;
	}

	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
	public ModelAndView updateProfile(@RequestParam("adv_prefix") String prefix,
			@RequestParam("adv_firstName") String firstName, @RequestParam("adv_lastName") String lastName,
			@RequestParam("adv_email") String email,
			@RequestParam(value = "adv_password", required = false) String password, HttpSession session,
			RedirectAttributes redirectAttributes) {

		Advisor sessionAdmin = (Advisor) session.getAttribute("admin");
		if (sessionAdmin == null) {
			return new ModelAndView("redirect:/loginAdmin");
		}

		Advisor admin = new Advisor();
		admin.setAdvisorId(sessionAdmin.getAdvisorId());
		admin.setAdv_prefix(prefix);
		admin.setAdv_firstName(firstName);
		admin.setAdv_lastName(lastName);
		admin.setAdv_email(email);

		if (password == null || password.trim().isEmpty()) {
			admin.setAdv_password(sessionAdmin.getAdv_password());
		} else {
			admin.setAdv_password(password.trim());
		}

		try {
			AdvisorManager advisorManager = new AdvisorManager();
			advisorManager.updateAdvisor(admin);

			Advisor updatedAdmin = advisorManager.getAdvisorById(admin.getAdvisorId());
			session.setAttribute("admin", updatedAdmin);

			redirectAttributes.addFlashAttribute("success", "บันทึกข้อมูลสำเร็จ");

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "เกิดข้อผิดพลาดในการบันทึกข้อมูล");
		}

		return new ModelAndView("redirect:/admin/editProfile");
	}

	@RequestMapping(value = "/listAdvisors", method = RequestMethod.GET)
	public ModelAndView listAdvisors(HttpSession session) {
		// ตรวจสอบ session ของ admin
		Object admin = session.getAttribute("admin");
		if (admin == null) {
			// ถ้า session หมดอายุหรือไม่มี admin ให้ redirect ไป login
			return new ModelAndView("redirect:/loginAdmin");
		}

		AdvisorManager advisorManager = new AdvisorManager();
		List<Advisor> advisors = advisorManager.getAllAdvisors();

		ModelAndView mav = new ModelAndView("listAdvisor");
		mav.addObject("advisorList", advisors);
		return mav;
	}

	@RequestMapping(value = "/addAdvisorForm", method = RequestMethod.GET)
	public ModelAndView addAdvisorForm(HttpSession session) {
		Object admin = session.getAttribute("admin");
		if (admin == null) {
			return new ModelAndView("redirect:/loginAdmin");
		}
		return new ModelAndView("addAdvisor");
	}

	@RequestMapping(value = "/addAdvisor", method = RequestMethod.POST)
	public ModelAndView addAdvisor(Advisor advisor) {
		AdvisorManager advisorManager = new AdvisorManager();

		advisorManager.addAdvisor(advisor);
		return new ModelAndView("redirect:/admin/listAdvisors");
	}

	@RequestMapping(value = "/deleteAdvisor/{id}", method = RequestMethod.GET)
	public ModelAndView deleteAdvisor(@PathVariable("id") String advisorId) {
		AdvisorManager advisorManager = new AdvisorManager();
		advisorManager.deleteAdvisor(advisorId);

		return new ModelAndView("redirect:/admin/listAdvisors");
	}

	@RequestMapping(value = "/toggleStatus", method = RequestMethod.POST)
	public ModelAndView toggleStatus(String adv_id, HttpSession session) {
		AdvisorManager advisorManager = new AdvisorManager();
		advisorManager.toggleStatus(adv_id);

		Object admin = session.getAttribute("admin");
		if (admin == null) {
			return new ModelAndView("redirect:/loginAdmin");
		}

		return new ModelAndView("redirect:/admin/listAdvisors");
	}

	@RequestMapping(value = "/togglePosition", method = RequestMethod.POST)
	public ModelAndView togglePosition(String adv_id, HttpSession session) {
		AdvisorManager advisorManager = new AdvisorManager();

		Advisor currentAdmin = (Advisor) session.getAttribute("admin");
		if (currentAdmin == null) {
			return new ModelAndView("redirect:/loginAdmin");
		}

		Advisor targetAdvisor = advisorManager.getAdvisorById(adv_id);
		if (targetAdvisor == null) {
			return new ModelAndView("redirect:/admin/listAdvisors");
		}

		// ถ้า currentAdmin เป็น admin (อาจารย์ประสานงาน)
		if ("อาจารย์ประสานงาน".equals(currentAdmin.getAdv_position())) {
			// อัพเดตตำแหน่งใน DB
			currentAdmin.setAdv_position("อาจารย์ที่ปรึกษา");
			advisorManager.updateAdvisorPosition(currentAdmin);

			targetAdvisor.setAdv_position("อาจารย์ประสานงาน");
			advisorManager.updateAdvisorPosition(targetAdvisor);

			// **ไม่ลบ session** → admin ยังอยู่ในระบบ
		} else {
			// toggle position คนเดียว
			advisorManager.togglePosition(adv_id);
		}

		return new ModelAndView("redirect:/admin/listAdvisors");
	}

}
