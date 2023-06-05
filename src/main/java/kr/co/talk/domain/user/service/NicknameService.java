package kr.co.talk.domain.user.service;

import kr.co.talk.domain.user.repository.NicknameMapRepository;
import kr.co.talk.domain.user.repository.UserRepository;
import kr.co.talk.global.exception.CustomError;
import kr.co.talk.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NicknameService {
    private final NicknameMapRepository nicknameMapRepository;
    private final UserRepository userRepository;

    public String getNickname(List<Integer> nameCode) {
        return nicknameMapRepository.getNickname(nameCode).stream().reduce((s, s2) -> String.format("%s %s", s, s2))
                .orElseThrow(() -> new CustomException(CustomError.NAMECODE_NOT_FOUND));
    }
}
