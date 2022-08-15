package com.springw6.backend.domain;

import com.springw6.backend.controller.request.CommentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "member_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @JoinColumn(name = "post_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Post post;

  @Column(nullable = false)
  private String comment;

  @Column
  private Long likes;

//  @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//  private List<CommentLike> commentLikeList;

  public void update(CommentRequestDto commentRequestDto) {
    this.comment = commentRequestDto.getComment();
  }

  public boolean validateMember(Member member) {
    return !this.member.equals(member);
  }

  public void updateLikes(Long likes) {
    this.likes = likes;
  }

}
