package com.yash.controller;


import com.yash.service.BackblazeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/backblaze")
@CrossOrigin(origins = "*")
public class BackblazeController {

    @Autowired
    private BackblazeService backblazeService;

    /**
     * Upload a single file
     * POST /api/backblaze/upload
     */

    @GetMapping("/test")
    public ResponseEntity<?> testConnection() {
        try {
            String result = backblazeService.testConnection();
            boolean success = result.startsWith("SUCCESS");

            return ResponseEntity.ok(Map.of(
                    "success", success,
                    "message", result,
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Exception: " + e.getMessage(),
                    "errorType", e.getClass().getSimpleName(),
                    "cause", e.getCause() != null ? e.getCause().getMessage() : "None"
            ));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "File is empty"
                        ));
            }

            // Validate file size (max 50MB)
            long maxSize = 50 * 1024 * 1024; // 50MB
            if (file.getSize() > maxSize) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "File size exceeds maximum limit of 50MB"
                        ));
            }

            String fileUrl = backblazeService.uploadFile(file);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("url", fileUrl);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("contentType", file.getContentType());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to upload file: " + e.getMessage()
                    ));
        }
    }

    /**
     * Upload multiple files
     * POST /api/backblaze/upload/multiple
     */
    @PostMapping("/upload/multiple")
    public ResponseEntity<?> uploadMultiple(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "No files provided"
                        ));
            }

            List<Map<String, Object>> uploadedFiles = new java.util.ArrayList<>();

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileUrl = backblazeService.uploadFile(file);

                    Map<String, Object> fileData = new HashMap<>();
                    fileData.put("url", fileUrl);
                    fileData.put("fileName", file.getOriginalFilename());
                    fileData.put("fileSize", file.getSize());
                    fileData.put("contentType", file.getContentType());

                    uploadedFiles.add(fileData);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", uploadedFiles.size() + " file(s) uploaded successfully");
            response.put("files", uploadedFiles);
            response.put("count", uploadedFiles.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to upload files: " + e.getMessage()
                    ));
        }
    }

    /**
     * Delete a file
     * DELETE /api/backblaze/delete/{fileName}
     */
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        try {
            backblazeService.deleteFile(fileName);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "File deleted successfully",
                    "fileName", fileName
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to delete file: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get file URL
     * GET /api/backblaze/file/{fileName}
     */
    @GetMapping("/file/{fileName}")
    public ResponseEntity<?> getFileUrl(@PathVariable String fileName) {
        try {
            String fileUrl = backblazeService.getFileUrl(fileName);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "File URL retrieved successfully",
                    "url", fileUrl,
                    "fileName", fileName
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to get file URL: " + e.getMessage()
                    ));
        }
    }

    /**
     * List all files in bucket
     * GET /api/backblaze/files
     */
    @GetMapping("/files")
    public ResponseEntity<?> listFiles() {
        try {
            List<BackblazeService.FileInfo> files = backblazeService.listFiles();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Files retrieved successfully",
                    "files", files,
                    "count", files.size()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to list files: " + e.getMessage()
                    ));
        }
    }

    /**
     * Health check endpoint
     * GET /api/backblaze/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Backblaze service is running",
                "timestamp", System.currentTimeMillis()
        ));
    }
}