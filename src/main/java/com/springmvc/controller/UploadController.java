package com.springmvc.controller;

import com.springmvc.manager.UploadManager;
import com.springmvc.model.DocumentFile;

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

	// 1. แสดงหน้าอัปโหลด + ไฟล์ทั้งหมดของโครงงาน
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public ModelAndView showUploadPage(HttpSession session) {
		Integer projectId = (Integer) session.getAttribute("projectId");
		UploadManager manager = new UploadManager();

		List<DocumentFile> files = manager.getFilesByProject(projectId);

		ModelAndView mav = new ModelAndView("uploadFileAndVideo");
		mav.addObject("uploadList", files); // เปลี่ยนให้ตรงกับ JSP ที่ใช้ ${uploadList}
		return mav;
	}

	// 2. อัปโหลดไฟล์หรือวิดีโอ
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String uploadFile(@RequestParam("fileType") String fileType, @RequestParam("fileName") String fileName,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "videoLink", required = false) String videoLink, HttpSession session) {

		Integer projectId = (Integer) session.getAttribute("projectId");

		UploadManager manager = new UploadManager();
		manager.saveFile(projectId, fileType, fileName, file, videoLink, servletContext);

		return "redirect:/student496/upload";
	}

	// 3. ไปหน้าแก้ไขไฟล์
	@RequestMapping(value = "/editFile/{fileId}", method = RequestMethod.GET)
	public ModelAndView editFile(@PathVariable("fileId") int fileId) {
		UploadManager manager = new UploadManager();
		DocumentFile file = manager.getFileById(fileId);

		ModelAndView mav = new ModelAndView("editFile");
		mav.addObject("file", file);
		return mav;
	}
}
