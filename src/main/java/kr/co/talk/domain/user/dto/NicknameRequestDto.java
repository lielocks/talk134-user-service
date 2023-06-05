package kr.co.talk.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class NicknameRequestDto {
    private List<Integer> nameCode;
    long userId;
}
