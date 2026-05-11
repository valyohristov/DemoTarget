package com.example.target.controller;

import com.example.target.model.Route;
import com.example.target.service.RouteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("routes", routeService.getAll());
        return "routes/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("route", new Route());
        model.addAttribute("mode", "add");
        return "routes/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Route route) {
        routeService.save(route);
        return "redirect:/routes";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Route> route = routeService.getById(id);
        if (route.isEmpty()) {
            return "redirect:/routes";
        }
        model.addAttribute("route", route.get());
        model.addAttribute("mode", "edit");
        return "routes/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Route route) {
        route.setId(id);
        routeService.save(route);
        return "redirect:/routes";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        routeService.delete(id);
        return "redirect:/routes";
    }
}
