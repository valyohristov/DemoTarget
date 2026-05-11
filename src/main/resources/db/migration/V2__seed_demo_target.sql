INSERT INTO routes (name, description) VALUES
('North Loop', 'Northern distribution circuit'),
('South Corridor', 'South depot to hub');

INSERT INTO vehicles (identifier, description) VALUES
('VH-1001', 'Delivery van'),
('TR-204', 'Straight truck');

INSERT INTO persons (first_name, last_name, email) VALUES
('Alex', 'Rivera', 'alex.rivera@example.com'),
('Jordan', 'Lee', 'jordan.lee@example.com');

INSERT INTO person_route_access (person_id, route_id, access_level) VALUES
(1, 1, 'WRITE'),
(1, 2, 'VIEW'),
(2, 1, 'READ');

INSERT INTO person_vehicle_access (person_id, vehicle_id, access_level) VALUES
(1, 1, 'VIEW'),
(2, 1, 'WRITE'),
(2, 2, 'READ');

INSERT INTO route_person_vehicle (route_id, person_id, vehicle_id) VALUES
(1, 1, 1),
(2, 2, 2);
