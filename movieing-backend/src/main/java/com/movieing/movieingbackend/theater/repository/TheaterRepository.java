package com.movieing.movieingbackend.theater.repository;

import com.movieing.movieingbackend.theater.entity.Theater;
import com.movieing.movieingbackend.theater.entity.TheaterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {

    List<Theater> findAllByStatusNot(TheaterStatus status);
}
