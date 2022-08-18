package com.springw6.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springw6.backend.controller.request.PostRequestDto;
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
public class Post extends Timestamped {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 데이터베이스에 위임
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @Column()
  private String imgUrl;

  @Column(nullable = false)
  private String star;

  @Column(nullable = false)
  private String category;

  @Column
  private Long likes;

  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true) // 단방향
  // cascade = CascadeType.ALL , 현 Entity의 변경에 대해 관계를 맺은 Entity도 변경 전략을 결정합니다.
  // fetch = FetchType.LAZY, 관계된 Entity의 정보를 LAZY는 실제로 요청하는 순간 가져오는겁니다.
  // orphanRemoval = true, 관계 Entity에서 변경이 일어난 경우 DB 변경을 같이 할지 결정합니다.
  private List<Comment> comments;


  @JoinColumn(name = "member_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  // mappedBy, 양방향 관계 설정시 관계의 주체가 되는 쪽에서 정의합니다.
  private List<Likes> postLikeList;

  public void update(PostRequestDto postRequestDto) {
    this.title = postRequestDto.getTitle();
    this.content = postRequestDto.getContent();
    this.imgUrl = postRequestDto.getImgUrl();
    this.star = postRequestDto.getStar();
    this.category = postRequestDto.getCategory();
  }


  public void updateLikes(Long likes) {
    this.likes = likes;
  }

}
