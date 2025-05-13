package com.shcho.myBlog.libs.exception;

import com.shcho.myBlog.libs.dto.ExceptionResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 처리 - ErrorCode에 정의된 예외 반환
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponseDto> handleCustomException(CustomException e) {
        log.error("CustomException 발생: {}", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(ExceptionResponseDto.of(
                                e.getErrorCode().getHttpStatus(),
                                e.getErrorCode().getMessage()
                        )
                );
    }

    /**
     * RuntimeException 처리 - 서버 내부 오류 반환
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponseDto> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException 발생: ", e);
        return ResponseEntity.internalServerError()
                .body(ExceptionResponseDto.of(
                                ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus(),
                                ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                        )
                );
    }
}
