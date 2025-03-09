package com.longfish.collabai.task;

import com.longfish.collabai.service.IMeetingService;
import com.longfish.collabai.service.IUserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.longfish.collabai.constant.CommonConstant.DEFAULT_AVATAR;
import static com.longfish.collabai.constant.CommonConstant.DEFAULT_COVER;

@Component
public class AppStart {

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private IUserService userService;

    @PostConstruct
    public void init() {
        meetingService.setDefaultCover(DEFAULT_COVER);
        userService.setDefaultAvatar(DEFAULT_AVATAR);
    }

}
