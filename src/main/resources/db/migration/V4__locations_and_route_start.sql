CREATE TABLE locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    CONSTRAINT uq_location_name UNIQUE (name)
);

INSERT INTO locations (name, description) VALUES
('Main Depot', 'Primary hub'),
('North Gate', 'Northern entry point');

ALTER TABLE routes ADD COLUMN start_location_id BIGINT NULL;
ALTER TABLE routes ADD CONSTRAINT fk_routes_start_location
    FOREIGN KEY (start_location_id) REFERENCES locations (id) ON DELETE SET NULL;

UPDATE routes SET start_location_id = 1 WHERE id = 1;
UPDATE routes SET start_location_id = 2 WHERE id = 2;
