package com.longfish.collabai.controller;

import com.longfish.collabai.pojo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/ai")
@Tag(name = "AI消息相关")
public class AIMessageController {

    @Operation(summary = "test")
    @GetMapping("/test")
    public Result<?> test() {
        return Result.success();
    }
}
