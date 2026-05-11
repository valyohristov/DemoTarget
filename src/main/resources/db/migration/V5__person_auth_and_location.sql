ALTER TABLE persons ADD COLUMN password_hash VARCHAR(255) NULL;
ALTER TABLE persons ADD COLUMN location_id BIGINT NULL;
ALTER TABLE persons ADD CONSTRAINT fk_persons_location FOREIGN KEY (location_id) REFERENCES locations (id) ON DELETE SET NULL;

-- Demo logins (email / password): alex.rivera@example.com / password, jordan.lee@example.com / password
UPDATE persons SET password_hash = '$2a$10$7Xld6l7sMsTGWw8N8EuFYuuwLc1hHCkD7vxTfHBG2ejVEwHaukP3G', location_id = 1 WHERE id = 1;
UPDATE persons SET password_hash = '$2a$10$7Xld6l7sMsTGWw8N8EuFYuuwLc1hHCkD7vxTfHBG2ejVEwHaukP3G', location_id = 2 WHERE id = 2;

-- Full visibility to all routes: home location IS NULL (back-office).
INSERT INTO persons (first_name, last_name, email, password_hash, location_id) VALUES
('Admin', 'User', 'admin@example.com', '$2a$10$7Xld6l7sMsTGWw8N8EuFYuuwLc1hHCkD7vxTfHBG2ejVEwHaukP3G', NULL);
