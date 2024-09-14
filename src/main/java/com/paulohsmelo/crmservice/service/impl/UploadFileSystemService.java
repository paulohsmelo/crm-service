package com.paulohsmelo.crmservice.service.impl;

import com.paulohsmelo.crmservice.exception.BadRequestException;
import com.paulohsmelo.crmservice.service.IUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class UploadFileSystemService implements IUploadService {

    @Value("${application.upload-directory}")
    private String uploadDirectory;

    @Override
    public String uploadCustomerPhoto(Long customerId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty: " + file.getOriginalFilename());
        }
        try {
            Path uploadPath = Paths.get(getUploadDirectory(customerId));
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = Paths.get(getUploadDirectory(customerId) + "/" + file.getOriginalFilename());

            log.debug("Saving file {}", filePath);
            Files.copy(file.getInputStream(), filePath);

            return filePath.toString();
        } catch (FileAlreadyExistsException e) {
            throw new BadRequestException("File already exists: " + file.getOriginalFilename());
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + file.getOriginalFilename());
        }
    }

    private String getUploadDirectory(Long customerId) {
        return uploadDirectory + customerId + "/";
    }
}
