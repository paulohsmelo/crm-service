package com.paulohsmelo.crmservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface IUploadService {

    URL uploadCustomerPhoto(Long customerId, MultipartFile file);

    URL createDownloadPhotoURL(String photoUrl);
}
