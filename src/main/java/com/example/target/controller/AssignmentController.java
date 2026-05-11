package com.example.target.controller;

import com.example.target.model.Person;
import com.example.target.model.RoutePersonVehicle;
import com.example.target.security.CurrentPersonService;
import com.example.target.security.RouteAccessPolicy;
import com.example.target.service.PersonPermissionService;
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
    private final PersonPermissionService personPermissionService;

    public AssignmentController(RoutePersonVehicleService routePersonVehicleService,
                               RouteService routeService,
                               PersonService personService,
                               VehicleService vehicleService,
                               CurrentPersonService currentPersonService,
                               PersonPermissionService personPermissionService) {
        this.routePersonVehicleService = routePersonVehicleService;
        this.routeService = routeService;
        this.personService = personService;
        this.vehicleService = vehicleService;
        this.currentPersonService = currentPersonService;
        this.personPermissionService = personPermissionService;
    }

    @GetMapping
    public String list(Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canViewAssignments(me)) {
            model.addAttribute("message", "You need read access to both routes and vehicles to view assignments.");
            return "no-access";
        }
        model.addAttribute("rows", routePersonVehicleService.getAllVisibleTo(me));
        return "assignments/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canEditAssignments(me)) {
            model.addAttribute("message", "You need write access to both routes and vehicles to add assignments.");
            return "no-access";
        }
        model.addAttribute("form", new AssignmentForm());
        model.addAttribute("routes", routeService.getRoutesVisibleTo(me));
        model.addAttribute("persons", personService.getAll());
        model.addAttribute("vehicles", vehicleService.getAll());
        model.addAttribute("mode", "add");
        return "assignments/form";
    }

    @PostMapping("/add")
    public String add(Model model, @ModelAttribute("form") AssignmentForm form) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canEditAssignments(me)) {
            model.addAttribute("message", "You need write access to both routes and vehicles to add assignments.");
            return "no-access";
        }
        routePersonVehicleService.save(null, form.getRouteId(), form.getPersonId(), form.getVehicleId(), me);
        return "redirect:/assignments";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canEditAssignments(me)) {
            model.addAttribute("message", "You need write access to both routes and vehicles to edit assignments.");
            return "no-access";
        }
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
    public String update(Model model, @PathVariable Long id, @ModelAttribute("form") AssignmentForm form) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canEditAssignments(me)) {
            model.addAttribute("message", "You need write access to both routes and vehicles to edit assignments.");
            return "no-access";
        }
        routePersonVehicleService.save(id, form.getRouteId(), form.getPersonId(), form.getVehicleId(), me);
        return "redirect:/assignments";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        if (!personPermissionService.canEditAssignments(me)) {
            model.addAttribute("message", "You need write access to both routes and vehicles to delete assignments.");
            return "no-access";
        }
        routePersonVehicleService.delete(id, me);
        return "redirect:/assignments";
    }
}
