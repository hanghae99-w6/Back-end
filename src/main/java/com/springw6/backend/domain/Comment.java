package com.springw6.backend.domain;

import com.springw6.backend.controller.request.CommentRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "comment")
public class Comment extends Timestamped {

  @Id
  @Column(name = "commentid")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "member_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @JoinColumn(name = "post_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Post post;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SubComment> subComments;

  @Column(nullable = false)
  private String comment;

  @Column
  private Long likes;


  public void update(CommentRequestDto commentRequestDto) {
    this.comment = commentRequestDto.getComment();
  }
  public void updateLikes(Long likes) {
    this.likes = likes;
  }

}
