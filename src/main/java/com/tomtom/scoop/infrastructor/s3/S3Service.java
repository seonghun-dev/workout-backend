package com.tomtom.scoop.infrastructor.s3;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.tomtom.scoop.global.exception.BusinessException;
import com.tomtom.scoop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class S3Service {
    private final AmazonS3Client amazonS3Client;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        String fileName = dirName + "/" + LocalDate.now() + "_" + multipartFile.getOriginalFilename();
        return putS3(multipartFile, fileName);
    }


    private String putS3(MultipartFile uploadFile, String fileName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(uploadFile.getContentType());
        objectMetadata.setContentLength(uploadFile.getSize());
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile.getInputStream(), objectMetadata));
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

}
