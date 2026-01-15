package com.movieing.movieingbackend.schedule.entity;

public enum ScheduleStatus {
    DRAFT,      // 작성 중
    OPEN,       // 오픈(판매 가능)
    CLOSED,     // 종료
    CANCELLED,  // 취소
    DELETED     // 삭제
}
