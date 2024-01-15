package kr.co.talk.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.talk.domain.user.controller.UserController;
import kr.co.talk.domain.user.dto.RegisterAdminUserDto;
import kr.co.talk.domain.user.dto.ResponseDto;
import kr.co.talk.domain.user.model.User;
import kr.co.talk.domain.user.service.NicknameService;
import kr.co.talk.domain.user.service.ProfileService;
import kr.co.talk.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private NicknameService nicknameService;

    @MockBean
    private ProfileService profileService;

    @Test
    @DisplayName("admin 회원 register 및 header 에 userId 포함하여 반환")
    @WithMockUser
    void admin_user_register() throws Exception {
        //given
        User user = User.builder().userId(1L).userUid("test").userName("testName").nickname("nickname").role(User.Role.ROLE_ADMIN).build();

        RegisterAdminUserDto adminUserDto = RegisterAdminUserDto.builder().teamName("talk").name(user.getUserName()).build();

        ResponseDto.TeamCodeResponseDto teamCodeResponseDto = new ResponseDto.TeamCodeResponseDto();
        teamCodeResponseDto.setTeamCode("talk");

        //when
        given(userService.registerAdminUser(adminUserDto, 1L))
                .willReturn(teamCodeResponseDto);

        //then
        String content = objectMapper.writeValueAsString(adminUserDto);
        mockMvc.perform(post("/user/admin/register")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                        .header("userId", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamCode").value("talk"));
    }

}
