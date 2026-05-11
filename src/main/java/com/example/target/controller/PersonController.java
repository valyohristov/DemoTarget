package com.example.target.controller;

import com.example.target.model.AccessLevel;
import com.example.target.model.ObjectType;
import com.example.target.model.Person;
import com.example.target.service.LocationService;
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

    public PersonController(PersonService personService, LocationService locationService) {
        this.personService = personService;
        this.locationService = locationService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("persons", personService.getAll());
        return "persons/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("locations", locationService.getAll());
        addAccessFormModel(model, null);
        model.addAttribute("mode", "add");
        return "persons/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Person person,
                      @RequestParam("password") String password,
                      @RequestParam(value = "locationId", required = false) String locationId,
                      HttpServletRequest request) {
        Person saved = personService.saveNew(person, password, parseLocationId(locationId));
        personService.replaceTypeAccess(saved.getId(), parseTypeAccess(request));
        return "redirect:/persons";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
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
    public String update(@PathVariable Long id,
                         @ModelAttribute Person person,
                         @RequestParam(value = "newPassword", required = false) String newPassword,
                         @RequestParam(value = "locationId", required = false) String locationId,
                         HttpServletRequest request) {
        person.setId(id);
        personService.updateExisting(person, newPassword, parseLocationId(locationId));
        personService.replaceTypeAccess(id, parseTypeAccess(request));
        return "redirect:/persons";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
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
