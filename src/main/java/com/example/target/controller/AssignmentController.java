package com.example.target.controller;

import com.example.target.model.RoutePersonVehicle;
import com.example.target.service.PersonService;
import com.example.target.service.RoutePersonVehicleService;
import com.example.target.service.RouteService;
import com.example.target.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/assignments")
public class AssignmentController {

    private final RoutePersonVehicleService routePersonVehicleService;
    private final RouteService routeService;
    private final PersonService personService;
    private final VehicleService vehicleService;

    public AssignmentController(RoutePersonVehicleService routePersonVehicleService,
                               RouteService routeService,
                               PersonService personService,
                               VehicleService vehicleService) {
        this.routePersonVehicleService = routePersonVehicleService;
        this.routeService = routeService;
        this.personService = personService;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rows", routePersonVehicleService.getAll());
        return "assignments/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("form", new AssignmentForm());
        model.addAttribute("routes", routeService.getAll());
        model.addAttribute("persons", personService.getAll());
        model.addAttribute("vehicles", vehicleService.getAll());
        model.addAttribute("mode", "add");
        return "assignments/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("form") AssignmentForm form) {
        routePersonVehicleService.save(null, form.getRouteId(), form.getPersonId(), form.getVehicleId());
        return "redirect:/assignments";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<RoutePersonVehicle> row = routePersonVehicleService.getById(id);
        if (row.isEmpty()) {
            return "redirect:/assignments";
        }
        AssignmentForm form = new AssignmentForm();
        form.setId(row.get().getId());
        form.setRouteId(row.get().getRoute().getId());
        form.setPersonId(row.get().getPerson().getId());
        form.setVehicleId(row.get().getVehicle().getId());
        model.addAttribute("form", form);
        model.addAttribute("routes", routeService.getAll());
        model.addAttribute("persons", personService.getAll());
        model.addAttribute("vehicles", vehicleService.getAll());
        model.addAttribute("mode", "edit");
        return "assignments/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("form") AssignmentForm form) {
        routePersonVehicleService.save(id, form.getRouteId(), form.getPersonId(), form.getVehicleId());
        return "redirect:/assignments";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        routePersonVehicleService.delete(id);
        return "redirect:/assignments";
    }
}
