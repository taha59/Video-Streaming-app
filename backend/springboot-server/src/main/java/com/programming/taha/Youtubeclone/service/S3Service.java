package com.programming.taha.Youtubeclone.service;

import com.programming.taha.Youtubeclone.config.AwsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.transcribe.TranscribeClient;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service implements FileService{

    @Value("${s3.bucket.name}")
    String BUCKETNAME;

    private final S3Client s3Client;

    @Autowired
    public S3Service(AwsCredentialsProvider awsCredentialsProvider) {
        this.s3Client = S3Client.builder()
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Override
    public String uploadFile (MultipartFile file) {

        var filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        var key = UUID.randomUUID() + "." + filenameExtension;

        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(BUCKETNAME)
                    .key(key)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(objectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        } catch (IOException ioException){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error uploading File!");
        }


        var video_url = s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(BUCKETNAME).key(key).build());

        return video_url.toString();
    }

    @Override
    public void deleteFile(String url){

        String s3Key = extractS3Key(url);

        try {
            DeleteObjectRequest ObjectDeleteRequest = DeleteObjectRequest.builder()
                    .bucket(BUCKETNAME)
                    .key(s3Key).build();

            s3Client.deleteObject(ObjectDeleteRequest);
            System.out.println("Object deleted -- " + s3Key);

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    private static String extractS3Key(String url) {
        // Remove the protocol (e.g., "https://") and split the URL by '/'
        String[] urlParts = url.split("/");

        // Rebuild the key from the parts after the bucket domain
        StringBuilder keyBuilder = new StringBuilder();
        for (int i = 3; i < urlParts.length; i++) {
            if (i > 3) {
                keyBuilder.append("/");
            }
            keyBuilder.append(urlParts[i]);
        }

        return keyBuilder.toString();
    }
}