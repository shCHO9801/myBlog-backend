package com.shcho.myBlog.libs.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 400 */
    USER_ALREADY_EXISTS(400, "이미 존재하는 사용자 입니다."),
    NICKNAME_ALREADY_EXISTS(400, "이미 사용 중인 닉네임 입니다."),
    DUPLICATE_NICKNAME(400, "중복된 닉네임 입니다."),
    ALREADY_DELETED_USER(400, "이미 탈퇴한 사용자입니다."),
    INVALID_IMAGE_FORMAT(400, "지원하지 않는 이미지 형식입니다."),
    INVALID_FILE_FORMAT(400, "지원하지 않는 파일 형식입니다."),
    INVALID_FILE_TYPE(400, "지원하지 않는 파일 타입입니다."),
    DUPLICATE_CATEGORY(400, "중복된 카테고리명 입니다."),

    /* 401 */
    INVALID_CREDENTIAL(401, "아이디 또는 비밀번호가 올바르지 않습니다."),
    JWT_KEY_ERROR(401, "JWT secret 키가 올바르지 않습니다."),
    INVALID_PASSWORD(401, "비밀번호가 일치하지 않습니다."),
    DELETED_USER_CANNOT_LOGIN(401, "탈퇴한 사용자는 로그인할 수 없습니다."),

    /* 403 */
    UNAUTHORIZED_CATEGORY_ACCESS(403, "해당 카테고리에 대한 접근 권한이 없습니다."),

    /* 404 NOT_FOUND */
    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다."),

    /* 500 INTERNAL_SERVER_ERROR */
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다."),
    FILE_UPLOAD_FAIL(500, "파일 업로드에 실패하였습니다.");

    private final Integer httpStatus;
    private final String message;
}
