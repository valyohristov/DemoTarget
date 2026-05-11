package com.example.target.controller;

import com.example.target.model.Location;
import com.example.target.service.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("locations", locationService.getAll());
        return "locations/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("location", new Location());
        model.addAttribute("mode", "add");
        return "locations/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Location location) {
        locationService.save(location);
        return "redirect:/locations";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Location> location = locationService.getById(id);
        if (location.isEmpty()) {
            return "redirect:/locations";
        }
        model.addAttribute("location", location.get());
        model.addAttribute("mode", "edit");
        return "locations/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Location location) {
        location.setId(id);
        locationService.save(location);
        return "redirect:/locations";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        locationService.delete(id);
        return "redirect:/locations";
    }
}
