package com.paulohsmelo.crmservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUploadService {

    String uploadCustomerPhoto(Long customerId, MultipartFile file);
}
