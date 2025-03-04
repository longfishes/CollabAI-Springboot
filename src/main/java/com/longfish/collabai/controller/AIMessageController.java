package com.longfish.collabai.controller;

import com.longfish.collabai.pojo.Result;
import com.longfish.collabai.service.AIMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/ai")
@Tag(name = "AI消息相关")
public class AIMessageController {

    @Autowired
    private AIMessageService aiMessageService;

    @Operation(summary = "总结会议")
    @PostMapping("/mt/summarize")
    public Result<?> summarize() {
        return Result.success();
    }
}
