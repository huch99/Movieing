package com.movieing.movieingbackend.theater.entity;

public enum TheaterStatus {
    DRAFT,      // 작성 중
    ACTIVE,     // 운영중 (노출 + 예매 가능)
    HIDDEN,     // 숨김 (노출 X)
    CLOSED,     // 운영 종료 (정책에 따라 노출/예매 불가)
    DELETED     // 소프트 삭제
}
