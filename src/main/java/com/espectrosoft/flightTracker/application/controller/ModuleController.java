package com.espectrosoft.flightTracker.application.controller;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleToggleRequestDto;
import com.espectrosoft.flightTracker.application.service.ModuleService;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleService
        moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService =
            moduleService;
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuleStatusDto> toggle(@Valid @RequestBody ModuleToggleRequestDto request) {
        return ResponseEntity.ok(moduleService.toggle(request));
    }

    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuleStatusDto> status(@RequestParam Long academyId, @RequestParam ModuleCode moduleCode) {
        return ResponseEntity.ok(moduleService.status(academyId, moduleCode));
    }
}
