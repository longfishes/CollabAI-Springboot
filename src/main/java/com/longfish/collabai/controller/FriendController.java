package com.longfish.collabai.controller;

import com.longfish.collabai.pojo.Result;
import com.longfish.collabai.pojo.vo.UserFriendVO;
import com.longfish.collabai.service.IUserFriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/friend")
@Tag(name = "好友相关")
public class FriendController {

    @Autowired
    private IUserFriendService userFriendService;

    @Operation(summary = "发起的好友申请")
    @RequestMapping("/reqs")
    public Result<List<UserFriendVO>> requestList() {
        return Result.success(userFriendService.requestList());
    }

    @Operation(summary = "收到的好友申请")
    @RequestMapping("/rcvs")
    public Result<List<UserFriendVO>> receiveList() {
        return Result.success(userFriendService.receiveList());
    }

    @Operation(summary = "好友列表")
    @RequestMapping("/list")
    public Result<List<UserFriendVO>> friendList() {
        return Result.success(userFriendService.friendList());
    }

    @Operation(summary = "发起好友申请")
    @RequestMapping("/invite/{userId}")
    public Result<?> invite(@PathVariable Long userId) {
        userFriendService.invite(userId);
        return Result.success();
    }

    @Operation(summary = "通过好友申请")
    @RequestMapping("/accept/{userId}")
    public Result<?> accept(@PathVariable Long userId) {
        userFriendService.accept(userId);
        return Result.success();
    }

}
