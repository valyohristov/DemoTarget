package com.example.target.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String identifier;

    @Column(length = 1000)
    private String description;

    public Vehicle() {}

    public Vehicle(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
