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
public class PostResponseDto {
  private Long id;
  private String title;
  private String content;
  private String imgUrl;
  private Long likes;
  private String star;
  private String author;
  private String category;
  private List<CommentResponseDto> comments;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
