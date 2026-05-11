package com.example.target.controller;

import com.example.target.model.ObjectType;
import com.example.target.model.Person;
import com.example.target.model.Vehicle;
import com.example.target.security.CurrentPersonService;
import com.example.target.service.PersonPermissionService;
import com.example.target.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final CurrentPersonService currentPersonService;
    private final PersonPermissionService personPermissionService;

    public VehicleController(VehicleService vehicleService,
                             CurrentPersonService currentPersonService,
                             PersonPermissionService personPermissionService) {
        this.vehicleService = vehicleService;
        this.currentPersonService = currentPersonService;
        this.personPermissionService = personPermissionService;
    }

    @GetMapping
    public String list(Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canRead(me, ObjectType.VEHICLE)) {
            model.addAttribute("message", "You need read access to vehicles to open this page.");
            return "no-access";
        }
        model.addAttribute("vehicles", vehicleService.getAll());
        return "vehicles/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canWrite(me, ObjectType.VEHICLE)) {
            model.addAttribute("message", "You need write access to vehicles to add one.");
            return "no-access";
        }
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("mode", "add");
        return "vehicles/form";
    }

    @PostMapping("/add")
    public String add(Model model, @ModelAttribute Vehicle vehicle) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canWrite(me, ObjectType.VEHICLE)) {
            model.addAttribute("message", "You need write access to vehicles to add one.");
            return "no-access";
        }
        vehicleService.save(vehicle);
        return "redirect:/vehicles";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canWrite(me, ObjectType.VEHICLE)) {
            model.addAttribute("message", "You need write access to vehicles to edit.");
            return "no-access";
        }
        Optional<Vehicle> vehicle = vehicleService.getById(id);
        if (vehicle.isEmpty()) {
            return "redirect:/vehicles";
        }
        model.addAttribute("vehicle", vehicle.get());
        model.addAttribute("mode", "edit");
        return "vehicles/form";
    }

    @PostMapping("/edit/{id}")
    public String update(Model model, @PathVariable Long id, @ModelAttribute Vehicle vehicle) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canWrite(me, ObjectType.VEHICLE)) {
            model.addAttribute("message", "You need write access to vehicles to edit.");
            return "no-access";
        }
        vehicle.setId(id);
        vehicleService.save(vehicle);
        return "redirect:/vehicles";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canWrite(me, ObjectType.VEHICLE)) {
            model.addAttribute("message", "You need write access to vehicles to delete.");
            return "no-access";
        }
        vehicleService.delete(id);
        return "redirect:/vehicles";
    }
}
