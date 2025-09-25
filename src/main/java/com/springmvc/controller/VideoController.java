package com.springmvc.controller;

import com.springmvc.manager.ProjectManager;
import com.springmvc.model.DocumentFile;
import com.springmvc.model.Project;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class VideoController {

	@RequestMapping(value = "/project/video", method = RequestMethod.GET)
	public ModelAndView viewProjectVideo(@RequestParam("projectId") int projectId) {
		ProjectManager pm = new ProjectManager();

		// ดึงโปรเจ็กต์พร้อมไฟล์แนบ (documentFiles)
		Project project = pm.getProjectWithFiles(projectId);

		// เอาไฟล์วิดีโอรายการแรกที่ "เผยแพร่"
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
