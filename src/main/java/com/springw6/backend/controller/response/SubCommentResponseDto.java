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
public class SubCommentResponseDto {
  private Long id;
  private String author;
  private String comment;
  private int likes;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
