package com.movieing.movieingbackend.schedule.repository;

import com.movieing.movieingbackend.schedule.entity.Schedule;
import com.movieing.movieingbackend.schedule.entity.ScheduleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Page<Schedule> findByStatusNot(ScheduleStatus status, Pageable pageable);

}
