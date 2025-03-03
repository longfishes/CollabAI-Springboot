package com.longfish.collabai.controller;


import com.longfish.collabai.pojo.Result;
import com.longfish.collabai.pojo.dto.MeetingDTO;
import com.longfish.collabai.pojo.dto.MeetingEditDTO;
import com.longfish.collabai.pojo.dto.ParticipantsEditDTO;
import com.longfish.collabai.pojo.vo.MeetingAbsVO;
import com.longfish.collabai.pojo.vo.MeetingUserVO;
import com.longfish.collabai.pojo.vo.MeetingVO;
import com.longfish.collabai.service.IMeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "编辑会议 基本信息（不包括编辑参与者）")
    @PutMapping("/edit")
    public Result<?> edit(@RequestBody MeetingEditDTO meetingEditDTO) {
        meetingService.edit(meetingEditDTO);
        return Result.success();
    }

    @Operation(summary = "与我相关的会议")
    @GetMapping("/list")
    public Result<List<MeetingAbsVO>> list() {
        return Result.success(meetingService.listMeetings());
    }

    @Operation(summary = "会议详情")
    @GetMapping("/{id}")
    public Result<MeetingVO> detail(@PathVariable String id) {
        return Result.success(meetingService.detail(id));
    }

    @Operation(summary = "会议成员")
    @GetMapping("/participants/{id}")
    public Result<List<MeetingUserVO>> participants(@PathVariable String id) {
        return Result.success(meetingService.participants(id));
    }

    @Operation(summary = "编辑会议成员（拉人，删人，转让，授权）")
    @PutMapping("/participants/{id}")
    public Result<?> editMember(
            @PathVariable String id,
            @RequestBody List<ParticipantsEditDTO> participantsEditDTOList) {
        meetingService.editMember(id, participantsEditDTOList);
        return Result.success();
    }

    @Operation(summary = "加入会议（通过他人的邀请链接）")
    @PostMapping("/join/{id}")
    public Result<?> join(@PathVariable String id) {
        meetingService.join(id);
        return Result.success();
    }
}
