package com.paulohsmelo.crmservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

public interface IUploadService {

    URL uploadCustomerPhoto(Long customerId, MultipartFile file);
}
