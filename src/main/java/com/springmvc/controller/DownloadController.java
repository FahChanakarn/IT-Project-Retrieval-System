package com.springmvc.controller;

import com.springmvc.manager.UploadManager;
import com.springmvc.model.DocumentFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class DownloadController {

	private static final String BASE_UPLOAD_PATH = "D:/Project496Uploads/uploadsFile/";

	// รับทั้ง id และชื่อไฟล์ใน URL
	@RequestMapping(value = "/download/file/{id}/{filename}", method = RequestMethod.GET)
	public void downloadFile(@PathVariable("id") int fileId, @PathVariable("filename") String filename,
			HttpServletResponse response) throws IOException {

		UploadManager manager = new UploadManager();
		DocumentFile doc = manager.getFileById(fileId);

		if (doc == null || !"file".equals(doc.getFiletype())) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found!");
			return;
		}

		File file = new File(BASE_UPLOAD_PATH + doc.getFilepath());
		if (!file.exists()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found on server!");
			return;
		}

		// ตั้ง Content-Type เป็น PDF
		response.setContentType("application/pdf");

		// encode ชื่อไฟล์ภาษาไทย
		String encodedFileName = URLEncoder.encode(doc.getFilename(), StandardCharsets.UTF_8.toString())
				.replaceAll("\\+", "%20");

		// เปิดใน browser
		response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + encodedFileName);

		// ส่งไฟล์ PDF
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				response.getOutputStream().write(buffer, 0, bytesRead);
			}
			response.getOutputStream().flush();
		}
	}
}
