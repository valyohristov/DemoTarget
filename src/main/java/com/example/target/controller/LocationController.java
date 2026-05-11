package com.example.target.controller;

import com.example.target.model.Location;
import com.example.target.model.ObjectType;
import com.example.target.model.Person;
import com.example.target.security.CurrentPersonService;
import com.example.target.service.LocationService;
import com.example.target.service.PersonPermissionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;
    private final CurrentPersonService currentPersonService;
    private final PersonPermissionService personPermissionService;

    public LocationController(LocationService locationService,
                              CurrentPersonService currentPersonService,
                              PersonPermissionService personPermissionService) {
        this.locationService = locationService;
        this.currentPersonService = currentPersonService;
        this.personPermissionService = personPermissionService;
    }

    @GetMapping
    public String list(Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canRead(me, ObjectType.LOCATION)) {
            model.addAttribute("message", "You need read access to locations to open this page.");
            return "no-access";
        }
        model.addAttribute("locations", locationService.getAll());
        return "locations/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canWrite(me, ObjectType.LOCATION)) {
            model.addAttribute("message", "You need write access to locations to add one.");
            return "no-access";
        }
        model.addAttribute("location", new Location());
        model.addAttribute("mode", "add");
        return "locations/form";
    }

    @PostMapping("/add")
    public String add(Model model, @ModelAttribute Location location) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canWrite(me, ObjectType.LOCATION)) {
            model.addAttribute("message", "You need write access to locations to add one.");
            return "no-access";
        }
        locationService.save(location);
        return "redirect:/locations";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canWrite(me, ObjectType.LOCATION)) {
            model.addAttribute("message", "You need write access to locations to edit.");
            return "no-access";
        }
        Optional<Location> location = locationService.getById(id);
        if (location.isEmpty()) {
            return "redirect:/locations";
        }
        model.addAttribute("location", location.get());
        model.addAttribute("mode", "edit");
        return "locations/form";
    }

    @PostMapping("/edit/{id}")
    public String update(Model model, @PathVariable Long id, @ModelAttribute Location location) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canWrite(me, ObjectType.LOCATION)) {
            model.addAttribute("message", "You need write access to locations to edit.");
            return "no-access";
        }
        location.setId(id);
        locationService.save(location);
        return "redirect:/locations";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canWrite(me, ObjectType.LOCATION)) {
            model.addAttribute("message", "You need write access to locations to delete.");
            return "no-access";
        }
        locationService.delete(id);
        return "redirect:/locations";
    }
}
