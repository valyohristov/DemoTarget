package com.example.target.web;

public final class UiPermissions {

    private final boolean routesRead;
    private final boolean routesWrite;
    private final boolean vehiclesRead;
    private final boolean vehiclesWrite;
    private final boolean locationsRead;
    private final boolean locationsWrite;
    private final boolean assignmentsRead;
    private final boolean assignmentsWrite;
    private final boolean personsManage;

    public UiPermissions(boolean routesRead, boolean routesWrite,
                         boolean vehiclesRead, boolean vehiclesWrite,
                         boolean locationsRead, boolean locationsWrite,
                         boolean assignmentsRead, boolean assignmentsWrite,
                         boolean personsManage) {
        this.routesRead = routesRead;
        this.routesWrite = routesWrite;
        this.vehiclesRead = vehiclesRead;
        this.vehiclesWrite = vehiclesWrite;
        this.locationsRead = locationsRead;
        this.locationsWrite = locationsWrite;
        this.assignmentsRead = assignmentsRead;
        this.assignmentsWrite = assignmentsWrite;
        this.personsManage = personsManage;
    }

    public static UiPermissions fullAccess() {
        return new UiPermissions(true, true, true, true, true, true, true, true, true);
    }

    public static UiPermissions anonymous() {
        return new UiPermissions(false, false, false, false, false, false, false, false, false);
    }

    public boolean getRoutesRead() { return routesRead; }
    public boolean getRoutesWrite() { return routesWrite; }
    public boolean getVehiclesRead() { return vehiclesRead; }
    public boolean getVehiclesWrite() { return vehiclesWrite; }
    public boolean getLocationsRead() { return locationsRead; }
    public boolean getLocationsWrite() { return locationsWrite; }
    public boolean getAssignmentsRead() { return assignmentsRead; }
    public boolean getAssignmentsWrite() { return assignmentsWrite; }
    public boolean getPersonsManage() { return personsManage; }
}
