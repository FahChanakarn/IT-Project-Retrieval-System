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

		System.out.println("Secure download request for file ID: " + fileId);

		try {
			UploadManager manager = new UploadManager();
			DocumentFile doc = manager.getFileById(fileId);

			if (doc == null) {
				System.err.println("Document not found for ID: " + fileId);
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Document not found!");
				return;
			}

			if (!"file".equals(doc.getFiletype())) {
				System.err.println("Document is not a file type: " + doc.getFiletype());
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Document is not a file!");
				return;
			}

			File originalFile = new File(BASE_UPLOAD_PATH + doc.getFilepath());
			if (!originalFile.exists()) {
				System.err.println("Physical file not found: " + originalFile.getAbsolutePath());
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Physical file not found!");
				return;
			}

			// คำนวณวันหมดอายุ (ทดสอบ - หมดอายุทันที)
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 1); // หมดอายุภายใน 1 นาที
			Date fileExpiryDate = cal.getTime();

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("th", "TH"));
			System.out.println("Creating PDF with expiry date: " + sdf.format(fileExpiryDate));
			System.out.println("Current time: " + sdf.format(new Date()));

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

			System.out.println("Secured PDF downloaded successfully");
			System.out.println("File: " + downloadFilename);
			System.out.println("Expires: " + sdf.format(fileExpiryDate));
			System.out.println("Size: " + tempFile.length() + " bytes");

			// ลบไฟล์ temp
			if (!tempFile.delete()) {
				System.err.println("Warning: Could not delete temp file: " + tempFile.getAbsolutePath());
			}

		} catch (Exception e) {
			System.err.println("Error in secure download: " + e.getMessage());
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

			// คำนวณเวลาแจ้งเตือน (30 วินาทีก่อนหมดอายุ)
			Calendar warningCal = Calendar.getInstance();
			warningCal.setTime(expiryDate);
			warningCal.add(Calendar.SECOND, -30);
			long warningTimestamp = warningCal.getTime().getTime();

			// สร้าง JavaScript code (ไม่ใช้ภาษาไทยโดยตรง)
			String javascript = String.format("console.println('PDF Security Check Start');"
					+ "var expiryTimestamp = %d;" + "var warningTimestamp = %d;" + "try {" + "  var now = new Date();"
					+ "  var currentTimestamp = now.getTime();" + "  console.println('Current: ' + currentTimestamp);"
					+ "  console.println('Expiry: ' + expiryTimestamp);"
					+ "  console.println('Diff seconds: ' + Math.floor((expiryTimestamp - currentTimestamp) / 1000));"
					+ "  if (currentTimestamp >= expiryTimestamp) {" + "    console.println('STATUS: EXPIRED');"
					+ "    app.alert('Document Expired\\n\\nPlease download again', 0, 0);" + "    this.closeDoc(true);"
					+ "  } else if (currentTimestamp >= warningTimestamp) {"
					+ "    var secondsLeft = Math.floor((expiryTimestamp - currentTimestamp) / 1000);"
					+ "    console.println('STATUS: WARNING, seconds left: ' + secondsLeft);"
					+ "    app.alert('Document will expire soon\\n\\nTime left: ' + secondsLeft + ' seconds', 1, 0);"
					+ "  } else {" + "    console.println('STATUS: VALID');" + "  }" + "} catch(e) {"
					+ "  console.println('ERROR: ' + e.message);" + "  app.alert('Error: ' + e.message, 0, 0);" + "}",
					expiryTimestamp, warningTimestamp);

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

			System.out.println("Created PDF with JavaScript protection");
			System.out.println("Output: " + outputPath);
			System.out.println("Expiry: " + sdf.format(expiryDate));

			return true;

		} catch (Exception e) {
			System.err.println("Error creating expiring PDF: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (pdfDoc != null) {
					pdfDoc.close();
				}
			} catch (Exception e) {
				System.err.println("Error closing PDF: " + e.getMessage());
			}
		}
	}

	@RequestMapping(value = "/download/file/{id}/{filename}", method = RequestMethod.GET)
	public void viewFile(@PathVariable("id") int fileId, @PathVariable("filename") String filename,
			HttpServletResponse response) throws IOException {

		System.out.println("View file request for ID: " + fileId);

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

		System.out.println("File viewed successfully");
	}
}