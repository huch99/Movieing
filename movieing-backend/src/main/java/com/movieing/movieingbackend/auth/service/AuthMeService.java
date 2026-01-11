package com.movieing.movieingbackend.auth.service;

import com.movieing.movieingbackend.auth.dto.MeResponseDto;
import com.movieing.movieingbackend.user.entity.User;
import com.movieing.movieingbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthMeService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MeResponseDto getMe(String subject){
        User user = userRepository.findByPublicUserId(subject)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return MeResponseDto.builder()
                .publicUserId(user.getPublicUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
