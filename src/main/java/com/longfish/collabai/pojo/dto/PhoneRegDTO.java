package com.longfish.collabai.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PhoneRegDTO {

    private String username;

    private String phone;

    private String code;

    private String password;
}
