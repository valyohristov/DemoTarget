CREATE TABLE routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000)
);

CREATE TABLE vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    identifier VARCHAR(255) NOT NULL,
    description VARCHAR(1000)
);

CREATE TABLE persons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT uq_person_email UNIQUE (email)
);

CREATE TABLE person_route_access (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    person_id BIGINT NOT NULL,
    route_id BIGINT NOT NULL,
    access_level VARCHAR(20) NOT NULL,
    CONSTRAINT uq_person_route UNIQUE (person_id, route_id),
    CONSTRAINT fk_pra_person FOREIGN KEY (person_id) REFERENCES persons (id) ON DELETE CASCADE,
    CONSTRAINT fk_pra_route FOREIGN KEY (route_id) REFERENCES routes (id) ON DELETE CASCADE
);

CREATE TABLE person_vehicle_access (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    person_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    access_level VARCHAR(20) NOT NULL,
    CONSTRAINT uq_person_vehicle UNIQUE (person_id, vehicle_id),
    CONSTRAINT fk_pva_person FOREIGN KEY (person_id) REFERENCES persons (id) ON DELETE CASCADE,
    CONSTRAINT fk_pva_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles (id) ON DELETE CASCADE
);

CREATE TABLE route_person_vehicle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_id BIGINT NOT NULL,
    person_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    CONSTRAINT uq_rpv_triple UNIQUE (route_id, person_id, vehicle_id),
    CONSTRAINT fk_rpv_route FOREIGN KEY (route_id) REFERENCES routes (id) ON DELETE CASCADE,
    CONSTRAINT fk_rpv_person FOREIGN KEY (person_id) REFERENCES persons (id) ON DELETE CASCADE,
    CONSTRAINT fk_rpv_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles (id) ON DELETE CASCADE
);
