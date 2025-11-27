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

		String firstName = request.getParameter("stu_firstName");
		String lastName = request.getParameter("stu_lastName");
		String password = request.getParameter("stu_password");

		if (firstName == null || lastName == null || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {

			String projectName = getProjectName(student);
			ModelAndView mav = new ModelAndView("editProfile");
			mav.addObject("projectName", projectName);
			mav.addObject("error", "กรุณากรอกชื่อและนามสกุลให้ครบถ้วน");
			return mav;
		}

		student.setStu_firstName(firstName.trim());
		student.setStu_lastName(lastName.trim());

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
		}

		if (imageFile != null && !imageFile.isEmpty()) {
			String uploadResult = handleImageUpload(imageFile, student);
			if (uploadResult != null) {
				String projectName = getProjectName(student);
				ModelAndView mav = new ModelAndView("editProfile");
				mav.addObject("projectName", projectName);
				mav.addObject("error", uploadResult);
				return mav;
			}
		}

		try {
			manager.updateStudent(student);
			session.setAttribute("student", student);

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

	private String handleImageUpload(MultipartFile imageFile, Student496 student) {
		try {
			String contentType = imageFile.getContentType();
			if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")
					&& !contentType.equals("image/jpg")) {
				return "กรุณาอัปโหลดไฟล์ JPG หรือ PNG เท่านั้น";
			}

			if (imageFile.getSize() > 5 * 1024 * 1024) {
				return "ไฟล์รูปภาพต้องมีขนาดไม่เกิน 5MB";
			}

			File uploadDir = new File(UploadController.BASE_UPLOAD_PATH);
			if (!uploadDir.exists()) {
				uploadDir.mkdirs();
			}

			String oldImagePath = student.getStu_image();
			if (oldImagePath != null && !oldImagePath.isEmpty() && !oldImagePath.contains("default-profile.png")) {
				String oldFileName = oldImagePath;
				if (oldImagePath.contains("/") || oldImagePath.contains("\\")) {
					oldFileName = oldImagePath
							.substring(Math.max(oldImagePath.lastIndexOf('/'), oldImagePath.lastIndexOf('\\')) + 1);
				}
				File oldFile = new File(UploadController.BASE_UPLOAD_PATH + File.separator + oldFileName);
				if (oldFile.exists()) {
					oldFile.delete();
				}
			}

			String fileName = student.getStuId() + "_" + System.currentTimeMillis() + "_"
					+ imageFile.getOriginalFilename();
			File dest = new File(UploadController.BASE_UPLOAD_PATH + File.separator + fileName);
			imageFile.transferTo(dest);

			student.setStu_image(fileName);

			return null;

		} catch (IOException e) {
			e.printStackTrace();
			return "ไม่สามารถอัปโหลดรูปภาพได้: " + e.getMessage();
		}
	}

	private String getProjectName(Student496 student) {
		if (student.getProject() != null) {
			return student.getProject().getProj_NameTh();
		}
		return "";
	}

	@RequestMapping(value = "/profileImage/{filename}", method = RequestMethod.GET)
	public void getProfileImage(@PathVariable("filename") String filename, HttpServletResponse response)
			throws IOException {

		if ("default-profile.png".equals(filename)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Default image should be handled by web server");
			return;
		}

		File imageFile = new File(UploadController.BASE_UPLOAD_PATH + File.separator + filename);

		if (!imageFile.exists()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Profile image not found");
			return;
		}

		String contentType = "image/jpeg";
		if (filename.toLowerCase().endsWith(".png")) {
			contentType = "image/png";
		} else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
			contentType = "image/jpeg";
		}

		response.setContentType(contentType);
		response.setContentLength((int) imageFile.length());

		response.setHeader("Cache-Control", "public, max-age=86400");
		response.setDateHeader("Expires", System.currentTimeMillis() + 86400 * 1000L);

		try (FileInputStream fis = new FileInputStream(imageFile)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				response.getOutputStream().write(buffer, 0, bytesRead);
			}
			response.getOutputStream().flush();
		} catch (IOException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving image");
		}
	}
}