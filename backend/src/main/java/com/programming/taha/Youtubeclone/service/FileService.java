package com.programming.taha.Youtubeclone.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    String uploadFile(MultipartFile file);

    void deleteFile(String videoId);
}
