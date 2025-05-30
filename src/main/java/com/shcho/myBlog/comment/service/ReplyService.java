package com.shcho.myBlog.comment.service;

import com.shcho.myBlog.comment.dto.ReplyCreateRequestDto;
import com.shcho.myBlog.comment.dto.ReplyDeleteRequestDto;
import com.shcho.myBlog.comment.dto.ReplyUpdateRequestDto;
import com.shcho.myBlog.comment.entity.Comment;
import com.shcho.myBlog.comment.entity.Reply;
import com.shcho.myBlog.comment.repository.CommentRepository;
import com.shcho.myBlog.comment.repository.ReplyRepository;
import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.libs.exception.ErrorCode;
import com.shcho.myBlog.user.entity.Role;
import com.shcho.myBlog.user.entity.User;
import com.shcho.myBlog.user.repository.UserRepository;
import com.shcho.myBlog.user.service.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Reply createReply(CustomUserDetails userDetails, Long commentId, ReplyCreateRequestDto request) {
        Comment comment = commentRepository.getReferenceById(commentId);

        if (userDetails != null) {
            User user = userRepository.getReferenceById(userDetails.getUserId());
            return replyRepository.save(
                    Reply.of(request.content(), user, comment)
            );
        } else {
            String rawPassword = request.anonymousPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);

            User guest = User.builder()
                    .username("guest-" + UUID.randomUUID())
                    .password(encodedPassword)
                    .nickname(request.anonymousName())
                    .role(Role.GUEST)
                    .build();
            userRepository.save(guest);

            return replyRepository.save(
                    Reply.of(request.content(), guest, comment)
            );
        }
    }

    @Transactional
    public Reply updateReply(Long replyId, CustomUserDetails userDetails, ReplyUpdateRequestDto request) {
        Reply reply = getReplyById(replyId);

        checkWriter(reply, userDetails, request.anonymousPassword());

        reply.update(request.content());
        return reply;
    }

    @Transactional
    public void deleteReply(Long replyId, CustomUserDetails userDetails, ReplyDeleteRequestDto request) {
        Reply reply = getReplyById(replyId);

        checkWriter(reply, userDetails, request.anonymousPassword());

        reply.delete();
    }

    private Reply getReplyById(Long replyId) {
        Reply reply = replyRepository.getReferenceById(replyId);

        if (reply.isDeleted()) {
            throw new CustomException(ErrorCode.ALREADY_DELETED_REPLY);
        }

        return reply;
    }

    private void checkWriter(Reply reply, CustomUserDetails userDetails, String anonymousPassword) {
        if (userDetails != null) {
            User user = userRepository.getReferenceById(userDetails.getUserId());
            if (!user.getId().equals(reply.getUser().getId())) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_REPLY_ACCESS);
            }
        } else {
            if (anonymousPassword == null || !passwordEncoder.matches(anonymousPassword, reply.getUser().getPassword())) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_REPLY_ACCESS);
            }
        }
    }
}
