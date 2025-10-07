package com.springmvc.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.springmvc.manager.Student496Manager;
import com.springmvc.manager.StudentProjectManager;
import com.springmvc.model.Student496;

@Controller
public class LoginStudent496Controller {

	@RequestMapping(value = "/loginStudent496", method = RequestMethod.GET)
	public ModelAndView showLoginPage() {
		StudentProjectManager manager = new StudentProjectManager();

		// ✅ ใช้เทอมล่าสุดในระบบแทน
		List<String> allTerms = manager.getAllTerms();
		String currentTerm = (allTerms != null && !allTerms.isEmpty()) ? allTerms.get(0) // เทอมล่าสุด
				: StudentProjectManager.getCurrentTerm();

		List<Student496> studentList = manager.getStudentsByTerm(currentTerm);

		System.out.println("🔍 Using Term: " + currentTerm);
		System.out.println("🔍 Student Count: " + studentList.size());

		ModelAndView mav = new ModelAndView("loginStudent496");
		mav.addObject("studentList", studentList);
		mav.addObject("currentTerm", currentTerm);
		return mav;
	}

	@RequestMapping(value = "/loginStudent496", method = RequestMethod.POST)
	public ModelAndView loginStudent496(@RequestParam("stuId") String stuId, @RequestParam("password") String password,
			HttpServletRequest request) {

		Student496Manager studentManager = new Student496Manager();
		StudentProjectManager projectManager = new StudentProjectManager();
		ModelAndView mav = new ModelAndView("loginStudent496");

		// ✅ ดึงเทอมปัจจุบัน
		List<String> allTerms = projectManager.getAllTerms();
		String currentTerm = (allTerms != null && !allTerms.isEmpty()) ? allTerms.get(0)
				: StudentProjectManager.getCurrentTerm();

		// ✅ โหลด studentList ไว้ก่อนเลย (ใช้ในทุกกรณี)
		List<Student496> studentList = projectManager.getStudentsByTerm(currentTerm);
		mav.addObject("studentList", studentList);
		mav.addObject("currentTerm", currentTerm);

		try {
			Student496 student = studentManager.findByStuId(stuId);

			if (student == null) {
				mav.addObject("loginFailed", true);
				mav.addObject("errorMessage", "ไม่พบรหัสนักศึกษานี้ในระบบ");
				return mav;
			}

			if (PasswordUtil.verifyPassword(password, student.getStu_password())) {
				HttpSession session = request.getSession();
				session.setAttribute("student", student);
				session.setAttribute("showWelcomePopup", true);

				if (student.getProject() != null) {
					session.setAttribute("projectId", student.getProject().getProjectId());
				}

				mav.addObject("loginSuccess", true);
				mav.addObject("redirectUrl", request.getContextPath() + "/");
			} else {
				mav.addObject("loginFailed", true);
				mav.addObject("errorMessage", "รหัสผ่านไม่ถูกต้อง");
			}
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("loginFailed", true);
			mav.addObject("errorMessage", "เกิดข้อผิดพลาดในระบบ: " + e.getMessage());
		}

		return mav;
	}

	@RequestMapping(value = "/logoutStudent", method = RequestMethod.GET)
	public String logoutStudent(HttpSession session) {
		session.invalidate();
		return "redirect:/loginStudent496";
	}
}