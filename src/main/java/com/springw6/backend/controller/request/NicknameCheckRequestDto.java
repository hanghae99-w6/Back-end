package com.springw6.backend.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NicknameCheckRequestDto {

    @NotBlank
    private String nickname;

}
