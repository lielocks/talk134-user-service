package kr.co.talk.user.service;

import kr.co.talk.domain.user.dto.NicknameResponseDto;
import kr.co.talk.domain.user.dto.RegisterAdminUserDto;
import kr.co.talk.domain.user.dto.RegisterUserDto;
import kr.co.talk.domain.user.model.NicknameMap;
import kr.co.talk.domain.user.model.Team;
import kr.co.talk.domain.user.model.User;
import kr.co.talk.domain.user.repository.CustomNicknameMapRepository;
import kr.co.talk.domain.user.repository.NicknameMapRepository;
import kr.co.talk.domain.user.repository.TeamRepository;
import kr.co.talk.domain.user.repository.UserRepository;
import kr.co.talk.domain.user.service.NicknameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
public class NicknameServiceTest {

    @Autowired
    NicknameService nicknameService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    CustomNicknameMapRepository customNicknameMapRepository;

    @Autowired
    NicknameMapRepository nicknameMapRepository;

    @DisplayName("name code 의 길이는 3 -> {0,0,0} 이고 따라서 step 도 총 3개이다. 각각의 step 에 따라 sequence 는 5개씩 배치되어 있다. 따라서 총 125 가지 경우의 수가 있다.")
    @Transactional
    @BeforeEach
    void createMockNicknameMapData() {
        RegisterAdminUserDto mockAdminUserDto = RegisterAdminUserDto.builder().name("김이름").teamName("teamName").build();
        Team team = Team.builder().teamName(mockAdminUserDto.getTeamName()).teamCode("YnNme3").build();
        teamRepository.save(team);

        RegisterUserDto mockUserDto = RegisterUserDto.builder().teamCode(teamRepository.findTeamByTeamName("teamName").get().getTeamCode()).name("김유저").build();
        userRepository.saveAll(List.of(User.builder().userName(mockAdminUserDto.getName()).team(team).role(User.Role.ROLE_ADMIN).build(),
                User.builder().userName(mockUserDto.getName()).team(team).role(User.Role.ROLE_USER).build()));

        nicknameMapRepository.saveAll(
                List.of(NicknameMap.builder().step(1).sequence(1).code("co").value("차가운").build(),
                        NicknameMap.builder().step(2).sequence(1).code("a").value("매의").build(),
                        NicknameMap.builder().step(3).sequence(1).code("sp").value("낮잠").build(),

                        NicknameMap.builder().step(1).sequence(5).code("fl").value("떠오르는").build(),
                        NicknameMap.builder().step(2).sequence(5).code("d").value("바람의").build(),
                        NicknameMap.builder().step(3).sequence(5).code("bl").value("일격").build())
        );
    }

    @DisplayName("name code 의 첫번째 자릿수는 step 1의 sequence 를 나타낸다. name code 의 두번째 자릿수는 step 2의 sequence 를 나타낸다. name code 의 세번째 자릿수는 step 3의 sequence 를 나타낸다.")
    @Test
    void generateNicknameAndProfileUrl() {
        // given
        List<Integer> nameCode1 = List.of(1,1,1);
        NicknameResponseDto nicknameResponseDto1 =
                nicknameService.getNicknameAndSave(userRepository.findUserByUserNameOrNickname("김이름").get(0).getUserId(), nameCode1);

        List<Integer> nameCode2 = List.of(5,5,5);
        NicknameResponseDto nicknameResponseDto2 =
                nicknameService.getNicknameAndSave(userRepository.findUserByUserNameOrNickname("김유저").get(0).getUserId(), nameCode2);

        // when
        String nickname1 = nicknameResponseDto1.getNickname();
        String profileUrl1 = nicknameResponseDto1.getProfileUrl();

        String nickname2 = nicknameResponseDto2.getNickname();
        String profileUrl2 = nicknameResponseDto2.getProfileUrl();

        // then
        assertEquals(nickname1, "차가운 매의 낮잠");
        assertEquals(profileUrl1, "https://134-back.s3.ap-northeast-2.amazonaws.com/profile/co-a-sp.png");

        assertEquals(nickname2, "떠오르는 바람의 일격");
        assertEquals(profileUrl2, "https://134-back.s3.ap-northeast-2.amazonaws.com/profile/fl-d-bl.png");
    }
}
