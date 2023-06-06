package kr.co.talk.domain.user.service;

import kr.co.talk.domain.user.dto.NicknameMapQueryDto;
import kr.co.talk.domain.user.dto.NicknameResponseDto;
import kr.co.talk.domain.user.model.User;
import kr.co.talk.domain.user.repository.NicknameMapRepository;
import kr.co.talk.domain.user.repository.UserRepository;
import kr.co.talk.global.exception.CustomError;
import kr.co.talk.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NicknameService {
    private final UserRepository userRepository;
    private final NicknameMapRepository nicknameMapRepository;

    @Transactional(rollbackFor = Exception.class)
    public NicknameResponseDto getNicknameAndSave(Long userId, List<Integer> nameCode) {
        var nicknameMap = nicknameMapRepository.getNicknameMap(nameCode);

        String nickname = generateNickname(nicknameMap);
        String profileImgCode = generateProfileImgCode(nicknameMap);

        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
        }

        user.setNickname(nickname);
        user.setProfileImgCode(profileImgCode);

        return NicknameResponseDto.builder()
                .nickname(nickname)
                .profileUrl(generateProfileUrl(profileImgCode))
                .build();
    }

    private String generateNickname(List<NicknameMapQueryDto> nicknameMap) {
        return nicknameMap.stream().map(NicknameMapQueryDto::getValue).reduce((s, s2) -> String.format("%s %s", s, s2))
                .orElseThrow(() -> new CustomException(CustomError.NAMECODE_NOT_FOUND));
    }

    private String generateProfileImgCode(List<NicknameMapQueryDto> nicknameMap) {
        return nicknameMap.stream().map(NicknameMapQueryDto::getCode).reduce((s, s2) -> String.format("%s-%s", s, s2))
                .orElseThrow(() -> new CustomException(CustomError.NAMECODE_NOT_FOUND));
    }

    private String generateProfileUrl(String nicknameCode) {
        return String.format("https://134-back.s3.ap-northeast-2.amazonaws.com/profile/%s.png", nicknameCode);
    }
}
