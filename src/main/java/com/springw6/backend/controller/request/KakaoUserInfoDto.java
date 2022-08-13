package com.springw6.backend.controller.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserInfoDto {

    private Long id;
    private String nickname;
    private String loginId;

}
