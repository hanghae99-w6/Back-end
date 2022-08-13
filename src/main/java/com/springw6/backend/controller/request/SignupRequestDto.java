package com.springw6.backend.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequestDto {

    @NotBlank
    private String loginId;

    @NotBlank
    @Size(min = 4, max = 32)
    @Pattern(regexp = "[a-z\\d]*${3,32}")
    private String password;

    @NotBlank
    @Size(min = 4, max = 12)
    @Pattern(regexp = "[a-zA-Z\\d]*${3,12}")
    private String nickname;


}
