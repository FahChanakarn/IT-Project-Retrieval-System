package com.springmvc.controller;

import com.springmvc.manager.ProjectManager;
import com.springmvc.manager.StudentProjectManager;
import com.springmvc.model.Project;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class StudentProjectController {

	@RequestMapping(value = "/listStudentProjects", method = RequestMethod.GET)
	public ModelAndView listStudentProjects(@RequestParam(value = "semester", required = false) String semester) {
		ProjectManager projectManager = new ProjectManager();

		if (semester == null || semester.isEmpty()) {
			semester = projectManager.getLatestSemester(); // fallback เป็นภาคเรียนล่าสุด
		}

		List<Project> projects = projectManager.getProjectsBySemester(semester); // ✅ ใช้ Project ไม่ใช่ StudentProject
		List<String> semesters = projectManager.getAllSemesters(); // สำหรับ dropdown

		ModelAndView mav = new ModelAndView("listStudentProject");
		mav.addObject("projectList", projects);
		mav.addObject("semesterList", semesters);
		mav.addObject("selectedSemester", semester);
		return mav;
	}

}
