package com.springw6.backend.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponseDto {
  private Long id;
  private String title;
  private String content;
  private String imgUrl;
  private String author;
  private Long likes;
  private int commentsNum;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
