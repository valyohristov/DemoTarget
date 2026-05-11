CREATE TABLE person_object_type_access (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    person_id BIGINT NOT NULL,
    object_type VARCHAR(20) NOT NULL,
    access_level VARCHAR(20) NOT NULL,
    CONSTRAINT uq_person_type_level UNIQUE (person_id, object_type, access_level),
    CONSTRAINT fk_pota_person FOREIGN KEY (person_id) REFERENCES persons (id) ON DELETE CASCADE
);

INSERT INTO person_object_type_access (person_id, object_type, access_level)
SELECT DISTINCT person_id, 'ROUTE', access_level FROM person_route_access;

INSERT INTO person_object_type_access (person_id, object_type, access_level)
SELECT DISTINCT person_id, 'VEHICLE', access_level FROM person_vehicle_access;

DROP TABLE person_route_access;
DROP TABLE person_vehicle_access;
