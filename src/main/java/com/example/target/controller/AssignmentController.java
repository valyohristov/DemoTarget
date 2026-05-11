package com.example.target.controller;

import com.example.target.model.Person;
import com.example.target.model.RoutePersonVehicle;
import com.example.target.security.CurrentPersonService;
import com.example.target.security.RouteAccessPolicy;
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
    private final CurrentPersonService currentPersonService;

    public AssignmentController(RoutePersonVehicleService routePersonVehicleService,
                               RouteService routeService,
                               PersonService personService,
                               VehicleService vehicleService,
                               CurrentPersonService currentPersonService) {
        this.routePersonVehicleService = routePersonVehicleService;
        this.routeService = routeService;
        this.personService = personService;
        this.vehicleService = vehicleService;
        this.currentPersonService = currentPersonService;
    }

    @GetMapping
    public String list(Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        model.addAttribute("rows", routePersonVehicleService.getAllVisibleTo(me));
        return "assignments/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        model.addAttribute("form", new AssignmentForm());
        model.addAttribute("routes", routeService.getRoutesVisibleTo(me));
        model.addAttribute("persons", personService.getAll());
        model.addAttribute("vehicles", vehicleService.getAll());
        model.addAttribute("mode", "add");
        return "assignments/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("form") AssignmentForm form) {
        Person me = currentPersonService.requireCurrentPerson();
        routePersonVehicleService.save(null, form.getRouteId(), form.getPersonId(), form.getVehicleId(), me);
        return "redirect:/assignments";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        Optional<RoutePersonVehicle> row = routePersonVehicleService.getById(id);
        if (row.isEmpty()) {
            return "redirect:/assignments";
        }
        RouteAccessPolicy.assertRouteAccessible(me, row.get().getRoute());
        AssignmentForm form = new AssignmentForm();
        form.setId(row.get().getId());
        form.setRouteId(row.get().getRoute().getId());
        form.setPersonId(row.get().getPerson().getId());
        form.setVehicleId(row.get().getVehicle().getId());
        model.addAttribute("form", form);
        model.addAttribute("routes", routeService.getRoutesVisibleTo(me));
        model.addAttribute("persons", personService.getAll());
        model.addAttribute("vehicles", vehicleService.getAll());
        model.addAttribute("mode", "edit");
        return "assignments/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("form") AssignmentForm form) {
        Person me = currentPersonService.requireCurrentPerson();
        routePersonVehicleService.save(id, form.getRouteId(), form.getPersonId(), form.getVehicleId(), me);
        return "redirect:/assignments";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Person me = currentPersonService.requireCurrentPerson();
        routePersonVehicleService.delete(id, me);
        return "redirect:/assignments";
    }
}
