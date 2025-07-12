package com.springmvc.controller;

import com.springmvc.manager.AdvisorManager;
import com.springmvc.model.Advisor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdvisorController {

	@RequestMapping(value = "/listAdvisors", method = RequestMethod.GET)
	public ModelAndView listAdvisors() {
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

		// กำหนดรหัสอาจารย์ตามตำแหน่ง
		String prefix = advisor.getAdv_position().equals("อาจารย์ที่ปรึกษา") ? "T" : "A";
		String newId = advisorManager.generateNextAdvisorId(prefix);
		advisor.setAdvisorId(newId);

		advisorManager.addAdvisor(advisor);
		return new ModelAndView("redirect:/admin/listAdvisors");
	}

	@RequestMapping(value = "/editAdvisorForm/{id}", method = RequestMethod.GET)
	public ModelAndView editAdvisorForm(@PathVariable("id") String advisorId) {
		AdvisorManager advisorManager = new AdvisorManager();
		Advisor advisor = advisorManager.getAdvisorById(advisorId);

		ModelAndView mav = new ModelAndView("editAdvisor");
		mav.addObject("advisor", advisor);
		return mav;
	}

	@RequestMapping(value = "/updateAdvisor", method = RequestMethod.POST)
	public ModelAndView updateAdvisor(Advisor advisor) {
		AdvisorManager advisorManager = new AdvisorManager();
		advisorManager.updateAdvisor(advisor);

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

}
