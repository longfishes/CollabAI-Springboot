package com.longfish.collabai.controller;

import com.longfish.collabai.pojo.Result;
import com.longfish.collabai.pojo.dto.ChatDTO;
import com.longfish.collabai.pojo.vo.AIMessageVO;
import com.longfish.collabai.pojo.vo.ChatVO;
import com.longfish.collabai.service.AIMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("/ai")
@Tag(name = "AI消息相关")
public class AIMessageController {

    @Autowired
    private AIMessageService aiMessageService;

    @Operation(summary = "手动请求会议总结")
    @PostMapping("/mt/summarize/{meetingId}")
    public Result<?> summarize(@PathVariable String meetingId) {
        aiMessageService.summarizeMeeting(meetingId);
        return Result.success();
    }

    @Operation(summary = "获取ai总结内容")
    @GetMapping("/sync/{meetingId}")
    public Result<AIMessageVO> sync(@PathVariable String meetingId) {
        return Result.success(
                AIMessageVO.builder()
                        .aiSummary(aiMessageService.syncAiMessage(meetingId))
                        .build()
        );
    }

    @Operation(summary = "获取安恒ai插件token")
    @GetMapping("/getToken")
    public Result<String> getAIToken() {
        return Result.success(aiMessageService.getAIToken());
    }

    @Operation(summary = "大模型问答")
    @PostMapping("/chat")
    public Result<ChatVO> chat(@RequestBody ChatDTO chatDTO) {
        return Result.success(aiMessageService.chat(chatDTO));
    }

}
