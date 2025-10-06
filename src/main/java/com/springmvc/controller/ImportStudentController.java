package com.springmvc.controller;

import java.time.chrono.ThaiBuddhistDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.ImportStudentManager;
import com.springmvc.model.Advisor;

@Controller
@RequestMapping("/admin")
public class ImportStudentController {

	@GetMapping("/importStudentFile")
	public ModelAndView showImportPage(HttpSession session) {
		Advisor admin = (Advisor) session.getAttribute("admin");
		if (admin == null)
			return new ModelAndView("redirect:/loginAdmin");

		int currentYear = ThaiBuddhistDate.now().get(ChronoField.YEAR);
		List<String> semesterList = new ArrayList<>();
		for (int year = currentYear; year >= 2562; year--)
			semesterList.add("2/" + year);

		ModelAndView mav = new ModelAndView("importStudentFile");
		mav.addObject("semesterList", semesterList);
		mav.addObject("selectedSemester", "2/" + currentYear);
		return mav;
	}

	@PostMapping("/importStudentFile")
	public ModelAndView importStudentFile(@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam("semester") String semester, HttpSession session) {

		if (session.getAttribute("admin") == null)
			return new ModelAndView("redirect:/loginAdmin");

		int currentYear = ThaiBuddhistDate.now().get(ChronoField.YEAR);
		List<String> semesterList = new ArrayList<>();
		for (int year = currentYear; year >= 2562; year--)
			semesterList.add("2/" + year);

		ModelAndView mav = new ModelAndView("importStudentFile");
		mav.addObject("semesterList", semesterList);
		mav.addObject("selectedSemester", semester);

		// ✅ ถ้าไม่มีไฟล์ หรือไฟล์ว่าง = แสดงว่ามาจากการเปลี่ยน semester
		if (file == null || file.isEmpty()) {
			return mav; // แค่แสดงหน้าเปล่าๆ ไม่ต้อง import
		}

		// ✅ มีไฟล์ = ทำการ import
		ImportStudentManager manager = new ImportStudentManager();
		String message = manager.importFromExcel(file, semester);

		// ตรวจสอบว่ามีข้อผิดพลาดหรือไม่
		if (message.startsWith("ERROR:")) {
			mav.addObject("error", message.substring(6));
		}
		// ตรวจสอบว่ามีรหัสนักศึกษาซ้ำหรือไม่
		else if (message.contains("|DUPLICATE:")) {
			String[] parts = message.split("\\|DUPLICATE:");
			String successMessage = parts[0];
			String duplicateIds = parts[1];

			mav.addObject("success", successMessage);
			mav.addObject("duplicateStudents", duplicateIds);
		}
		// นำเข้าสำเร็จโดยไม่มีข้อมูลซ้ำ
		else {
			mav.addObject("success", message);
		}

		return mav;
	}
}