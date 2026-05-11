package com.example.target.controller;

import com.example.target.model.AccessLevel;
import com.example.target.model.Person;
import com.example.target.model.Route;
import com.example.target.model.Vehicle;
import com.example.target.service.PersonService;
import com.example.target.service.RouteService;
import com.example.target.service.VehicleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;
    private final RouteService routeService;
    private final VehicleService vehicleService;

    public PersonController(PersonService personService,
                            RouteService routeService,
                            VehicleService vehicleService) {
        this.personService = personService;
        this.routeService = routeService;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("persons", personService.getAll());
        return "persons/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("person", new Person());
        addAccessFormModel(model, null);
        model.addAttribute("mode", "add");
        return "persons/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Person person, HttpServletRequest request) {
        Person saved = personService.save(person);
        personService.replaceAccess(saved.getId(), parseRouteAccess(request), parseVehicleAccess(request));
        return "redirect:/persons";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Person> person = personService.getById(id);
        if (person.isEmpty()) {
            return "redirect:/persons";
        }
        model.addAttribute("person", person.get());
        addAccessFormModel(model, id);
        model.addAttribute("mode", "edit");
        return "persons/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Person person, HttpServletRequest request) {
        person.setId(id);
        personService.save(person);
        personService.replaceAccess(id, parseRouteAccess(request), parseVehicleAccess(request));
        return "redirect:/persons";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        personService.delete(id);
        return "redirect:/persons";
    }

    private void addAccessFormModel(Model model, Long personId) {
        model.addAttribute("routes", routeService.getAll());
        model.addAttribute("vehicles", vehicleService.getAll());
        model.addAttribute("accessLevels", AccessLevel.values());
        model.addAttribute("routeAccess", personService.routeAccessMapForForm(personId));
        model.addAttribute("vehicleAccess", personService.vehicleAccessMapForForm(personId));
    }

    private Map<Long, AccessLevel> parseRouteAccess(HttpServletRequest request) {
        Map<Long, AccessLevel> out = new HashMap<>();
        for (Route r : routeService.getAll()) {
            String raw = request.getParameter("routeAccess_" + r.getId());
            if (raw != null && !raw.isBlank() && !"NONE".equalsIgnoreCase(raw.trim())) {
                out.put(r.getId(), AccessLevel.valueOf(raw.trim().toUpperCase()));
            }
        }
        return out;
    }

    private Map<Long, AccessLevel> parseVehicleAccess(HttpServletRequest request) {
        Map<Long, AccessLevel> out = new HashMap<>();
        for (Vehicle v : vehicleService.getAll()) {
            String raw = request.getParameter("vehicleAccess_" + v.getId());
            if (raw != null && !raw.isBlank() && !"NONE".equalsIgnoreCase(raw.trim())) {
                out.put(v.getId(), AccessLevel.valueOf(raw.trim().toUpperCase()));
            }
        }
        return out;
    }
}
