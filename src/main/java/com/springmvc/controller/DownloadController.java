package com.springmvc.controller;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.springmvc.manager.UploadManager;
import com.springmvc.model.DocumentFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class DownloadController {

	private static final String BASE_UPLOAD_PATH = "D:/Project496Uploads/uploadsFile/";
	private static final int EXPIRY_DAYS = 14; // จำนวนวันที่ไฟล์หมดอายุ

	@RequestMapping(value = "/download/secure/{id}", method = RequestMethod.GET)
	public void downloadSecureFile(@PathVariable("id") int fileId, HttpServletResponse response) throws IOException {

		try {
			UploadManager manager = new UploadManager();
			DocumentFile doc = manager.getFileById(fileId);

			if (doc == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Document not found!");
				return;
			}

			if (!"file".equals(doc.getFiletype())) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Document is not a file!");
				return;
			}

			File originalFile = new File(BASE_UPLOAD_PATH + doc.getFilepath());
			if (!originalFile.exists()) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Physical file not found!");
				return;
			}

			// คำนวณวันหมดอายุ (14 วัน)
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, EXPIRY_DAYS);
			Date fileExpiryDate = cal.getTime();

			// สร้างไฟล์ temp พร้อม JavaScript
			File tempFile = File.createTempFile("secured_", ".pdf");
			tempFile.deleteOnExit();

			boolean success = createExpiringPDF(originalFile, tempFile.getAbsolutePath(), fileExpiryDate);

			if (!success) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create secured PDF");
				return;
			}

			// ตั้งค่า response headers
			response.setContentType("application/pdf");

			String originalFilename = doc.getFilename();
			String downloadFilename;
			if (originalFilename.toLowerCase().endsWith(".pdf")) {
				downloadFilename = "secured_" + originalFilename;
			} else {
				downloadFilename = "secured_" + originalFilename + ".pdf";
			}

			String encodedFileName = URLEncoder.encode(downloadFilename, StandardCharsets.UTF_8.toString())
					.replaceAll("\\+", "%20");
			response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
			response.setHeader("Content-Length", String.valueOf(tempFile.length()));

			// ส่งไฟล์
			try (FileInputStream fis = new FileInputStream(tempFile); OutputStream os = response.getOutputStream()) {
				byte[] buffer = new byte[8192];
				int bytesRead;
				while ((bytesRead = fis.read(buffer)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				os.flush();
			}

			// ลบไฟล์ temp
			tempFile.delete();

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error creating secured file: " + e.getMessage());
		}
	}

	private boolean createExpiringPDF(File originalFile, String outputPath, Date expiryDate) {
		PdfReader reader = null;
		PdfWriter writer = null;
		PdfDocument pdfDoc = null;

		try {
			// คำนวณ timestamp
			long expiryTimestamp = expiryDate.getTime();

			// คำนวณเวลาแจ้งเตือน (1 วันก่อนหมดอายุ)
			Calendar warningCal = Calendar.getInstance();
			warningCal.setTime(expiryDate);
			warningCal.add(Calendar.DAY_OF_MONTH, -1);
			long warningTimestamp = warningCal.getTime().getTime();

			// สร้าง JavaScript code
			String javascript = String.format("var expiryTimestamp = %d;" + "var warningTimestamp = %d;" + "try {"
					+ "  var now = new Date();" + "  var currentTimestamp = now.getTime();"
					+ "  if (currentTimestamp >= expiryTimestamp) {"
					+ "    app.alert('Document Expired\\n\\nPlease download again', 0, 0);" + "    this.closeDoc(true);"
					+ "  } else if (currentTimestamp >= warningTimestamp) {"
					+ "    var daysLeft = Math.floor((expiryTimestamp - currentTimestamp) / (1000 * 60 * 60 * 24));"
					+ "    var hoursLeft = Math.floor(((expiryTimestamp - currentTimestamp) %% (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));"
					+ "    app.alert('Document will expire soon\\n\\nTime left: ' + daysLeft + ' days ' + hoursLeft + ' hours', 1, 0);"
					+ "  }" + "} catch(e) {" + "  app.alert('Error: ' + e.message, 0, 0);" + "}", expiryTimestamp,
					warningTimestamp);

			// อ่านไฟล์ต้นฉบับ
			reader = new PdfReader(originalFile);
			writer = new PdfWriter(outputPath);
			pdfDoc = new PdfDocument(reader, writer);

			// เพิ่ม JavaScript Action
			pdfDoc.getCatalog().setOpenAction(PdfAction.createJavaScript(javascript));

			// เพิ่ม Metadata
			PdfDocumentInfo info = pdfDoc.getDocumentInfo();
			info.setCreator("Secured Document System");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			info.setKeywords("Secured, ExpiryTimestamp: " + expiryTimestamp);

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (pdfDoc != null) {
					pdfDoc.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/download/file/{id}/{filename}", method = RequestMethod.GET)
	public void viewFile(@PathVariable("id") int fileId, @PathVariable("filename") String filename,
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

		response.setContentType("application/pdf");
		String encodedFileName = URLEncoder.encode(doc.getFilename(), StandardCharsets.UTF_8.toString())
				.replaceAll("\\+", "%20");
		response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + encodedFileName);
		response.setContentLength((int) file.length());

		try (FileInputStream fis = new FileInputStream(file); OutputStream os = response.getOutputStream()) {
			byte[] buffer = new byte[8192];
			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.flush();
		}
	}
}