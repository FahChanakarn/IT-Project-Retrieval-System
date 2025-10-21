package com.springmvc.controller;

import com.springmvc.manager.ProjectManager;
import com.springmvc.model.DocumentFile;
import com.springmvc.model.Project;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class VideoController {

	@RequestMapping(value = "/project/video", method = RequestMethod.GET)
	public ModelAndView viewProjectVideo(@RequestParam(value = "projectId", required = false) Integer projectId,
			HttpSession session) {

		// ✅ ถ้าไม่มี projectId ใน URL ให้ดึงจาก session
		if (projectId == null) {
			projectId = (Integer) session.getAttribute("viewingProjectId");
		}

		// ✅ ถ้ายังไม่มี ให้ redirect กลับ
		if (projectId == null) {
			return new ModelAndView("redirect:/searchProjects");
		}

		// ✅ เก็บ/อัปเดต projectId ใน session
		session.setAttribute("viewingProjectId", projectId);

		ProjectManager pm = new ProjectManager();
		Project project = pm.getProjectWithFiles(projectId);
		DocumentFile videoDoc = pm.findFirstVideoDoc(projectId, true);

		ModelAndView mav = new ModelAndView("viewVideo");
		mav.addObject("project", project);

		if (videoDoc != null) {
			mav.addObject("videoDoc", videoDoc);
		} else {
			mav.addObject("errorMessage", "ยังไม่มีวิดีโอเผยแพร่สำหรับโครงงานนี้");
		}

		return mav;
	}
}
