package com.longfish.collabai.controller;

import com.longfish.collabai.context.UploadStrategyContext;
import com.longfish.collabai.enums.FilePathEnum;
import com.longfish.collabai.pojo.Result;
import com.longfish.collabai.pojo.vo.UrlVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "通用接口")
@RestController
public class CommonController {

    @Autowired
    private UploadStrategyContext uploadStrategyContext;

    @Operation(summary = "上传会议封面图片")
    @PostMapping("/mt/img/upload")
    public Result<UrlVO> uploadMeetingImg(MultipartFile file) {
        return Result.success(UrlVO.builder()
                .url(uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.MEETING.getPath()))
                .build());
    }

    @Operation(summary = "上传头像图片")
    @PostMapping("/user/avatar/upload")
    public Result<UrlVO> uploadAvatar(MultipartFile file) {
        return Result.success(UrlVO.builder()
                .url(uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.AVATAR.getPath()))
                .build());
    }
}
