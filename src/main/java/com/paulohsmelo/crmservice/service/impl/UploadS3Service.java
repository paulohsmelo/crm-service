package com.paulohsmelo.crmservice.service.impl;

import com.paulohsmelo.crmservice.service.IUploadService;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class UploadS3Service implements IUploadService {

    @Value("${application.s3.bucket-name}")
    private String bucketName;

    private final S3Template s3Template;

    @Override
    public URL uploadCustomerPhoto(Long customerId, MultipartFile file) {
        try {
            if (!s3Template.bucketExists(bucketName)) {
                s3Template.createBucket(bucketName);
            }
            String fileKey = customerId + "/" + file.getOriginalFilename();
            S3Resource resource = s3Template.upload(bucketName, fileKey, file.getInputStream());
            return resource.getURL();
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + file.getOriginalFilename());
        }
    }
}
