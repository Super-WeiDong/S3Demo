package com.antra.S3DemoApp.Controller;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.antra.S3DemoApp.Service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }

        try {
            // Upload the file to S3
            String key = file.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            String etag = s3Service.uploadFileToS3(bucketName, key, file.getInputStream(), metadata);

            // Return the S3 object ETag as the response
            return ResponseEntity.ok(etag);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the file.");
        }
    }

    @GetMapping("/delete")
    public String showUpDeleteForm() {
        return "delete";
    }

    @PostMapping("/delete")
    public ResponseEntity<String> handleFileUpload(@RequestParam("fileName") String fileName) {

        try {
            // Delete the file on S3
            s3Service.deleteFileFromS3(bucketName,fileName);

            // Return success info
            return ResponseEntity.ok("Successfully delete file: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the file.");
        }
    }

    @GetMapping("/getFilesAndFolders")
    public ResponseEntity<String> getFiles() {
        String bucketName = "udemy-test-weidong-bucket";
        System.out.println(s3Service.listFilesInS3(bucketName));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "text/plain");
        ResponseEntity<String> res = new ResponseEntity<>(s3Service.listFilesInS3(bucketName),headers,HttpStatus.OK);
        return res;
    }
}

