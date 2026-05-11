package com.example.target.controller;

import com.example.target.model.AccessLevel;
import com.example.target.model.ObjectType;
import com.example.target.model.Person;
import com.example.target.security.CurrentPersonService;
import com.example.target.service.LocationService;
import com.example.target.service.PersonPermissionService;
import com.example.target.service.PersonService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;
    private final LocationService locationService;
    private final CurrentPersonService currentPersonService;
    private final PersonPermissionService personPermissionService;

    public PersonController(PersonService personService,
                            LocationService locationService,
                            CurrentPersonService currentPersonService,
                            PersonPermissionService personPermissionService) {
        this.personService = personService;
        this.locationService = locationService;
        this.currentPersonService = currentPersonService;
        this.personPermissionService = personPermissionService;
    }

    @GetMapping
    public String list(Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canManagePersons(me)) {
            model.addAttribute("message", "Only administrators can manage persons.");
            return "no-access";
        }
        model.addAttribute("persons", personService.getAll());
        return "persons/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canManagePersons(me)) {
            model.addAttribute("message", "Only administrators can manage persons.");
            return "no-access";
        }
        model.addAttribute("person", new Person());
        model.addAttribute("locations", locationService.getAll());
        addAccessFormModel(model, null);
        model.addAttribute("mode", "add");
        return "persons/form";
    }

    @PostMapping("/add")
    public String add(Model model,
                      @ModelAttribute Person person,
                      @RequestParam("password") String password,
                      @RequestParam(value = "locationId", required = false) String locationId,
                      HttpServletRequest request) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canManagePersons(me)) {
            model.addAttribute("message", "Only administrators can manage persons.");
            return "no-access";
        }
        Person saved = personService.saveNew(person, password, parseLocationId(locationId));
        personService.replaceTypeAccess(saved.getId(), parseTypeAccess(request));
        return "redirect:/persons";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canManagePersons(me)) {
            model.addAttribute("message", "Only administrators can manage persons.");
            return "no-access";
        }
        Optional<Person> person = personService.getById(id);
        if (person.isEmpty()) {
            return "redirect:/persons";
        }
        model.addAttribute("person", person.get());
        model.addAttribute("locations", locationService.getAll());
        addAccessFormModel(model, id);
        model.addAttribute("mode", "edit");
        return "persons/form";
    }

    @PostMapping("/edit/{id}")
    public String update(Model model,
                         @PathVariable Long id,
                         @ModelAttribute Person person,
                         @RequestParam(value = "newPassword", required = false) String newPassword,
                         @RequestParam(value = "locationId", required = false) String locationId,
                         HttpServletRequest request) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canManagePersons(me)) {
            model.addAttribute("message", "Only administrators can manage persons.");
            return "no-access";
        }
        person.setId(id);
        personService.updateExisting(person, newPassword, parseLocationId(locationId));
        personService.replaceTypeAccess(id, parseTypeAccess(request));
        return "redirect:/persons";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canManagePersons(me)) {
            model.addAttribute("message", "Only administrators can manage persons.");
            return "no-access";
        }
        personService.delete(id);
        return "redirect:/persons";
    }

    private void addAccessFormModel(Model model, Long personId) {
        model.addAttribute("accessLevels", AccessLevel.values());
        Map<ObjectType, Set<AccessLevel>> typeAccess = personService.typeAccessForForm(personId);
        model.addAttribute("routeTypeAccess", typeAccess.get(ObjectType.ROUTE));
        model.addAttribute("vehicleTypeAccess", typeAccess.get(ObjectType.VEHICLE));
        model.addAttribute("locationTypeAccess", typeAccess.get(ObjectType.LOCATION));
    }

    private Map<ObjectType, Set<AccessLevel>> parseTypeAccess(HttpServletRequest request) {
        Map<ObjectType, Set<AccessLevel>> out = new EnumMap<>(ObjectType.class);
        out.put(ObjectType.ROUTE, parseLevelSet(request, "routeTypeAccess"));
        out.put(ObjectType.VEHICLE, parseLevelSet(request, "vehicleTypeAccess"));
        out.put(ObjectType.LOCATION, parseLevelSet(request, "locationTypeAccess"));
        return out;
    }

    private static Set<AccessLevel> parseLevelSet(HttpServletRequest request, String param) {
        String[] raw = request.getParameterValues(param);
        if (raw == null || raw.length == 0) {
            return EnumSet.noneOf(AccessLevel.class);
        }
        Set<AccessLevel> set = EnumSet.noneOf(AccessLevel.class);
        for (String r : raw) {
            if (r != null && !r.isBlank()) {
                set.add(AccessLevel.valueOf(r.trim().toUpperCase()));
            }
        }
        return set;
    }

    private static Long parseLocationId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return Long.parseLong(raw.trim());
    }
}
