package com.springmvc.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.ProgrammingLangManager;
import com.springmvc.manager.ProjectManager;
import com.springmvc.model.Project;
import com.springmvc.model.Student496;
import com.springmvc.model.ProgrammingLang;
import com.springmvc.model.ProjectLangDetail;

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

		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing", "Web and Mobile");
		mav.addObject("projectTypes", projectTypes);

		ProgrammingLangManager langManager = new ProgrammingLangManager();

		mav.addObject("programmingLangs", langManager.getLanguagesByType(ProgrammingLang.LangType.PROGRAMMING));
		mav.addObject("typeDBs", langManager.getLanguagesByType(ProgrammingLang.LangType.DBMS));

		// หา DB ที่ถูกเลือกไว้แล้ว (DBMS)
		Integer selectedDBId = null;
		if (project.getProjectLangDetails() != null) {
			for (ProjectLangDetail detail : project.getProjectLangDetails()) {
				if (detail.getProgrammingLang().getLangType() == ProgrammingLang.LangType.DBMS) {
					selectedDBId = detail.getProgrammingLang().getLangId();
					break;
				}
			}
		}
		mav.addObject("selectedDBId", selectedDBId);

		return mav;
	}

	@RequestMapping(value = "/updateAbstract", method = RequestMethod.POST)
	public ModelAndView updateAbstract(@RequestParam("projNameTh") String projNameTh,
			@RequestParam("projNameEn") String projNameEn, @RequestParam("projectType") String projectType,
			@RequestParam(value = "typeDBId", required = false, defaultValue = "0") Integer typeDBId,
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
		ProgrammingLangManager programmingLangManager = new ProgrammingLangManager();

		Project project = projectManager.findProjectForEditAbstract(projectId);

		project.setProj_NameTh(projNameTh);
		project.setProj_NameEn(projNameEn);
		project.setProjectType(projectType);

		// ✅ เก็บ langId ที่ถูกเลือกไว้
		Set<Integer> selectedLangIds = new HashSet<>();

		// เพิ่ม DBMS ที่เลือก (เฉพาะเมื่อไม่ใช่ Testing)
		if (typeDBId != null && typeDBId > 0) {
			selectedLangIds.add(typeDBId);
		}

		// เพิ่มภาษาโปรแกรมที่เลือก
		if (languageIds != null) {
			for (int langId : languageIds) {
				selectedLangIds.add(langId);
			}
		}

		// ✅ ลบภาษาที่ไม่ได้เลือก (เฉพาะ PROGRAMMING และ DBMS ที่อยู่ในระบบ)
		programmingLangManager.removeUnselectedLanguages(projectId, selectedLangIds);

		// ✅ เพิ่มภาษาที่เลือก (ถ้ายังไม่มี)
		if (typeDBId != null && typeDBId > 0) {
			if (!programmingLangManager.existsProjectLangDetail(projectId, typeDBId)) {
				programmingLangManager.createProjectLangDetailAndSave(project, typeDBId);
			}
		}

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

		project.setAbstractTh(abstractTh);
		project.setAbstractEn(abstractEn);
		project.setKeywordTh(keywordTh);
		project.setKeywordEn(keywordEn);

		projectManager.updateProject(project);

		// โหลด project ใหม่
		project = projectManager.findProjectForEditAbstract(projectId);

		ModelAndView mav = new ModelAndView("editAbstract");
		mav.addObject("project", project);
		mav.addObject("successMessage", "บันทึกข้อมูลเรียบร้อยแล้ว");

		List<String> projectTypes = Arrays.asList("Web", "Mobile App", "Testing", "Web and Mobile");
		mav.addObject("projectTypes", projectTypes);

		mav.addObject("programmingLangs",
				programmingLangManager.getLanguagesByType(ProgrammingLang.LangType.PROGRAMMING));
		mav.addObject("typeDBs", programmingLangManager.getLanguagesByType(ProgrammingLang.LangType.DBMS));

		// ส่ง selectedDBId กลับไป
		Integer selectedDBId = null;
		if (project.getProjectLangDetails() != null) {
			for (ProjectLangDetail detail : project.getProjectLangDetails()) {
				if (detail.getProgrammingLang().getLangType() == ProgrammingLang.LangType.DBMS) {
					selectedDBId = detail.getProgrammingLang().getLangId();
					break;
				}
			}
		}
		mav.addObject("selectedDBId", selectedDBId);

		return mav;
	}
}