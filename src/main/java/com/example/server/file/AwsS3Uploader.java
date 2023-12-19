package com.example.server.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class AwsS3Uploader {
    private final S3Client s3Client;
    private final FileApplication fileApplication;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile imgFile) {
        String translatedFileName = fileApplication.translateFileName(imgFile.getOriginalFilename());

        PutObjectRequest putObj = PutObjectRequest.builder()
                .bucket(bucket)
                .key(translatedFileName)
                .contentType(imgFile.getContentType())
                .build();

        try {
            s3Client.putObject(putObj, RequestBody.fromInputStream(imgFile.getInputStream(), imgFile.getSize()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return translatedFileName;
    }


    public void delete(String fileUrl) {
        ObjectIdentifier key = ObjectIdentifier.builder()
                .key(fileUrl)
                .build();

        Delete del = Delete.builder()
                .objects(key)
                .build();

        DeleteObjectsRequest multiObjectDeleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(del)
                .build();

        s3Client.deleteObjects(multiObjectDeleteRequest);
    }
}
