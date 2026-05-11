package com.example.target.model;

import jakarta.persistence.*;

@Entity
@Table(name = "person_route_access",
        uniqueConstraints = @UniqueConstraint(name = "uq_person_route", columnNames = {"person_id", "route_id"}))
public class PersonRouteAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false, length = 20)
    private AccessLevel accessLevel;

    public PersonRouteAccess() {}

    public PersonRouteAccess(Person person, Route route, AccessLevel accessLevel) {
        this.person = person;
        this.route = route;
        this.accessLevel = accessLevel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public AccessLevel getAccessLevel() { return accessLevel; }
    public void setAccessLevel(AccessLevel accessLevel) { this.accessLevel = accessLevel; }
}
