package com.movieing.movieingbackend.screen.repository;

import com.movieing.movieingbackend.screen.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
}
