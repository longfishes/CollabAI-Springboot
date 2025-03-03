package com.longfish.collabai.controller;


import com.longfish.collabai.pojo.Result;
import com.longfish.collabai.pojo.dto.MeetingDTO;
import com.longfish.collabai.service.IMeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author longfish
 * @since 2025-03-03
 */
@RestController
@RequestMapping("/mt")
@Tag(name = "会议相关")
public class MeetingController {

    @Autowired
    private IMeetingService meetingService;

    @Operation(summary = "创建会议")
    @PostMapping("/create")
    public Result<?> create(@RequestBody MeetingDTO meetingDTO) {
        meetingService.createNew(meetingDTO);
        return Result.success();
    }
}
