package com.springmvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.springmvc.manager.Student496Manager;
import com.springmvc.model.Student496;

@Controller
public class LoginStudent496Controller {

	@RequestMapping(value = "/loginStudent496", method = RequestMethod.GET)
	public ModelAndView showLoginPage() {
		ModelAndView mav = new ModelAndView("loginStudent496");
		return mav;
	}

	@RequestMapping(value = "/loginStudent496", method = RequestMethod.POST)
	public ModelAndView loginStudent496(@RequestParam("stuId") String stuId, @RequestParam("password") String password,
			HttpServletRequest request) {

		Student496Manager studentManager = new Student496Manager();
		ModelAndView mav = new ModelAndView("loginStudent496");

		try {
			Student496 student = studentManager.findByStuId(stuId.trim());

			if (student == null) {
				mav.addObject("loginFailed", true);
				mav.addObject("errorMessage", "ไม่พบรหัสนักศึกษานี้ในระบบ");
				return mav;
			}

			if (PasswordUtil.verifyPassword(password.trim(), student.getStu_password())) {
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