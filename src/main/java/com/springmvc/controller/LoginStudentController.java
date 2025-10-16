package com.springmvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.springmvc.manager.StudentManager;
import com.springmvc.model.Student;

@Controller
public class LoginStudentController {

	private StudentManager manager = new StudentManager();

	@RequestMapping(value = "/loginStudent", method = RequestMethod.GET)
	public ModelAndView showLoginPage() {
		// ไม่ต้องส่ง studentList ไปแล้ว เพราะไม่มี dropdown
		ModelAndView mav = new ModelAndView("loginStudent");
		return mav;
	}

	// ประมวลผล login
	@RequestMapping(value = "/loginStudent", method = RequestMethod.POST)
	public ModelAndView loginStudent(@RequestParam("stuId") String stuId, @RequestParam("password") String password,
			HttpServletRequest request) {

		Student student = manager.findByStuIdAndPassword(stuId.trim(), password.trim());
		ModelAndView mav = new ModelAndView("loginStudent");

		if (student != null) {
			HttpSession session = request.getSession();
			session.setAttribute("itstudent", student);

			mav.addObject("loginSuccess", true);
			mav.addObject("redirectUrl", request.getContextPath() + "/searchProjects");
		} else {
			mav.addObject("loginFailed", true);
		}

		return mav;
	}
}