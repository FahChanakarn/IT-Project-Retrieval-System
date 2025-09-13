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

		// เอาไฟล์วิดีโอรายการแรกที่ "เผยแพร่" (ปรับเป็น false
		// ถ้าให้ที่ปรึกษาดูได้ทุกไฟล์)
		DocumentFile videoDoc = pm.findFirstVideoDoc(projectId, true);

		ModelAndView mav = new ModelAndView("viewVideo");
		mav.addObject("project", project);
		mav.addObject("videoDoc", videoDoc); // ใน JSP จะอ่าน videoDoc.filepath
		return mav;
	}
}
