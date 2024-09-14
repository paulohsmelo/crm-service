package com.paulohsmelo.crmservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface IUploadService {

    String uploadCustomerPhoto(Long customerId, MultipartFile file);
}
