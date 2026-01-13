package com.movieing.movieingbackend.aspect;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 엔티티 공통 시간 관리 베이스 클래스
 * <p>
 * - 생성/수정 일시를 자동으로 관리하기 위한 추상 클래스
 * - JPA Auditing 기능을 사용하여 값이 자동 주입됨
 * - 실제 테이블로 생성되지 않고, 상속받은 엔티티의 컬럼으로 포함됨
 * <p>
 * 사용 조건:
 * - @EnableJpaAuditing 활성화 필요
 * - 엔티티에서 extends BaseTimeEntity 로 상속
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseTimeEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;    // 생성 일자

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;    // 수정 일자
}
