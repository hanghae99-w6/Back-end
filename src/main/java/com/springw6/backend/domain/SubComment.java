package com.springw6.backend.domain;

import com.springw6.backend.controller.request.SubCommentRequestDto;
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
public class SubComment extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "member_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  private Long commentId;

  @Column(nullable = false)
  private String comment;

//  @OneToMany(mappedBy = "subComment", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//  private List<SubCommentLike> subCommentLikeList;

  public void update(SubCommentRequestDto requestDto) {
    this.comment = requestDto.getComment();
  }

  public boolean validateMember(Member member) {
    return !this.member.equals(member);
  }
}
