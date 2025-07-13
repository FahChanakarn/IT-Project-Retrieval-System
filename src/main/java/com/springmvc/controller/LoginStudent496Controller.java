package com.springmvc.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.Student496Manager;
import com.springmvc.model.Student496;

@Controller
public class LoginStudent496Controller {

	@RequestMapping(value = "/loginStudent496", method = RequestMethod.GET)
	public ModelAndView showLoginPage() {
		Student496Manager manager = new Student496Manager();
		List<Student496> studentList = manager.getAllStudents();

		ModelAndView mav = new ModelAndView("loginStudent496");
		mav.addObject("studentList", studentList);
		return mav;
	}

	@RequestMapping(value = "/loginStudent496", method = RequestMethod.POST)
	public ModelAndView loginStudent496(@RequestParam("stuId") String stuId, @RequestParam("password") String password,
			HttpServletRequest request) {

		Student496Manager manager = new Student496Manager();
		Student496 student = manager.findByStuIdAndPassword(stuId, password);

		if (student != null) {
		    HttpSession session = request.getSession();
		    session.setAttribute("student", student);

		    // ✅ ตั้ง projectId จาก student.getProject()
		    if (student.getProject() != null) {
		        session.setAttribute("projectId", student.getProject().getProjectId());
		    }
		    return new ModelAndView("redirect:/searchProjects");
		} else {
			ModelAndView mav = new ModelAndView("loginStudent496");
			mav.addObject("error", "รหัสผ่านไม่ถูกต้อง");
			return mav;
		}
	}

}
