package com.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import java.io.IOException;

@Controller
public class StudentImportController {

	@GetMapping("/admin/importStudent")
	public String showImportPage() {
		return "importStudentFile"; // ชี้ไปยัง importStudent.jsp
	}

	@PostMapping("/admin/importStudent")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
		if (file.isEmpty()) {
			model.addAttribute("error", "กรุณาเลือกไฟล์เพื่อทำการนำเข้า");
			return "importStudentFile";
		}

		try {
			// 📝 ตัวอย่าง: อ่านไฟล์ที่อัปโหลดมา
			byte[] fileData = file.getBytes();

			
			System.out.println("File uploaded: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize() + " bytes");
            System.out.println("Content type: " + file.getContentType());

			model.addAttribute("success", "นำเข้าข้อมูลสำเร็จ");
		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("error", "เกิดข้อผิดพลาดระหว่างการอัปโหลดไฟล์");
		}

		return "importStudentFile";
	}
}
