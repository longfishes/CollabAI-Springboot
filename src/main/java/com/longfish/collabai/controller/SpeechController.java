package com.longfish.collabai.controller;

import com.longfish.collabai.annotation.NoLogin;
import com.longfish.collabai.pojo.Result;
import com.longfish.collabai.pojo.vo.SpeechVO;
import com.longfish.collabai.service.SpeechService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/speech")
@Tag(name = "语音识别")
public class SpeechController {

    @Autowired
    private SpeechService speechService;

    @Operation(summary = "同步识别结果")
    @GetMapping("/sync/{meetingId}")
    public Result<SpeechVO> syncSpeechText(@PathVariable String meetingId) {
        return Result.success(
            SpeechVO.builder()
                .speechText(speechService.syncSpeechText(meetingId))
                .build()
        );
    }

    @Operation(summary = "上传识别音频")
    @PostMapping("/recognize/{meetingId}")
    @NoLogin
    public Result<?> recognizeSpeech(
            @RequestBody byte[] audioData,
            @PathVariable String meetingId) {
        speechService.recognizeSpeech(audioData, meetingId);
        return Result.success();
    }
}
