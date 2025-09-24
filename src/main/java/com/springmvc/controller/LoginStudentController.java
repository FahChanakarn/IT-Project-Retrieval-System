package com.springmvc.controller;

import java.util.List;
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
		List<Student> studentList = manager.getStudentsWithoutProject();
		ModelAndView mav = new ModelAndView("loginStudent");
		mav.addObject("studentList", studentList);
		return mav;
	}

	// ประมวลผล login
	@RequestMapping(value = "/loginStudent", method = RequestMethod.POST)
	public ModelAndView loginStudent(@RequestParam("stuId") String stuId, @RequestParam("password") String password,
			HttpServletRequest request) {

		Student student = manager.findByStuIdAndPassword(stuId.trim(), password.trim());
		List<Student> studentList = manager.getStudentsWithoutProject();
		ModelAndView mav = new ModelAndView("loginStudent");
		mav.addObject("studentList", studentList);

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
