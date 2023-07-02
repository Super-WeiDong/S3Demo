package com.antra.S3DemoApp.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.List;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3Client;

    public String uploadFileToS3(String bucketName, String key, InputStream inputStream, ObjectMetadata metadata) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream,metadata);
        PutObjectResult res = amazonS3Client.putObject(putObjectRequest);
        return res.getETag();//Entity Tag, unique identifier assigned to each object stored in S3
    }

    public void deleteFileFromS3(String bucketName, String key) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, key);
        amazonS3Client.deleteObject(deleteObjectRequest);
    }

    public String listFilesInS3(String bucketName) {
        ObjectListing objectListing = amazonS3Client.listObjects(bucketName);
        StringBuilder sb = new StringBuilder();

        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        for (S3ObjectSummary objectSummary : objectSummaries) {
            String key = objectSummary.getKey();
            if (key.endsWith("/")) {
                sb.append("Folder: " + key + "\n");
            } else {
                sb.append("File: " + key + "\n");
            }
        }

        return sb.toString();
    }
}
