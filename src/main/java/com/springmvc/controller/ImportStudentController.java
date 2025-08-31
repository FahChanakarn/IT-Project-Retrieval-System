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
	public ModelAndView importStudentFile(@RequestParam("file") MultipartFile file,
			@RequestParam("semester") String semester, HttpSession session) {
		if (session.getAttribute("admin") == null)
			return new ModelAndView("redirect:/loginAdmin");

		ImportStudentManager manager = new ImportStudentManager();
		String message = manager.importFromExcel(file, semester);

		int currentYear = ThaiBuddhistDate.now().get(ChronoField.YEAR);
		List<String> semesterList = new ArrayList<>();
		for (int year = currentYear; year >= 2562; year--)
			semesterList.add("2/" + year);

		ModelAndView mav = new ModelAndView("importStudentFile");
		mav.addObject("semesterList", semesterList);
		mav.addObject("selectedSemester", semester);

		if (message.startsWith("นำเข้าข้อมูลสำเร็จ"))
			mav.addObject("success", message);
		else
			mav.addObject("error", message);

		return mav;
	}
}
