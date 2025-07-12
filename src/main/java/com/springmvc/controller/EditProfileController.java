package com.springmvc.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.Student496Manager;
import com.springmvc.model.Student496;

@Controller
public class EditProfileController {

	@RequestMapping(value = "/editProfile", method = RequestMethod.GET)
	public ModelAndView showEditForm(HttpSession session) {
		Student496 student = (Student496) session.getAttribute("student");

		if (student == null) {
			return new ModelAndView("redirect:/loginStudent496");
		}

		String projectName = "";
		if (student.getProject() != null) {
			projectName = student.getProject().getProj_NameTh(); // หรือ getProj_NameEn()
		}

		ModelAndView mav = new ModelAndView("editProfile");
		mav.addObject("projectName", projectName);
		return mav;
	}

	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
	public ModelAndView updateProfile(HttpServletRequest request,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile, HttpSession session) {

		Student496Manager manager = new Student496Manager();
		Student496 student = (Student496) session.getAttribute("student");

		if (student == null) {
			return new ModelAndView("redirect:/loginStudent496");
		}

		// ✅ รับค่าจาก request
		String firstName = request.getParameter("stu_firstName");
		String lastName = request.getParameter("stu_lastName");
		String password = request.getParameter("stu_password");

		// ✅ ตรวจสอบว่าข้อมูลไม่ว่าง
		if (firstName == null || lastName == null || password == null || firstName.isBlank() || lastName.isBlank()
				|| password.isBlank()) {
			ModelAndView mav = new ModelAndView("editProfile");
			mav.addObject("error", "กรุณากรอกข้อมูลให้ครบถ้วน");
			return mav;
		}

		// ✅ อัปเดตข้อมูลนักศึกษา
		student.setStu_firstName(firstName);
		student.setStu_lastName(lastName);
		student.setStu_password(password);

		// ✅ จัดการอัปโหลดไฟล์
		if (imageFile != null && !imageFile.isEmpty()) {
			try {
				String contentType = imageFile.getContentType();
				if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
					ModelAndView mav = new ModelAndView("editProfile");
					mav.addObject("error", "กรุณาอัปโหลดไฟล์ JPG หรือ PNG เท่านั้น");
					return mav;
				}

				String uploadPath = request.getServletContext().getRealPath("/assets/uploadsProfile/");
				File uploadDir = new File(uploadPath);
				if (!uploadDir.exists()) {
					uploadDir.mkdirs();
				}

				String fileName = student.getStuId() + "_" + imageFile.getOriginalFilename();
				File dest = new File(uploadPath + File.separator + fileName);
				imageFile.transferTo(dest);

				student.setStu_image("assets/uploadsProfile/" + fileName);
			} catch (IOException e) {
				e.printStackTrace();
				ModelAndView mav = new ModelAndView("editProfile");
				mav.addObject("error", "ไม่สามารถอัปโหลดรูปภาพได้");
				return mav;
			}
		}

		// ✅ บันทึกในฐานข้อมูล
		manager.updateStudent(student);
		session.setAttribute("student", student);

		// ✅ แสดงหน้าเดิมพร้อมข้อความสำเร็จ
		ModelAndView mav = new ModelAndView("editProfile");
		mav.addObject("success", "บันทึกข้อมูลเรียบร้อยแล้ว");
		return mav;
	}

}
