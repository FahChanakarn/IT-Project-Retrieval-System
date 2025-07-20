package com.springmvc.controller;

import com.springmvc.manager.UploadManager;
import com.springmvc.model.DocumentFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Controller
public class DownloadController {

	private static final String BASE_UPLOAD_PATH = "D:/Project496Uploads/uploadsFile/";

	@RequestMapping(value = "/download/file/{id}", method = RequestMethod.GET)
	public void downloadFile(@PathVariable("id") int fileId, HttpServletResponse response) throws IOException {

		UploadManager manager = new UploadManager();
		DocumentFile doc = manager.getFileById(fileId);

		if (doc == null || !"file".equals(doc.getFiletype())) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		File file = new File(BASE_UPLOAD_PATH + doc.getFilepath());

		if (!file.exists()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
		Files.copy(file.toPath(), response.getOutputStream());
	}
}
