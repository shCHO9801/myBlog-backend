package com.shcho.myBlog.common.service;

import com.shcho.myBlog.common.dto.FileUploadResponseDto;
import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.libs.exception.ErrorCode;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final MinioClient minioClient;
    private final String bucket = "shblog";

    public FileUploadResponseDto uploadFile(MultipartFile file, String dir, String username) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);

            String uuid = UUID.randomUUID().toString();
            String fileName = String.format("%s/%s-%s%s", dir, username, uuid, fileExtension);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), - 1L)
                            .contentType(file.getContentType())
                            .build()
            );

            String url = String.format("https://minio-api.csh980116.synology.me/shblog/%s", fileName);
            return FileUploadResponseDto.from(url);
        } catch (Exception e) {
            log.error("[Minio] 파일 업로드 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }

    private String getFileExtension(String originalFilename) {
        if(originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}
