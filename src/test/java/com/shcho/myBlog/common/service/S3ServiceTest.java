package com.shcho.myBlog.common.service;

import com.shcho.myBlog.common.dto.FileUploadResponseDto;
import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.libs.exception.ErrorCode;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import static com.shcho.myBlog.libs.exception.ErrorCode.FILE_UPLOAD_FAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("파일 업로드 테스트")
class S3ServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("파일 업로드 - 성공")
    void uploadFileSuccess() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test".getBytes()
        );

        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        // when
        FileUploadResponseDto response = s3Service.uploadFile(file, "profile", "username");

        // then
        assertNotNull(response);
        assertTrue(response.url().startsWith("https://minio-api.csh980116.synology.me/shblog/profile/username-"));
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("파일 업로드 - 실패")
    void uploadFileFail() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test".getBytes()
        );

        doThrow(new RuntimeException("Upload failed")).when(minioClient).putObject(any(PutObjectArgs.class));

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> s3Service.uploadFile(file, "profile", "username")
        );

        assertEquals(FILE_UPLOAD_FAIL, exception.getErrorCode());
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("파일 업로드 - 파일이 null일 경우 예외 발생")
    void uploadFileNull() {
        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                s3Service.uploadFile(null, "profile", "username")
        );

        assertEquals(FILE_UPLOAD_FAIL, exception.getErrorCode());
    }


}