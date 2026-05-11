package com.example.target.model;

import jakarta.persistence.*;

@Entity
@Table(name = "person_object_type_access",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_person_type_level",
                columnNames = {"person_id", "object_type", "access_level"}))
public class PersonObjectTypeAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(name = "object_type", nullable = false, length = 20)
    private ObjectType objectType;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false, length = 20)
    private AccessLevel accessLevel;

    public PersonObjectTypeAccess() {}

    public PersonObjectTypeAccess(Person person, ObjectType objectType, AccessLevel accessLevel) {
        this.person = person;
        this.objectType = objectType;
        this.accessLevel = accessLevel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public ObjectType getObjectType() { return objectType; }
    public void setObjectType(ObjectType objectType) { this.objectType = objectType; }

    public AccessLevel getAccessLevel() { return accessLevel; }
    public void setAccessLevel(AccessLevel accessLevel) { this.accessLevel = accessLevel; }
}
