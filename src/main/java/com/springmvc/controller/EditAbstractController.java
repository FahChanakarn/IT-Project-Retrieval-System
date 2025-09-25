package com.springmvc.controller;

import java.util.Arrays;
import java.util.List;

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

		// ใช้ method ใหม่ที่ fetch projectLangDetails, student496s และ documentFiles
		Project project = projectManager.findProjectForEditAbstract(projectId);

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

		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing");
		mav.addObject("projectTypes", projectTypes);

		mav.addObject("typeDBs", new TypeDBManager().getAllTypeDBs());
		mav.addObject("programmingLangs", new ProgrammingLangManager().getAllProgrammingLanguages());

		return mav;
	}

	@RequestMapping(value = "/updateAbstract", method = RequestMethod.POST)
	public ModelAndView updateAbstract(@RequestParam("projNameTh") String projNameTh,
			@RequestParam("projNameEn") String projNameEn, @RequestParam("projectType") String projectType,
			@RequestParam("typeDBId") int typeDBId,
			@RequestParam(value = "languageIds", required = false) int[] languageIds,
			@RequestParam(value = "otherLanguages", required = false) String otherLanguages,
			@RequestParam("abstractTh") String abstractTh, @RequestParam("abstractEn") String abstractEn,
			@RequestParam("keywordTh") String keywordTh, @RequestParam("keywordEn") String keywordEn,
			HttpSession session) {

		Student496 student = (Student496) session.getAttribute("student");
		if (student == null || student.getProject() == null) {
			return new ModelAndView("redirect:/loginStudent496");
		}

		int projectId = student.getProject().getProjectId();
		ProjectManager projectManager = new ProjectManager();

		// ใช้ method ใหม่
		Project project = projectManager.findProjectForEditAbstract(projectId);

		// อัปเดตข้อมูลพื้นฐาน
		project.setProj_NameTh(projNameTh);
		project.setProj_NameEn(projNameEn);
		project.setProjectType(projectType);

		// อัปเดต typeDB
		TypeDBManager typeDBManager = new TypeDBManager();
		project.setTypeDB(typeDBManager.findTypeDBById(typeDBId));

		// อัปเดต languages (Many-to-Many) โดยไม่ลบของเก่า
		ProgrammingLangManager programmingLangManager = new ProgrammingLangManager();

		if (languageIds != null) {
			for (int langId : languageIds) {
				if (!programmingLangManager.existsProjectLangDetail(projectId, langId)) {
					programmingLangManager.createProjectLangDetailAndSave(project, langId);
				}
			}
		}

		// เพิ่มภาษาที่กรอกเอง
		if (otherLanguages != null && !otherLanguages.trim().isEmpty()) {
			String[] otherLangs = otherLanguages.split(",");
			for (String langName : otherLangs) {
				langName = langName.trim();
				if (!langName.isEmpty()) {
					programmingLangManager.addOtherLanguageToProject(project, langName);
				}
			}
		}

		// อัปเดต abstract และ keyword
		project.setAbstractTh(abstractTh);
		project.setAbstractEn(abstractEn);
		project.setKeywordTh(keywordTh);
		project.setKeywordEn(keywordEn);

		// บันทึก Project
		projectManager.updateProject(project);

		// โหลดกลับไปยัง editAbstract
		ModelAndView mav = new ModelAndView("editAbstract");
		mav.addObject("project", project);
		mav.addObject("successMessage", "บันทึกข้อมูลเรียบร้อยแล้ว");

		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing");
		mav.addObject("projectTypes", projectTypes);
		mav.addObject("typeDBs", typeDBManager.getAllTypeDBs());
		mav.addObject("programmingLangs", programmingLangManager.getAllProgrammingLanguages());

		return mav;
	}
}
