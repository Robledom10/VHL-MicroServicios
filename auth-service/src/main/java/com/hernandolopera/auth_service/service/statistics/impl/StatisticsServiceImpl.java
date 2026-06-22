package com.hernandolopera.auth_service.service.statistics.impl;

import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.dto.response.statistics.UserStatisticsResponse;
import com.hernandolopera.auth_service.repository.auth.UserRepository;
import com.hernandolopera.auth_service.service.statistics.StatisticsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl
        implements StatisticsService {

    private final UserRepository userRepository;

    @Override
    public UserStatisticsResponse getUserStatistics() {

        return new UserStatisticsResponse(

                userRepository.count(),

                userRepository.countByActiveTrue(),

                userRepository.countByActiveFalse(),

                userRepository.countByEmailVerifiedTrue(),

                userRepository.countByPhoneVerifiedTrue(),

                userRepository.countByProfileCompletedTrue(),

                userRepository.countByAccountNonLockedFalse(),

                userRepository.countByRole_Name("ADMIN"),

                userRepository.countByRole_Name("CLIENT"),

                userRepository.countByRole_Name("GUIDE"));
    }
}