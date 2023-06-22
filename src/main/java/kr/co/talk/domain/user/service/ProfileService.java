package kr.co.talk.domain.user.service;

import kr.co.talk.domain.user.dto.UserProfileDto;
import kr.co.talk.domain.user.model.User;
import kr.co.talk.domain.user.repository.ProfileRepository;
import kr.co.talk.domain.user.repository.UserRepository;
import kr.co.talk.global.exception.CustomError;
import kr.co.talk.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public UserProfileDto getProfile(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomError.USER_DOES_NOT_EXIST));

        return UserProfileDto.builder()
                .name(user.getUserName())
                .nickname(user.getNickname())
                .profileUrl(NicknameService.generateProfileUrl(user.getProfileImgCode()))
                .nameCode(profileRepository.getNameCodeList(user.getProfileImgCode()))
                .build();
    }
}
