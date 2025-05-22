package com.shcho.myBlog.common.controller;

import com.shcho.myBlog.common.dto.FileUploadResponseDto;
import com.shcho.myBlog.common.service.S3Service;
import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.user.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDto> uploadFile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart MultipartFile file,
            @RequestParam("type") String type
    ) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(s3Service.uploadByType(file, type, username));
    }

}
