package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleToggleRequestDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleInfoDto;
import com.espectrosoft.flightTracker.application.modules.management.modules.usecase.GetModuleStatusUseCase;
import com.espectrosoft.flightTracker.application.modules.management.modules.usecase.ToggleModuleUseCase;
import com.espectrosoft.flightTracker.application.modules.shared.modules.usecase.ListModulesUseCase;
import com.espectrosoft.flightTracker.application.modules.shared.modules.usecase.GetModuleInfoUseCase;
import com.espectrosoft.flightTracker.application.core.policy.access.InternalAccessPolicy;
import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModuleServiceImplTest {

    @Mock
    private ToggleModuleUseCase toggleModuleUseCase;
    @Mock
    private GetModuleStatusUseCase getModuleStatusUseCase;
    @Mock
    private ListModulesUseCase listModulesUseCase;
    @Mock
    private GetModuleInfoUseCase getModuleInfoUseCase;
    @Mock
    private InternalAccessPolicy internalAccessPolicy;
    @Mock
    private DomainLookup domainLookup;

    private ModuleServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ModuleServiceImpl(toggleModuleUseCase, getModuleStatusUseCase, listModulesUseCase, getModuleInfoUseCase, internalAccessPolicy, domainLookup);
    }

    @Test
    void toggle_delegates_to_usecase() {
        final ModuleToggleRequestDto req = new ModuleToggleRequestDto();
        req.setAcademyId(1L);
        req.setModuleCode(ModuleCode.HOURS);
        req.setActive(Boolean.TRUE);
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(2L).username("admin").academy(academy).fullName("Admin").password("p").build();
        final ModuleStatusDto expected = new ModuleStatusDto(1L, ModuleCode.HOURS, true);
        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(toggleModuleUseCase.apply(eq(req))).thenReturn(expected);

        final ModuleStatusDto resp = service.toggle(req);

        assertEquals(1L, resp.getAcademyId());
        assertEquals(ModuleCode.HOURS, resp.getModuleCode());
        assertEquals(true, resp.isActive());
        verify(internalAccessPolicy).validate(eq(academy), eq(current));
        verify(toggleModuleUseCase).apply(eq(req));
    }

    @Test
    void status_delegates_to_usecase() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(2L).username("admin").academy(academy).fullName("Admin").password("p").build();
        final ModuleStatusDto expected = new ModuleStatusDto(1L, ModuleCode.HOURS, false);
        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(getModuleStatusUseCase.apply(eq(1L), eq(ModuleCode.HOURS))).thenReturn(expected);

        final ModuleStatusDto resp = service.status(1L, ModuleCode.HOURS);

        assertEquals(1L, resp.getAcademyId());
        assertEquals(ModuleCode.HOURS, resp.getModuleCode());
        assertEquals(false, resp.isActive());
        verify(internalAccessPolicy).validate(eq(academy), eq(current));
        verify(getModuleStatusUseCase).apply(eq(1L), eq(ModuleCode.HOURS));
    }

    @Test
    void list_all_delegates_to_usecase() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(2L).username("u").academy(academy).fullName("User").password("p").build();
        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(listModulesUseCase.apply(eq(1L))).thenReturn(java.util.List.of(
                ModuleInfoDto.builder().academyId(1L).section(ModuleSection.APPLICATION).moduleCode(ModuleCode.HOURS).active(true).build()
        ));

        final java.util.List<ModuleInfoDto> resp = service.listAll(1L);

        org.junit.jupiter.api.Assertions.assertNotNull(resp);
        org.junit.jupiter.api.Assertions.assertEquals(1, resp.size());
        verify(internalAccessPolicy).validate(eq(academy), eq(current));
        verify(listModulesUseCase).apply(eq(1L));
    }

    @Test
    void info_delegates_to_usecase() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(2L).username("u").academy(academy).fullName("User").password("p").build();
        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        final ModuleInfoDto expected = ModuleInfoDto.builder().academyId(1L).section(ModuleSection.MANAGEMENT).moduleCode(ModuleCode.HOURS).active(false).build();
        when(getModuleInfoUseCase.apply(eq(1L), eq(ModuleSection.MANAGEMENT), eq(ModuleCode.HOURS))).thenReturn(expected);

        final ModuleInfoDto resp = service.info(1L, ModuleSection.MANAGEMENT, ModuleCode.HOURS);

        org.junit.jupiter.api.Assertions.assertNotNull(resp);
        org.junit.jupiter.api.Assertions.assertEquals(ModuleSection.MANAGEMENT, resp.getSection());
        verify(internalAccessPolicy).validate(eq(academy), eq(current));
        verify(getModuleInfoUseCase).apply(eq(1L), eq(ModuleSection.MANAGEMENT), eq(ModuleCode.HOURS));
    }
}
