package com.longfish.collabai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.longfish.collabai.constant.FriendConstant;
import com.longfish.collabai.context.BaseContext;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.pojo.entity.User;
import com.longfish.collabai.pojo.entity.UserFriend;
import com.longfish.collabai.mapper.UserFriendMapper;
import com.longfish.collabai.pojo.vo.UserFriendVO;
import com.longfish.collabai.service.IUserFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longfish.collabai.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author longfish
 * @since 2025-03-10
 */
@Service
public class UserFriendServiceImpl extends ServiceImpl<UserFriendMapper, UserFriend>
        implements IUserFriendService {

    @Autowired
    private IUserService userService;

    @Override
    public List<UserFriendVO> requestList() {
        Long currentId = BaseContext.getCurrentId();
        List<UserFriend> userFriends = lambdaQuery().eq(UserFriend::getUserId, currentId)
                .eq(UserFriend::getType, FriendConstant.REQUEST)
                .list();

        List<Long> idList = userFriends.stream().map(UserFriend::getFriendId).toList();
        List<User> users = userService.listByIds(idList);
        return users.stream().map(user -> BeanUtil.copyProperties(user, UserFriendVO.class)).toList();
    }

    @Override
    public List<UserFriendVO> receiveList() {
        Long currentId = BaseContext.getCurrentId();
        List<UserFriend> userFriends = lambdaQuery().eq(UserFriend::getFriendId, currentId)
                .eq(UserFriend::getType, FriendConstant.REQUEST)
                .list();

        List<Long> idList = userFriends.stream().map(UserFriend::getUserId).toList();
        List<User> users = userService.listByIds(idList);
        return users.stream().map(user -> BeanUtil.copyProperties(user, UserFriendVO.class)).toList();
    }

    @Override
    public List<UserFriendVO> friendList() {
        Long currentId = BaseContext.getCurrentId();
        List<UserFriend> userFriends = lambdaQuery().eq(UserFriend::getUserId, currentId)
                .eq(UserFriend::getType, FriendConstant.FRIEND)
                .list();

        List<Long> idList = userFriends.stream().map(UserFriend::getFriendId).toList();
        List<User> users = userService.listByIds(idList);
        return users.stream().map(user -> BeanUtil.copyProperties(user, UserFriendVO.class)).toList();
    }

    @Override
    public void invite(Long userId) {
        // 检测是否已发送或已是好友
        throw new BizException("还没开发");
    }

    @Override
    public void accept(Long userId) {
        // type 改 2
        // 好友反向记录也添加
        throw new BizException("还没开发");
    }
}
