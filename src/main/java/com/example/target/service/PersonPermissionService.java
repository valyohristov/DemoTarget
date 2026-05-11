package com.example.target.service;

import com.example.target.model.AccessLevel;
import com.example.target.model.ObjectType;
import com.example.target.model.Person;
import com.example.target.model.PersonObjectTypeAccess;
import com.example.target.repository.PersonObjectTypeAccessRepository;
import com.example.target.web.UiPermissions;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class PersonPermissionService {

    private final PersonObjectTypeAccessRepository personObjectTypeAccessRepository;

    public PersonPermissionService(PersonObjectTypeAccessRepository personObjectTypeAccessRepository) {
        this.personObjectTypeAccessRepository = personObjectTypeAccessRepository;
    }

    /**
     * Users without a home location are treated as back-office: full access (same as route visibility).
     */
    public boolean isBackOffice(Person person) {
        return person.getLocation() == null;
    }

    /** Only back-office accounts (no home location) may CRUD person records. */
    public boolean canManagePersons(Person person) {
        return isBackOffice(person);
    }

    public UiPermissions uiPermissionsFor(Person person) {
        if (isBackOffice(person)) {
            return UiPermissions.fullAccess();
        }
        Map<ObjectType, Set<AccessLevel>> m = loadAllForPerson(person.getId());
        return new UiPermissions(
                hasRead(m, ObjectType.ROUTE),
                hasWrite(m, ObjectType.ROUTE),
                hasRead(m, ObjectType.VEHICLE),
                hasWrite(m, ObjectType.VEHICLE),
                hasRead(m, ObjectType.LOCATION),
                hasWrite(m, ObjectType.LOCATION),
                hasRead(m, ObjectType.ROUTE) && hasRead(m, ObjectType.VEHICLE),
                hasWrite(m, ObjectType.ROUTE) && hasWrite(m, ObjectType.VEHICLE),
                false
        );
    }

    public boolean canRead(Person person, ObjectType type) {
        if (isBackOffice(person)) {
            return true;
        }
        return hasRead(loadAllForPerson(person.getId()), type);
    }

    public boolean canWrite(Person person, ObjectType type) {
        if (isBackOffice(person)) {
            return true;
        }
        return hasWrite(loadAllForPerson(person.getId()), type);
    }

    public boolean canViewAssignments(Person person) {
        if (isBackOffice(person)) {
            return true;
        }
        Map<ObjectType, Set<AccessLevel>> m = loadAllForPerson(person.getId());
        return hasRead(m, ObjectType.ROUTE) && hasRead(m, ObjectType.VEHICLE);
    }

    public boolean canEditAssignments(Person person) {
        if (isBackOffice(person)) {
            return true;
        }
        Map<ObjectType, Set<AccessLevel>> m = loadAllForPerson(person.getId());
        return hasWrite(m, ObjectType.ROUTE) && hasWrite(m, ObjectType.VEHICLE);
    }

    private static boolean hasRead(Map<ObjectType, Set<AccessLevel>> m, ObjectType type) {
        Set<AccessLevel> s = m.get(type);
        return s.contains(AccessLevel.READ) || s.contains(AccessLevel.WRITE);
    }

    private static boolean hasWrite(Map<ObjectType, Set<AccessLevel>> m, ObjectType type) {
        return m.get(type).contains(AccessLevel.WRITE);
    }

    private Map<ObjectType, Set<AccessLevel>> loadAllForPerson(Long personId) {
        Map<ObjectType, Set<AccessLevel>> m = new EnumMap<>(ObjectType.class);
        for (ObjectType t : ObjectType.values()) {
            m.put(t, new HashSet<>());
        }
        for (PersonObjectTypeAccess row : personObjectTypeAccessRepository.findByPerson_Id(personId)) {
            m.get(row.getObjectType()).add(row.getAccessLevel());
        }
        return m;
    }
}
