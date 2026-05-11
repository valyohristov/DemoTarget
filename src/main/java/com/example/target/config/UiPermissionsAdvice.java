package com.example.target.config;

import com.example.target.security.CurrentPersonService;
import com.example.target.service.PersonPermissionService;
import com.example.target.web.UiPermissions;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UiPermissionsAdvice {

    private final CurrentPersonService currentPersonService;
    private final PersonPermissionService personPermissionService;

    public UiPermissionsAdvice(CurrentPersonService currentPersonService,
                               PersonPermissionService personPermissionService) {
        this.currentPersonService = currentPersonService;
        this.personPermissionService = personPermissionService;
    }

    @ModelAttribute("perm")
    public UiPermissions uiPermissions() {
        return currentPersonService.getCurrentPerson()
                .map(personPermissionService::uiPermissionsFor)
                .orElseGet(UiPermissions::anonymous);
    }
}
