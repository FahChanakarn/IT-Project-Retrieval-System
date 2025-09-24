package com.springmvc.controller;

import java.util.List;

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
    public String viewChapter(@RequestParam("projectId") int projectId, Model model) {
        Project project = projectManager.findProjectById(projectId);

        if (project != null) {
            List<DocumentFile> publishedFiles = projectManager.getPublishedFilesByProjectId(projectId);
            project.setDocumentFiles(publishedFiles);

            model.addAttribute("project", project);
        }
        return "ViewChapterPDFFile";
    }
}

