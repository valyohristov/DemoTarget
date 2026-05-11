package com.example.target.controller;

import com.example.target.model.Vehicle;
import com.example.target.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("vehicles", vehicleService.getAll());
        return "vehicles/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("mode", "add");
        return "vehicles/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Vehicle vehicle) {
        vehicleService.save(vehicle);
        return "redirect:/vehicles";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Vehicle> vehicle = vehicleService.getById(id);
        if (vehicle.isEmpty()) {
            return "redirect:/vehicles";
        }
        model.addAttribute("vehicle", vehicle.get());
        model.addAttribute("mode", "edit");
        return "vehicles/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Vehicle vehicle) {
        vehicle.setId(id);
        vehicleService.save(vehicle);
        return "redirect:/vehicles";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return "redirect:/vehicles";
    }
}
