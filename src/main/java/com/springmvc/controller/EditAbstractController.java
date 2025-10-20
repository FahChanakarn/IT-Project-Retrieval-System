package com.springmvc.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
		List<Tools> allTools = toolsManager.getAllTools();
		mav.addObject("allTools", allTools);

		return mav;
	}

	@RequestMapping(value = "/updateAbstract", method = RequestMethod.POST)
	public String updateAbstract(@RequestParam("projNameTh") String projNameTh,
			@RequestParam("projNameEn") String projNameEn, @RequestParam("projectType") String projectType,
			@RequestParam(value = "programmingToolIds", required = false) int[] programmingToolIds,
			@RequestParam(value = "dbmsToolIds", required = false) int[] dbmsToolIds,
			@RequestParam(value = "testingToolIds", required = false) int[] testingToolIds,
			@RequestParam("abstractTh") String abstractTh, @RequestParam("abstractEn") String abstractEn,
			@RequestParam("keywordTh") String keywordTh, @RequestParam("keywordEn") String keywordEn,
			HttpSession session) {

		Student496 student = (Student496) session.getAttribute("student");
		if (student == null || student.getProject() == null) {
			return "redirect:/loginStudent496";
		}

		int projectId = student.getProject().getProjectId();
		ProjectManager projectManager = new ProjectManager();
		ToolsManager toolsManager = new ToolsManager();

		Project project = projectManager.findProjectForEditAbstract(projectId);

		if (project == null) {
			// ✅ ส่ง error parameter
			return "redirect:/editAbstract?error=true";
		}

		try {
			project.setProj_NameTh(projNameTh);
			project.setProj_NameEn(projNameEn);
			project.setProjectType(projectType);

			// รวม toolIds ทั้งหมด
			Set<Integer> selectedToolsIds = new HashSet<>();

			if (programmingToolIds != null) {
				for (int toolsId : programmingToolIds) {
					selectedToolsIds.add(toolsId);
				}
			}

			if (dbmsToolIds != null) {
				for (int toolsId : dbmsToolIds) {
					selectedToolsIds.add(toolsId);
				}
			}

			if (testingToolIds != null) {
				for (int toolsId : testingToolIds) {
					selectedToolsIds.add(toolsId);
				}
			}

			// ลบ tools ที่ไม่ได้เลือก
			toolsManager.removeUnselectedTools(projectId, selectedToolsIds);

			// เพิ่ม tools ที่เลือก
			for (Integer toolsId : selectedToolsIds) {
				if (!toolsManager.existsProjectTools(projectId, toolsId)) {
					toolsManager.addToolsToProject(project, toolsId);
				}
			}

			project.setAbstractTh(abstractTh);
			project.setAbstractEn(abstractEn);
			project.setKeywordTh(keywordTh);
			project.setKeywordEn(keywordEn);

			projectManager.updateProject(project);

			// ✅ Redirect พร้อม success parameter
			return "redirect:/editAbstract?success=true";

		} catch (Exception e) {
			e.printStackTrace();
			// ✅ Redirect พร้อม error parameter
			return "redirect:/editAbstract?error=true";
		}
	}

	// ✅ เพิ่ม API สำหรับเพิ่ม Tools ใหม่
	@RequestMapping(value = "/addNewTool", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addNewTool(@RequestParam("toolType") String toolType,
			@RequestParam("toolName") String toolName) {

		Map<String, Object> response = new HashMap<>();

		try {
			ToolsManager toolsManager = new ToolsManager();

			// ตรวจสอบว่ามี Tools ชื่อนี้แล้วหรือยัง
			if (toolsManager.isToolNameExists(toolName)) {
				response.put("success", false);
				response.put("message", "มี Tools ชื่อนี้อยู่ในระบบแล้ว");
				return response;
			}

			// แปลง String เป็น Enum
			Tools.ToolsType type;
			try {
				type = Tools.ToolsType.valueOf(toolType);
			} catch (IllegalArgumentException e) {
				response.put("success", false);
				response.put("message", "ประเภท Tools ไม่ถูกต้อง");
				return response;
			}

			// บันทึก Tools ใหม่
			Tools newTool = new Tools();
			newTool.setToolsName(toolName);
			newTool.setToolType(type);

			boolean success = toolsManager.saveTool(newTool);

			if (success) {
				response.put("success", true);
				response.put("message", "เพิ่ม Tools สำเร็จ");
			} else {
				response.put("success", false);
				response.put("message", "เกิดข้อผิดพลาดในการบันทึก");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", "เกิดข้อผิดพลาด: " + e.getMessage());
		}

		return response;
	}
}