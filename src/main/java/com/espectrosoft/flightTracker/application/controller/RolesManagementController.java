package com.espectrosoft.flightTracker.application.controller;

import com.espectrosoft.flightTracker.application.dto.roles.CreateRoleRequestDto;
import com.espectrosoft.flightTracker.application.dto.roles.RoleDto;
import com.espectrosoft.flightTracker.application.dto.roles.RolePermissionDto;
import com.espectrosoft.flightTracker.application.dto.roles.UpdateRolePermissionsRequestDto;
import com.espectrosoft.flightTracker.application.dto.roles.UpdateRoleRequestDto;
import com.espectrosoft.flightTracker.application.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/management/roles")
@RequiredArgsConstructor
public class RolesManagementController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoleDto> create(@Valid @RequestBody CreateRoleRequestDto request) {
        return ResponseEntity.ok(roleService.create(request));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RoleDto>> list() {
        return ResponseEntity.ok(roleService.list());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoleDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.get(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoleDto> update(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequestDto request) {
        return ResponseEntity.ok(roleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RolePermissionDto>> getPermissions(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getPermissions(id));
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RolePermissionDto>> updatePermissions(@PathVariable Long id,
                                                                     @Valid @RequestBody UpdateRolePermissionsRequestDto request) {
        return ResponseEntity.ok(roleService.updatePermissions(id, request));
    }
}
