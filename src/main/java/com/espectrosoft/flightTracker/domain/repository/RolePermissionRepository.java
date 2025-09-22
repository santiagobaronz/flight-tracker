package com.espectrosoft.flightTracker.domain.repository;

import com.espectrosoft.flightTracker.domain.model.RolePermission;
import com.espectrosoft.flightTracker.domain.model.Role;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.PermissionAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    boolean existsByRoleInAndModuleCodeAndAction(Set<Role> roles, ModuleCode moduleCode, PermissionAction action);

    @Query("select case when count(rp)>0 then true else false end " +
           "from RolePermission rp join rp.role r join r.users u " +
           "where u.id = :userId and rp.moduleCode = :moduleCode and rp.action = :action")
    boolean existsByUserIdAndModuleCodeAndAction(@Param("userId") Long userId,
                                                 @Param("moduleCode") ModuleCode moduleCode,
                                                 @Param("action") PermissionAction action);

    boolean existsByRoleAndModuleCodeAndAction(Role role, ModuleCode moduleCode, PermissionAction action);
}
