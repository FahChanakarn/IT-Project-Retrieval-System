package com.springmvc.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.StudentManager;
import com.springmvc.model.Student;

@Controller
public class LoginStudentController {

	@RequestMapping(value = "/loginStudent",  method = RequestMethod.GET)
	public ModelAndView showLoginPage() {
		StudentManager manager = new StudentManager();
		List<Student> studentList = manager.getAllStudents();

		ModelAndView mav = new ModelAndView("loginStudent");
		mav.addObject("studentList", studentList);
		return mav;
	}
}
