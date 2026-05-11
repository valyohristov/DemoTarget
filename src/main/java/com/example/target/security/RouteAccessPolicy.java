package com.example.target.security;

import com.example.target.model.Person;
import com.example.target.model.Route;
import org.springframework.security.access.AccessDeniedException;

public final class RouteAccessPolicy {

    private RouteAccessPolicy() {}

    /**
     * Users with no home location see all routes (back-office). Otherwise only routes
     * whose start location matches the user's location.
     */
    public static void assertRouteAccessible(Person viewer, Route route) {
        if (viewer.getLocation() == null) {
            return;
        }
        if (route.getStartLocation() == null) {
            throw new AccessDeniedException("This route has no start location.");
        }
        if (!route.getStartLocation().getId().equals(viewer.getLocation().getId())) {
            throw new AccessDeniedException("This route is not in your location.");
        }
    }
}
