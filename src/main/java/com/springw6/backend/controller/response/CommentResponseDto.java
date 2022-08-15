package com.springw6.backend.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
  private Long id;
  private Long postId;
  private String author;
  private String comment;
  private Long likes;
  private List<SubCommentResponseDto> subComment;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
