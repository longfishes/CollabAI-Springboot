package com.longfish.collabai;

import com.longfish.collabai.constant.MeetingConstant;
import com.longfish.collabai.pojo.dto.ParticipantsEditDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LocalTest {

    @Test
    public void testFormat() {
        LocalDateTime time = LocalDateTime.parse("2000-11-11 10:00" , DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println(time);
    }

    @Test
    public void testMultiHolder() {
        List<ParticipantsEditDTO> editDTOList = new ArrayList<>();
        editDTOList.add(ParticipantsEditDTO.builder().authType(MeetingConstant.HOLDER).build());
        editDTOList.add(ParticipantsEditDTO.builder().authType(MeetingConstant.HOLDER).build());
        final boolean[] flag = {false, false};
        editDTOList.forEach(e -> {
            if (e.getAuthType().equals(MeetingConstant.HOLDER)) {
                if (flag[0]) flag[1] = true;
                flag[0] = true;
            }
        });
        if (flag[1]) System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }

}
