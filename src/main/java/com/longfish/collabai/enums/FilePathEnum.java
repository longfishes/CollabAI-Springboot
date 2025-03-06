package com.longfish.collabai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FilePathEnum {

    AVATAR("avatar/user/", "头像路径"),

    MEETING("meeting/img/", "会议封面路径");

    private final String path;

    private final String desc;

}
