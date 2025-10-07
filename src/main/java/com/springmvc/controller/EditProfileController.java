package com.springmvc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.springmvc.manager.Student496Manager;
import com.springmvc.model.Student496;

@Controller
public class EditProfileController {

	// ✅ กำหนด path ถาวรสำหรับเก็บรูปโปรไฟล์
	private static final String PROFILE_UPLOAD_PATH = "D:/Project496Uploads/profileImages";

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

		// ✅ ตรวจสอบว่าข้อมูลพื้นฐานไม่ว่าง (ไม่รวม password)
		if (firstName == null || lastName == null || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {

			String projectName = getProjectName(student);
			ModelAndView mav = new ModelAndView("editProfile");
			mav.addObject("projectName", projectName);
			mav.addObject("error", "กรุณากรอกชื่อและนามสกุลให้ครบถ้วน");
			return mav;
		}

		// ✅ อัปเดตข้อมูลนักศึกษา
		student.setStu_firstName(firstName.trim());
		student.setStu_lastName(lastName.trim());

		// ✅ เปลี่ยนรหัสผ่านเฉพาะเมื่อมีการกรอกมา
		if (password != null && !password.trim().isEmpty()) {
			if (password.trim().length() < 6) {
				String projectName = getProjectName(student);
				ModelAndView mav = new ModelAndView("editProfile");
				mav.addObject("projectName", projectName);
				mav.addObject("error", "รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร");
				return mav;
			}
			String hashedPassword = PasswordUtil.hashPassword(password.trim());
			student.setStu_password(hashedPassword);
			System.out.println("🔐 Password updated and hashed for student: " + student.getStuId());
		} else {
			System.out.println("ℹ️ Password not changed for student: " + student.getStuId());
		}
		// ถ้าไม่กรอก password รหัสผ่านเดิมจะยังคงอยู่ใน object student

		// ✅ จัดการอัปโหลดไฟล์
		if (imageFile != null && !imageFile.isEmpty()) {
			String uploadResult = handleImageUpload(imageFile, student);
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
			mav.addObject("error", "เกิดข้อผิดพลาดในการบันทึกข้อมูล: " + e.getMessage());
			return mav;
		}
	}

	// ✅ จัดการการอัปโหลดรูปภาพ
	private String handleImageUpload(MultipartFile imageFile, Student496 student) {
		try {
			// ตรวจสอบประเภทไฟล์
			String contentType = imageFile.getContentType();
			if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")
					&& !contentType.equals("image/jpg")) {
				return "กรุณาอัปโหลดไฟล์ JPG หรือ PNG เท่านั้น";
			}

			// ตรวจสอบขนาดไฟล์ (5MB)
			if (imageFile.getSize() > 5 * 1024 * 1024) {
				return "ไฟล์รูปภาพต้องมีขนาดไม่เกิน 5MB";
			}

			// ✅ ใช้ absolute path
			File uploadDir = new File(PROFILE_UPLOAD_PATH);
			if (!uploadDir.exists()) {
				uploadDir.mkdirs();
				System.out.println("📁 Created profile upload directory: " + PROFILE_UPLOAD_PATH);
			}

			// ลบไฟล์เก่าถ้ามี
			String oldImagePath = student.getStu_image();
			if (oldImagePath != null && !oldImagePath.isEmpty() && !oldImagePath.contains("default-profile.png")) {
				String oldFileName = oldImagePath;
				// ถ้า oldImagePath เป็น path เต็ม ให้แยกเฉพาะชื่อไฟล์
				if (oldImagePath.contains("/") || oldImagePath.contains("\\")) {
					oldFileName = oldImagePath
							.substring(Math.max(oldImagePath.lastIndexOf('/'), oldImagePath.lastIndexOf('\\')) + 1);
				}
				File oldFile = new File(PROFILE_UPLOAD_PATH + File.separator + oldFileName);
				if (oldFile.exists()) {
					boolean deleted = oldFile.delete();
					System.out.println(
							"🗑️ Deleted old profile image: " + oldFile.getAbsolutePath() + " - Success: " + deleted);
				}
			}

			// บันทึกไฟล์ใหม่
			String fileName = student.getStuId() + "_" + System.currentTimeMillis() + "_"
					+ imageFile.getOriginalFilename();
			File dest = new File(PROFILE_UPLOAD_PATH + File.separator + fileName);
			imageFile.transferTo(dest);

			// ✅ เก็บเฉพาะชื่อไฟล์ในฐานข้อมูล (ไม่ใช่ path เต็ม)
			student.setStu_image(fileName);

			System.out.println("📸 Profile image saved: " + dest.getAbsolutePath());
			System.out.println("💾 Image filename in DB: " + fileName);

			return null; // ไม่มี error

		} catch (IOException e) {
			e.printStackTrace();
			return "ไม่สามารถอัปโหลดรูปภาพได้: " + e.getMessage();
		}
	}

	// ✅ ดึงชื่อโปรเจค
	private String getProjectName(Student496 student) {
		if (student.getProject() != null) {
			return student.getProject().getProj_NameTh();
		}
		return "";
	}

	// ✅ ส่งรูปภาพโปรไฟล์
	@RequestMapping(value = "/profileImage/{filename}", method = RequestMethod.GET)
	public void getProfileImage(@PathVariable("filename") String filename, HttpServletResponse response)
			throws IOException {

		// ตรวจสอบว่าเป็นไฟล์ default หรือไม่
		if ("default-profile.png".equals(filename)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Default image should be handled by web server");
			return;
		}

		File imageFile = new File(PROFILE_UPLOAD_PATH + File.separator + filename);

		if (!imageFile.exists()) {
			System.err.println("❌ Profile image not found: " + imageFile.getAbsolutePath());
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Profile image not found");
			return;
		}

		// กำหนด Content-Type ตามนามสกุลไฟล์
		String contentType = "image/jpeg"; // default
		if (filename.toLowerCase().endsWith(".png")) {
			contentType = "image/png";
		} else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
			contentType = "image/jpeg";
		}

		response.setContentType(contentType);
		response.setContentLength((int) imageFile.length());

		// ตั้งค่า cache headers
		response.setHeader("Cache-Control", "public, max-age=86400"); // cache 1 วัน
		response.setDateHeader("Expires", System.currentTimeMillis() + 86400 * 1000L);

		// ส่งไฟล์รูปภาพ
		try (FileInputStream fis = new FileInputStream(imageFile)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				response.getOutputStream().write(buffer, 0, bytesRead);
			}
			response.getOutputStream().flush();
		} catch (IOException e) {
			System.err.println("❌ Error serving profile image: " + filename + " - " + e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving image");
		}
	}
}