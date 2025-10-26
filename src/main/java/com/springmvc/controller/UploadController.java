package com.springmvc.controller;

import com.springmvc.manager.ProjectManager;
import com.springmvc.manager.UploadManager;
import com.springmvc.model.DocumentFile;
import com.springmvc.model.Project;
import com.springmvc.model.Student496;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
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
		List<DocumentFile> files = manager.getFilesByProject(projectId);

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
		String uploadedByStudentId = student.getStuId(); // ✅ ดึงรหัสนักศึกษา

		try {
			UploadManager manager = new UploadManager();
			// ✅ ส่ง parameter ตามที่ manager ต้องการ
			manager.saveFile(projectId, fileType, fileName, file, videoLink, uploadedByStudentId, servletContext);
			return "redirect:/student496/upload?upload=true";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/student496/upload?error=true";
		}
	}

	// 3. ✅ ไปหน้าแก้ไขไฟล์ - เพิ่ม allFiles สำหรับตรวจสอบชื่อซ้ำ
	@RequestMapping(value = "/editFileAndVideo/{fileId}", method = RequestMethod.GET)
	public ModelAndView editFile(@PathVariable("fileId") int fileId, HttpSession session) {
		// ✅ เช็ค session อย่างเข้มงวด
		if (!isSessionValid(session)) {
			return new ModelAndView("redirect:/loginStudent496");
		}

		Integer projectId = (Integer) session.getAttribute("projectId");
		UploadManager manager = new UploadManager();

		// ✅ ดึงไฟล์ที่ต้องการแก้ไข
		DocumentFile file = manager.getFileById(fileId);

		// ✅ ดึงไฟล์ทั้งหมดของโครงงานเพื่อตรวจสอบชื่อซ้ำ
		List<DocumentFile> allFiles = manager.getFilesByProject(projectId);

		ModelAndView mav = new ModelAndView("editFileAndVideo");
		mav.addObject("file", file);
		mav.addObject("allFiles", allFiles); // ✅ ส่งไปให้ JSP
		return mav;
	}

	// ✅ ลบ method ซ้ำ - รวมเป็น method เดียว (ใช้ editFileAndVideo สำหรับ query
	// parameter)
	@RequestMapping("/editFileAndVideo")
	public ModelAndView editFileAndVideo(@RequestParam("id") int fileId, HttpSession session) {
		// ✅ เช็ค session อย่างเข้มงวด
		if (!isSessionValid(session)) {
			return new ModelAndView("redirect:/loginStudent496");
		}

		Integer projectId = (Integer) session.getAttribute("projectId");
		UploadManager manager = new UploadManager();

		// ✅ ดึงไฟล์ที่ต้องการแก้ไข
		DocumentFile file = manager.getFileById(fileId);

		// ✅ ดึงไฟล์ทั้งหมดของโครงงานเพื่อตรวจสอบชื่อซ้ำ
		List<DocumentFile> allFiles = manager.getFilesByProject(projectId);

		ModelAndView mav = new ModelAndView("editFileAndVideo");
		mav.addObject("file", file);
		mav.addObject("allFiles", allFiles); // ✅ ส่งไปให้ JSP
		return mav;
	}

	// 4. อัปเดตไฟล์หรือวิดีโอ
	@RequestMapping(value = "/updateFileAndVideo", method = RequestMethod.POST)
	public String updateFileAndVideo(HttpServletRequest request, @RequestParam("id") int id,
			@RequestParam("name") String name, @RequestParam(value = "videoLink", required = false) String videoLink,
			@RequestParam(value = "newFile", required = false) MultipartFile newFile, HttpSession session) {

		// ✅ เช็ค session อย่างเข้มงวด
		if (!isSessionValid(session)) {
			return "redirect:/loginStudent496";
		}

		Student496 student = (Student496) session.getAttribute("student");
		String uploadedByStudentId = student.getStuId(); // ✅ ดึงรหัสนักศึกษา

		try {
			UploadManager fileManager = new UploadManager();
			// ✅ ส่ง parameter ตามที่ manager ต้องการ
			fileManager.updateFileOrVideo(id, name, videoLink, newFile, uploadedByStudentId, request);
			return "redirect:/student496/upload?success=true";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/student496/upload?error=true";
		}
	}
}