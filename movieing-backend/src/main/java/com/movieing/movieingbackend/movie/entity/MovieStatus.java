package com.movieing.movieingbackend.movie.entity;

public enum MovieStatus {
    DRAFT, // 작성 중(임시 저장)
    COMING_SOON, // 개봉 예정 (노출 + 예매 가능)
    NOW_SHOWING, // 상영 중 (노출 + 예매 가능)
    HIDDEN, // 운영자가 숨김 (노출 X)
    ENDED, // 상영 종료 (노출은 정책에 따라, 예매 X)
    DELETED     // 삭제(소프트 삭제)
}
