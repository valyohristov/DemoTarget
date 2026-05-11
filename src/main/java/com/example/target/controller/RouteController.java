package com.example.target.controller;

import com.example.target.model.Location;
import com.example.target.model.Person;
import com.example.target.model.Route;
import com.example.target.security.CurrentPersonService;
import com.example.target.security.RouteAccessPolicy;
import com.example.target.service.LocationService;
import com.example.target.service.RouteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/routes")
public class RouteController {

    private final RouteService routeService;
    private final LocationService locationService;
    private final CurrentPersonService currentPersonService;

    public RouteController(RouteService routeService,
                           LocationService locationService,
                           CurrentPersonService currentPersonService) {
        this.routeService = routeService;
        this.locationService = locationService;
        this.currentPersonService = currentPersonService;
    }

    @GetMapping
    public String list(Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        model.addAttribute("routes", routeService.getRoutesVisibleTo(me));
        return "routes/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        model.addAttribute("route", new Route());
        model.addAttribute("locations", locationsForRouteForm(me));
        model.addAttribute("mode", "add");
        return "routes/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Route route,
                      @RequestParam(value = "startLocationId", required = false) String startLocationId) {
        Person me = currentPersonService.requireCurrentPerson();
        Long lid = parseLocationId(startLocationId);
        lid = constrainStartLocationForUser(me, lid);
        routeService.save(route, lid);
        return "redirect:/routes";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Person me = currentPersonService.requireCurrentPerson();
        Optional<Route> route = routeService.getById(id);
        if (route.isEmpty()) {
            return "redirect:/routes";
        }
        RouteAccessPolicy.assertRouteAccessible(me, route.get());
        model.addAttribute("route", route.get());
        model.addAttribute("locations", locationsForRouteForm(me));
        model.addAttribute("mode", "edit");
        return "routes/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Route route,
                         @RequestParam(value = "startLocationId", required = false) String startLocationId) {
        Person me = currentPersonService.requireCurrentPerson();
        Optional<Route> existing = routeService.getById(id);
        if (existing.isEmpty()) {
            return "redirect:/routes";
        }
        RouteAccessPolicy.assertRouteAccessible(me, existing.get());
        route.setId(id);
        Long lid = parseLocationId(startLocationId);
        lid = constrainStartLocationForUser(me, lid);
        routeService.save(route, lid);
        return "redirect:/routes";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Person me = currentPersonService.requireCurrentPerson();
        Route route = routeService.getById(id).orElseThrow();
        RouteAccessPolicy.assertRouteAccessible(me, route);
        routeService.delete(id);
        return "redirect:/routes";
    }

    private List<Location> locationsForRouteForm(Person me) {
        if (me.getLocation() == null) {
            return locationService.getAll();
        }
        return List.of(me.getLocation());
    }

    /**
     * Location-scoped users can only create/edit routes for their own location.
     */
    private static Long constrainStartLocationForUser(Person me, Long requestedStartLocationId) {
        if (me.getLocation() == null) {
            return requestedStartLocationId;
        }
        return me.getLocation().getId();
    }

    private static Long parseLocationId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return Long.parseLong(raw.trim());
    }
}
