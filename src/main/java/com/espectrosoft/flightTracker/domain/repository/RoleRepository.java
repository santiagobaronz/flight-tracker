package com.espectrosoft.flightTracker.domain.repository;

import com.espectrosoft.flightTracker.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    @Query("select count(u) from User u join u.roles r where r.id = :roleId")
    long countUsersByRoleId(@Param("roleId") Long roleId);
}
