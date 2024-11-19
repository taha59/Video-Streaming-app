package com.programming.taha.Youtubeclone.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    String uploadFile(MultipartFile file);

    void deleteFile(String url);

    ResponseEntity<Resource> downloadFile(String s3Url);
}
