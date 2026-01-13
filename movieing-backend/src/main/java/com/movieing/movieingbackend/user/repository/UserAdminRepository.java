package com.movieing.movieingbackend.user.repository;

import com.movieing.movieingbackend.user.entity.UserAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAdminRepository extends JpaRepository<UserAdmin, Long> {
}
