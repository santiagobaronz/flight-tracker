package com.espectrosoft.flightTracker.application.controller;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleToggleRequestDto;
import com.espectrosoft.flightTracker.application.usecase.ModuleUseCase;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleUseCase moduleUseCase;

    public ModuleController(ModuleUseCase moduleUseCase) {
        this.moduleUseCase = moduleUseCase;
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuleStatusDto> toggle(@Valid @RequestBody ModuleToggleRequestDto request) {
        return ResponseEntity.ok(moduleUseCase.toggle(request));
    }

    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuleStatusDto> status(@RequestParam Long academyId, @RequestParam ModuleCode moduleCode) {
        return ResponseEntity.ok(moduleUseCase.status(academyId, moduleCode));
    }
}
