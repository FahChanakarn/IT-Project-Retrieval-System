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
			projectName = student.getProject().getProj_NameTh();
		}

		ModelAndView mav = new ModelAndView("editProfile");
		mav.addObject("projectName", projectName);
		return mav;
	}

	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
	public ModelAndView updateProfile(HttpServletRequest request,
	        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, 
	        HttpSession session) {

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
	    if (firstName == null || lastName == null || password == null || 
	        firstName.trim().isEmpty() || lastName.trim().isEmpty() || password.trim().isEmpty()) {
	        
	        String projectName = getProjectName(student);
	        ModelAndView mav = new ModelAndView("editProfile");
	        mav.addObject("projectName", projectName);
	        mav.addObject("error", "กรุณากรอกข้อมูลให้ครบถ้วน");
	        return mav;
	    }

	    // ✅ อัปเดตข้อมูลนักศึกษา
	    student.setStu_firstName(firstName.trim());
	    student.setStu_lastName(lastName.trim());
	    student.setStu_password(password.trim());

	    // ✅ จัดการอัปโหลดไฟล์ (ปรับปรุง)
	    if (imageFile != null && !imageFile.isEmpty()) {
	        String uploadResult = handleImageUpload(imageFile, student, request);
	        if (uploadResult != null) { // มี error
	            String projectName = getProjectName(student);
	            ModelAndView mav = new ModelAndView("editProfile");
	            mav.addObject("projectName", projectName);
	            mav.addObject("error", uploadResult);
	            return mav;
	        }
	    }

	    try {
	        // ✅ บันทึกในฐานข้อมูล
	        manager.updateStudent(student);
	        session.setAttribute("student", student);

	        // ✅ แสดงหน้าเดิมพร้อมข้อความสำเร็จ
	        String projectName = getProjectName(student);
	        ModelAndView mav = new ModelAndView("editProfile");
	        mav.addObject("projectName", projectName);
	        mav.addObject("success", "บันทึกข้อมูลเรียบร้อยแล้ว");
	        return mav;
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        String projectName = getProjectName(student);
	        ModelAndView mav = new ModelAndView("editProfile");
	        mav.addObject("projectName", projectName);
	        mav.addObject("error", "เกิดข้อผิดพลาดในการบันทึกข้อมูล");
	        return mav;
	    }
	}

	// ✅ แยก method สำหรับจัดการรูป
	private String handleImageUpload(MultipartFile imageFile, Student496 student, HttpServletRequest request) {
	    try {
	        // ตรวจสอบประเภทไฟล์
	        String contentType = imageFile.getContentType();
	        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg")) {
	            return "กรุณาอัปโหลดไฟล์ JPG หรือ PNG เท่านั้น";
	        }

	        // ตรวจสอบขนาดไฟล์ (5MB)
	        if (imageFile.getSize() > 5 * 1024 * 1024) {
	            return "ไฟล์รูปภาพต้องมีขนาดไม่เกิน 5MB";
	        }

	        String uploadPath = request.getServletContext().getRealPath("/assets/uploadsProfile/");
	        File uploadDir = new File(uploadPath);
	        if (!uploadDir.exists()) {
	            uploadDir.mkdirs();
	        }

	        // ลบไฟล์เก่าถ้ามี
	        String oldImagePath = student.getStu_image();
	        if (oldImagePath != null && !oldImagePath.contains("default-profile.png")) {
	            File oldFile = new File(request.getServletContext().getRealPath("/") + oldImagePath);
	            if (oldFile.exists()) {
	                oldFile.delete();
	            }
	        }

	        // บันทึกไฟล์ใหม่
	        String fileName = student.getStuId() + "_" + System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
	        File dest = new File(uploadPath + File.separator + fileName);
	        imageFile.transferTo(dest);

	        student.setStu_image("assets/uploadsProfile/" + fileName);
	        return null; // ไม่มี error
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	        return "ไม่สามารถอัปโหลดรูปภาพได้";
	    }
	}

	// ✅ แยก method สำหรับดึงชื่อโปรเจค
	private String getProjectName(Student496 student) {
	    if (student.getProject() != null) {
	        return student.getProject().getProj_NameTh();
	    }
	    return "";
	}

}
