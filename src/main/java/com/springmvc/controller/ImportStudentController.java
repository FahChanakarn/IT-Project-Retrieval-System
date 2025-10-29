package com.springmvc.controller;

import java.time.chrono.ThaiBuddhistDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.springmvc.manager.ImportStudentManager;
import com.springmvc.manager.Student496Manager;
import com.springmvc.model.Advisor;
import com.springmvc.model.Student496;

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

		String selectedSemester = "2/" + currentYear;

		ModelAndView mav = new ModelAndView("importStudentFile");
		mav.addObject("semesterList", semesterList);
		mav.addObject("selectedSemester", selectedSemester);

		Student496Manager studentManager = new Student496Manager();
		List<Student496> importedStudents = studentManager.getStudentsBySemester(selectedSemester);

		mav.addObject("importedStudents", importedStudents);

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

		Student496Manager studentManager = new Student496Manager();
		List<Student496> importedStudents = studentManager.getStudentsBySemester(semester);

		mav.addObject("importedStudents", importedStudents);

		if (file == null || file.isEmpty()) {
			return mav;
		}

		ImportStudentManager manager = new ImportStudentManager();
		String message = manager.importFromExcel(file, semester);

		if (message.startsWith("ERROR:")) {
			mav.addObject("error", message.substring(6));
		} else if (message.contains("|DUPLICATE:")) {
			String[] parts = message.split("\\|DUPLICATE:");
			String successMessage = parts[0];
			String duplicateIds = parts[1];

			mav.addObject("success", successMessage);

			List<String> duplicateList = Arrays.asList(duplicateIds.split(",\\s*"));
			mav.addObject("duplicateStudents", duplicateList);

			importedStudents = studentManager.getStudentsBySemester(semester);
			mav.addObject("importedStudents", importedStudents);
		} else {
			mav.addObject("success", message);

			importedStudents = studentManager.getStudentsBySemester(semester);
			mav.addObject("importedStudents", importedStudents);
		}

		return mav;
	}
}