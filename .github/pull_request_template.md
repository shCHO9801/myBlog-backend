## 🔀 PR 제목
<!-- ex: [Feature] JWT 기반 로그인 기능 구현 -->

## ✅ 작업 내용
<!-- 어떤 작업을 했는지 요약해서 적어주세요 -->
- JWT 토큰 발급 로직 추가
- 로그인 실패 시 CustomException 반환
- 인증 성공 시 토큰 응답

## 🔧 변경사항
<!-- 어떤 파일이 추가/수정/삭제되었는지 -->
- `JwtUtil.java` 추가
- `AuthController.java` 추가
- `SecurityConfig.java` 수정

## 📌 관련 이슈
<!-- 관련된 이슈 번호를 연결해주세요 -->
- close #3

## 📎 참고 자료
<!-- 참고한 문서, 블로그, 링크 등 -->
- [JWT 공식 문서](https://jwt.io/introduction)
