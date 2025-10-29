package com.springmvc.controller;

import com.springmvc.manager.ProjectManager;
import com.springmvc.manager.UploadManager;
import com.springmvc.manager.UploadManager.FileWithUploader;
import com.springmvc.model.DocumentFile;
import com.springmvc.model.Project;
import com.springmvc.model.Student496;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/student496")
public class UploadController {

	@Autowired
	private ServletContext servletContext;

	// ✅ Method ตรวจสอบ session - ตรวจสอบว่ามี student object หรือไม่
	private boolean isSessionValid(HttpSession session) {
		if (session == null) {
			return false;
		}
		Student496 student = (Student496) session.getAttribute("student");
		Integer projectId = (Integer) session.getAttribute("projectId");
		return student != null && projectId != null;
	}

	// 1. แสดงหน้าอัปโหลด + ไฟล์ทั้งหมดของโครงงาน
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public ModelAndView showUploadPage(HttpSession session) {
		// ✅ เช็ค session อย่างเข้มงวด
		if (!isSessionValid(session)) {
			return new ModelAndView("redirect:/loginStudent496");
		}

		Integer projectId = (Integer) session.getAttribute("projectId");
		UploadManager manager = new UploadManager();
		ProjectManager proj = new ProjectManager();
		Project project = proj.findProjectById(projectId);

		// ✅ เปลี่ยนเป็น List<FileWithUploader>
		List<FileWithUploader> files = manager.getFilesByProject(projectId);

		ModelAndView mav = new ModelAndView("uploadFileAndVideo");
		mav.addObject("uploadList", files);
		mav.addObject("project", project);
		return mav;
	}

	// 2. อัปโหลดไฟล์หรือวิดีโอ
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String uploadFile(@RequestParam("fileType") String fileType, @RequestParam("fileName") String fileName,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "videoLink", required = false) String videoLink, HttpSession session) {

		// ✅ เช็ค session อย่างเข้มงวด
		if (!isSessionValid(session)) {
			return "redirect:/loginStudent496";
		}

		Integer projectId = (Integer) session.getAttribute("projectId");
		Student496 student = (Student496) session.getAttribute("student");
		String uploadedByStudentId = student.getStuId();

		try {
			UploadManager manager = new UploadManager();
			manager.saveFile(projectId, fileType, fileName, file, videoLink, uploadedByStudentId, servletContext);
			return "redirect:/student496/upload?upload=true";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/student496/upload?error=true";
		}
	}

	// 3. ลบไฟล์หรือวิดีโอ
	@RequestMapping(value = "/deleteFile/{fileId}", method = RequestMethod.GET)
	public String deleteFile(@PathVariable("fileId") int fileId, HttpSession session) {
		// ✅ เช็ค session อย่างเข้มงวด
		if (!isSessionValid(session)) {
			return "redirect:/loginStudent496";
		}

		try {
			UploadManager manager = new UploadManager();
			manager.deleteFile(fileId, servletContext);
			return "redirect:/student496/upload?delete=true";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/student496/upload?deleteError=true";
		}
	}
}