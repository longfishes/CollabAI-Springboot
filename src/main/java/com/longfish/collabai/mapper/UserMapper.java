package com.longfish.collabai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longfish.collabai.pojo.entity.User;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author longfish
 * @since 2024-05-16
 */
public interface UserMapper extends BaseMapper<User> {

    void setDefaultAvatar(String url);
}
