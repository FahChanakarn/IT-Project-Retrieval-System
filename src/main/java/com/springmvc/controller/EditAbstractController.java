package com.springmvc.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.ProgrammingLangManager;
import com.springmvc.manager.ProjectManager;
import com.springmvc.manager.TypeDBManager;
import com.springmvc.model.Project;
import com.springmvc.model.Student496;

@Controller
public class EditAbstractController {

	@RequestMapping(value = "/editAbstract", method = RequestMethod.GET)
	public ModelAndView showEditAbstract(HttpSession session) {
		Student496 student = (Student496) session.getAttribute("student");

		if (student == null || student.getProject() == null) {
			return new ModelAndView("redirect:/loginStudent496");
		}

		int projectId = student.getProject().getProjectId();

		ProjectManager projectManager = new ProjectManager();
		Project project = projectManager.findProjectById(projectId);

		if (project == null) {
			ModelAndView mav = new ModelAndView("editAbstract");
			mav.addObject("errorMessage", "ไม่พบข้อมูลโครงงานของคุณ กรุณาติดต่อผู้ดูแลระบบ");
			return mav;
		}

		String studentFullName = student.getStu_prefix() + student.getStu_firstName() + " " + student.getStu_lastName();

		ModelAndView mav = new ModelAndView("editAbstract");
		mav.addObject("project", project);
		mav.addObject("projectName", project.getProj_NameTh());
		mav.addObject("studentFullName", studentFullName);

		mav.addObject("projectTypes", projectManager.getAllProjectTypes());
		mav.addObject("typeDBs", new TypeDBManager().getAllTypeDBs());
		mav.addObject("programmingLangs", new ProgrammingLangManager().getAllProgrammingLanguages());

		return mav;
	}

	@RequestMapping(value = "/updateAbstract", method = RequestMethod.POST)
	public ModelAndView updateAbstract(@RequestParam("abstractTh") String abstractTh,
			@RequestParam("abstractEn") String abstractEn, @RequestParam("keywordTh") String keywordTh,
			@RequestParam("keywordEn") String keywordEn, HttpSession session) {

		Student496 student = (Student496) session.getAttribute("student");

		if (student == null || student.getProject() == null) {
			return new ModelAndView("redirect:/loginStudent496");
		}

		int projectId = student.getProject().getProjectId();

		ProjectManager projectManager = new ProjectManager();
		Project project = projectManager.findProjectById(projectId);

		// ✅ อัปเดตค่าที่รับมา
		project.setAbstractTh(abstractTh);
		project.setAbstractEn(abstractEn);
		project.setKeywordTh(keywordTh);
		project.setKeywordEn(keywordEn);

		projectManager.updateProject(project);

		// ✅ โหลด dropdown เดิมกลับไปด้วย
		ModelAndView mav = new ModelAndView("editAbstract");
		mav.addObject("project", project);
		mav.addObject("successMessage", "บันทึกข้อมูลเรียบร้อยแล้ว");

		// ดึง dropdown กลับมาเพื่อให้ฟอร์มโหลดข้อมูลได้ครบ
		mav.addObject("projectTypes", projectManager.getAllProjectTypes());
		TypeDBManager typeDBManager = new TypeDBManager();
		mav.addObject("typeDBs", typeDBManager.getAllTypeDBs());
		ProgrammingLangManager programmingLangManager = new ProgrammingLangManager();
		mav.addObject("programmingLangs", programmingLangManager.getAllProgrammingLanguages());

		return mav;
	}

}
