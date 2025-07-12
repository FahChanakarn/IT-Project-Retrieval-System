package com.springmvc.controller;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UploadController {

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description,
            RedirectAttributes redirectAttributes) {
        
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload");
            return "redirect:/upload";
        }
        
        try {
            // Get upload directory path
            String uploadDir = System.getProperty("catalina.home") + File.separator + "uploads";
            File uploadDirectory = new File(uploadDir);
            
            // Create directory if it doesn't exist
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }
            
            // Create unique filename
            String originalFilename = file.getOriginalFilename();
            String filename = System.currentTimeMillis() + "_" + originalFilename;
            
            // Save file to disk
            File destinationFile = new File(uploadDirectory, filename);
            file.transferTo(destinationFile);
            
            // Log file information
            System.out.println("File uploaded: " + filename);
            System.out.println("File size: " + file.getSize() + " bytes");
            System.out.println("Content type: " + file.getContentType());
            System.out.println("Description: " + description);
            
            redirectAttributes.addFlashAttribute("message", 
                "File uploaded successfully: " + originalFilename);
            
        } catch (IOException e) {
        	System.out.println("Error uploading file" + e.getMessage());
            redirectAttributes.addFlashAttribute("error", 
                "Error uploading file: " + e.getMessage());
        }
        
        return "redirect:/upload";
    }

}
