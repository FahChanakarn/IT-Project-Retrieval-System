package com.springmvc.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.ToolsManager;
import com.springmvc.manager.ProjectManager;
import com.springmvc.model.Project;
import com.springmvc.model.Student496;
import com.springmvc.model.Tools;

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

		ToolsManager toolsManager = new ToolsManager();

		mav.addObject("programmingLangs", toolsManager.getToolsByType(Tools.ToolsType.PROGRAMMING));
		mav.addObject("typeDBs", toolsManager.getToolsByType(Tools.ToolsType.DBMS));

		// ✅ หา DB ที่ถูกเลือกไว้แล้ว (DBMS) - เพิ่มการเช็ค null
		Integer selectedDBId = null;
		if (project != null && project.getTools() != null) {
			for (Tools tool : project.getTools()) {
				if (tool.getToolType() == Tools.ToolsType.DBMS) {
					selectedDBId = tool.getToolsId();
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
		ToolsManager toolsManager = new ToolsManager();

		Project project = projectManager.findProjectForEditAbstract(projectId);

		// ✅ เพิ่มการเช็ค project null
		if (project == null) {
			ModelAndView mav = new ModelAndView("editAbstract");
			mav.addObject("errorMessage", "ไม่พบข้อมูลโครงงาน กรุณาลองใหม่อีกครั้ง");
			return mav;
		}

		project.setProj_NameTh(projNameTh);
		project.setProj_NameEn(projNameEn);
		project.setProjectType(projectType);

		// ✅ เก็บ toolsId ที่ถูกเลือกไว้
		Set<Integer> selectedToolsIds = new HashSet<>();

		// เพิ่ม DBMS ที่เลือก (เฉพาะเมื่อไม่ใช่ Testing)
		if (typeDBId != null && typeDBId > 0) {
			selectedToolsIds.add(typeDBId);
		}

		// เพิ่มภาษาโปรแกรมที่เลือก
		if (languageIds != null) {
			for (int toolsId : languageIds) {
				selectedToolsIds.add(toolsId);
			}
		}

		// ✅ ลบ tools ที่ไม่ได้เลือก (เฉพาะ PROGRAMMING และ DBMS ที่อยู่ในระบบ)
		toolsManager.removeUnselectedTools(projectId, selectedToolsIds);

		// ✅ เพิ่ม tools ที่เลือก (ถ้ายังไม่มี)
		if (typeDBId != null && typeDBId > 0) {
			if (!toolsManager.existsProjectTools(projectId, typeDBId)) {
				toolsManager.addToolsToProject(project, typeDBId);
			}
		}

		if (languageIds != null) {
			for (int toolsId : languageIds) {
				if (!toolsManager.existsProjectTools(projectId, toolsId)) {
					toolsManager.addToolsToProject(project, toolsId);
				}
			}
		}

		// เพิ่มภาษาที่กรอกเอง
		if (otherLanguages != null && !otherLanguages.trim().isEmpty()) {
			String[] otherLangs = otherLanguages.split(",");
			for (String toolsName : otherLangs) {
				toolsName = toolsName.trim();
				if (!toolsName.isEmpty()) {
					toolsManager.addOtherToolsToProject(project, toolsName);
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

		mav.addObject("programmingLangs", toolsManager.getToolsByType(Tools.ToolsType.PROGRAMMING));
		mav.addObject("typeDBs", toolsManager.getToolsByType(Tools.ToolsType.DBMS));

		// ✅ ส่ง selectedDBId กลับไป - เพิ่มการเช็ค null
		Integer selectedDBId = null;
		if (project != null && project.getTools() != null) {
			for (Tools tool : project.getTools()) {
				if (tool.getToolType() == Tools.ToolsType.DBMS) {
					selectedDBId = tool.getToolsId();
					break;
				}
			}
		}
		mav.addObject("selectedDBId", selectedDBId);

		return mav;
	}
}