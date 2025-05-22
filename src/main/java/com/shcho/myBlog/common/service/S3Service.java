package com.shcho.myBlog.common.service;

import com.shcho.myBlog.common.dto.FileUploadResponseDto;
import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.libs.exception.ErrorCode;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.shcho.myBlog.libs.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public FileUploadResponseDto uploadByType(MultipartFile file, String type, String username) {
        if ("image".equalsIgnoreCase(type)) {
            return uploadImage(file, type, username);
        } else if ("file".equalsIgnoreCase(type)) {
            return uploadFile(file, type, username);
        } else {
            throw new CustomException(INVALID_FILE_TYPE);
        }
    }

    private FileUploadResponseDto uploadImage(MultipartFile file, String dir, String username) {
        validateImageExtension(file);
        return upload(file, dir, username);
    }

    private FileUploadResponseDto uploadFile(MultipartFile file, String dir, String username) {
        // TODO : 필요시 일반 파일 확장자 검증 추가
        return upload(file, dir, username);
    }

    private FileUploadResponseDto upload(MultipartFile file, String dir, String username) {
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
            log.error("[Minio] 파일 업로드 실패:", e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }

    private void validateImageExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new CustomException(INVALID_IMAGE_FORMAT);
        }

        String ext = getFileExtension(originalFilename).toLowerCase();

        // 허용된 이미지 확장자
        if (!(ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") || ext.equals(".gif"))) {
            throw new CustomException(INVALID_IMAGE_FORMAT);
        }
    }


    private String getFileExtension(String originalFilename) {
        if(originalFilename == null || !originalFilename.contains(".")) {
            throw new CustomException(INVALID_FILE_FORMAT);
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}
