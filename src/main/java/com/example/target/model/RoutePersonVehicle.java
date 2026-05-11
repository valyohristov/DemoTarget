package com.example.target.model;

import jakarta.persistence.*;

@Entity
@Table(name = "route_person_vehicle",
        uniqueConstraints = @UniqueConstraint(name = "uq_rpv_triple", columnNames = {"route_id", "person_id", "vehicle_id"}))
public class RoutePersonVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    public RoutePersonVehicle() {}

    public RoutePersonVehicle(Route route, Person person, Vehicle vehicle) {
        this.route = route;
        this.person = person;
        this.vehicle = vehicle;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
}
