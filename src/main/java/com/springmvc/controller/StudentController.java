package com.springmvc.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springmvc.manager.ProjectManager;
import com.springmvc.model.DocumentFile;
import com.springmvc.model.Project;

@Controller
@RequestMapping("/student")
public class StudentController {

	private ProjectManager projectManager = new ProjectManager();

	@RequestMapping("/viewChapter")
	public String viewChapter(@RequestParam(value = "projectId", required = false) Integer projectId, Model model,
			HttpSession session) {

		// ✅ ถ้าไม่มี projectId ใน URL ให้ดึงจาก session
		if (projectId == null) {
			projectId = (Integer) session.getAttribute("viewingProjectId");
		}

		// ✅ ถ้ายังไม่มี ให้ redirect กลับ
		if (projectId == null) {
			return "redirect:/searchProjects";
		}

		// ✅ เก็บ/อัปเดต projectId ใน session
		session.setAttribute("viewingProjectId", projectId);

		Project project = projectManager.findProjectById(projectId);

		if (project != null) {
			List<DocumentFile> publishedFiles = projectManager.getPublishedFilesByProjectId(projectId);
			project.setDocumentFiles(publishedFiles);
			model.addAttribute("project", project);
		}

		return "ViewChapterPDFFile";
	}
}
