package com.springmvc.controller;

import com.springmvc.manager.StudentImportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class StudentImportController {

	@Autowired
	private StudentImportManager studentImportManager;

	@RequestMapping(value = "/importStudent", method = RequestMethod.GET)
	public String showImportPage() {
		return "importStudentFile";
	}

	@RequestMapping(value = "/importStudent", method = RequestMethod.POST)
	public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
		if (file.isEmpty()) {
			model.addAttribute("error", "กรุณาเลือกไฟล์ Excel");
			return "importStudentFile";
		}

		try {
			int imported = studentImportManager.importStudentsFromExcel(file);
			model.addAttribute("success", "นำเข้านักศึกษา " + imported + " คนสำเร็จแล้ว");
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "เกิดข้อผิดพลาด: " + e.getMessage());
		}

		return "importStudentFile";
	}
}
