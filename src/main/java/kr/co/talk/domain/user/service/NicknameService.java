package kr.co.talk.domain.user.service;

import kr.co.talk.domain.user.dto.NicknameMapQueryDto;
import kr.co.talk.domain.user.dto.NicknameResponseDto;
import kr.co.talk.domain.user.repository.NicknameMapRepository;
import kr.co.talk.global.exception.CustomError;
import kr.co.talk.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NicknameService {
    private final NicknameMapRepository nicknameMapRepository;

    public NicknameResponseDto getNickname(List<Integer> nameCode) {
        var nicknameMap = nicknameMapRepository.getNicknameMap(nameCode);
        return NicknameResponseDto.builder()
                .nickname(nicknameMap.stream().map(NicknameMapQueryDto::getValue).reduce((s, s2) -> String.format("%s %s", s, s2))
                        .orElseThrow(() -> new CustomException(CustomError.NAMECODE_NOT_FOUND)))
                .profileUrl(
                        generateProfileUrl(
                                nicknameMap.stream().map(NicknameMapQueryDto::getCode).reduce((s, s2) -> String.format("%s-%s", s, s2))
                                        .orElseThrow(() -> new CustomException(CustomError.NAMECODE_NOT_FOUND))))
                .build();
    }

    private String generateProfileUrl(String nicknameCode) {
        return String.format("https://134-front.s3.ap-northeast-2.amazonaws.com/profile/%s.png", nicknameCode);
    }
}
