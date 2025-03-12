package com.longfish.collabai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longfish.collabai.pojo.entity.UserFriend;
import com.longfish.collabai.pojo.vo.UserFriendVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author longfish
 * @since 2025-03-10
 */
public interface IUserFriendService extends IService<UserFriend> {

    List<UserFriendVO> requestList();

    List<UserFriendVO> receiveList();

    List<UserFriendVO> friendList();

    void invite(Long userId);

    void accept(Long userId);

    void delFriend(Long userId);
}
