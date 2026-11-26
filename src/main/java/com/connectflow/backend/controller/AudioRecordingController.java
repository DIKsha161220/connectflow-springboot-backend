package com.connectflow.backend.controller;

import com.connectflow.backend.dto.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/recordings")
@CrossOrigin(origins = "*")
public class AudioRecordingController {

    private final Path storageDirectory = Paths.get("data", "recordings").toAbsolutePath().normalize();

    public AudioRecordingController() {
        try {
            Files.createDirectories(storageDirectory);
        } catch (IOException e) {
            System.err.println("Could not create recordings directory: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadRecording(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "contactName", required = false) String contactName,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "notes", required = false) String notes) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.<Map<String, Object>>failure("Uploaded audio file is empty"));
        }

        try {
            Files.createDirectories(storageDirectory);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String originalFilename = file.getOriginalFilename();
            String extension = ".wav";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            } else if (file.getContentType() != null && file.getContentType().contains("webm")) {
                extension = ".webm";
            } else if (file.getContentType() != null && file.getContentType().contains("mp3")) {
                extension = ".mp3";
            }

            String savedFilename = "rec_" + timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            Path targetLocation = storageDirectory.resolve(savedFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("filename", savedFilename);
            responseData.put("fileUrl", "/api/recordings/" + savedFilename);
            responseData.put("contactName", contactName != null ? contactName : "Unknown");
            responseData.put("phone", phone != null ? phone : "");
            responseData.put("notes", notes != null ? notes : "");
            responseData.put("sizeBytes", file.getSize());
            responseData.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Audio recording uploaded successfully", responseData));
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Map<String, Object>>failure("Failed to save audio file: " + ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listRecordings() {
        List<Map<String, Object>> filesList = new ArrayList<>();
        try {
            if (Files.exists(storageDirectory)) {
                File folder = storageDirectory.toFile();
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.isFile()) {
                            Map<String, Object> fileInfo = new HashMap<>();
                            fileInfo.put("filename", f.getName());
                            fileInfo.put("fileUrl", "/api/recordings/" + f.getName());
                            fileInfo.put("sizeBytes", f.length());
                            fileInfo.put("lastModified", f.lastModified());
                            filesList.add(fileInfo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<Map<String, Object>>>failure("Could not list recordings: " + e.getMessage()));
        }

        filesList.sort((a, b) -> Long.compare((Long) b.get("lastModified"), (Long) a.get("lastModified")));
        return ResponseEntity.ok(ApiResponse.success("Recordings retrieved", filesList));
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> serveRecording(@PathVariable("filename") String filename) {
        try {
            Path filePath = storageDirectory.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "audio/wav";
                if (filename.endsWith(".mp3")) {
                    contentType = "audio/mpeg";
                } else if (filename.endsWith(".webm")) {
                    contentType = "audio/webm";
                } else if (filename.endsWith(".ogg")) {
                    contentType = "audio/ogg";
                }

                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
