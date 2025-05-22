package com.shcho.myBlog.common.service;

import com.shcho.myBlog.common.dto.FileUploadResponseDto;
import com.shcho.myBlog.libs.exception.CustomException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import static com.shcho.myBlog.libs.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("파일 업로드 서비스 테스트")
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
    @DisplayName("uploadByType - 이미지 업로드 성공")
    void uploadImageSuccess() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "image-content".getBytes()
        );

        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        // when
        FileUploadResponseDto response = s3Service.uploadByType(file, "image", "username");

        // then
        assertNotNull(response);
        assertTrue(response.url().startsWith("https://minio-api.csh980116.synology.me/shblog/profile/username-") ||
                response.url().contains("username-")); // 정확한 디렉토리 이름은 호출 시 결정됨
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("uploadByType - 이미지 확장자 아닌 경우 예외")
    void uploadImageInvalidExtension() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "malicious.exe", "application/octet-stream", "virus".getBytes()
        );

        // when & then
        CustomException ex = assertThrows(CustomException.class,
                () -> s3Service.uploadByType(file, "image", "username")
        );
        assertEquals(INVALID_IMAGE_FORMAT, ex.getErrorCode());
    }

    @Test
    @DisplayName("uploadByType - 파일 업로드 성공")
    void uploadGenericFileSuccess() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "test pdf".getBytes()
        );

        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        // when
        FileUploadResponseDto response = s3Service.uploadByType(file, "file", "username");

        // then
        assertNotNull(response);
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("uploadByType - 파일이 null일 경우 예외")
    void uploadNullFile() {
        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> s3Service.uploadByType(null, "file", "username")
        );

        assertEquals(FILE_UPLOAD_FAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("uploadByType - 지원하지 않는 타입일 경우 예외")
    void uploadUnsupportedType() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "file.txt", "text/plain", "content".getBytes()
        );

        // when & then
        CustomException ex = assertThrows(CustomException.class,
                () -> s3Service.uploadByType(file, "unknown", "username")
        );

        assertEquals(INVALID_FILE_TYPE, ex.getErrorCode());
    }

    @Test
    @DisplayName("uploadByType - 업로드 실패 예외")
    void uploadInternalFail() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "content".getBytes()
        );

        doThrow(new RuntimeException("MinIO down")).when(minioClient).putObject(any(PutObjectArgs.class));

        // when & then
        CustomException ex = assertThrows(CustomException.class,
                () -> s3Service.uploadByType(file, "image", "username")
        );

        assertEquals(FILE_UPLOAD_FAIL, ex.getErrorCode());
    }
}