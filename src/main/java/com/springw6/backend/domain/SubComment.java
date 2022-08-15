package com.springw6.backend.domain;

import com.springw6.backend.controller.request.SubCommentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "SubComment")
public class SubComment extends Timestamped {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @JoinColumn(name = "member_id", nullable = false)
   @ManyToOne(fetch = FetchType.LAZY)
   private Member member;

   @JoinColumn(name = "post_id", nullable = false)
   @ManyToOne(fetch = FetchType.LAZY)
   private Post post;

   @JoinColumn(name = "commentid", nullable = false)
   @ManyToOne(fetch = FetchType.LAZY)
   private Comment comment;

   @Column(nullable = false)
   private Long commentId;

   @Column(nullable = false)
   private String subComment;

   @Column
   private Long likes;

//  @OneToMany(mappedBy = "subComment", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//  private List<SubCommentLike> subCommentLikeList;

   public void update(SubCommentRequestDto requestDto) {
      this.subComment = requestDto.getSubComment();
   }

   public boolean validateMember(Member member) {
      return !this.member.equals(member);
   }

   public void updateLikes(Long likes) {
      this.likes = likes;
   }
}
