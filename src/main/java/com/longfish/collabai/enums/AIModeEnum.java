package com.longfish.collabai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AIModeEnum {

    DEEPSEEK("ds", "dsAIStrategyImpl"),

    HENG("heng", "hengAIStrategyImpl");

    private final String mode;

    private final String strategy;

    public static String getStrategy(String mode) {
        for (AIModeEnum value : AIModeEnum.values()) {
            if (value.getMode().equals(mode)) {
                return value.getStrategy();
            }
        }
        return null;
    }

}
