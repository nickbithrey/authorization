-- CREATE ROLES
INSERT INTO ROLE (id, name) VALUES (1, 'USER');

-- CREATE USERS
INSERT INTO USERINFO (id, username, password, enabled, failed_attempts, max_failed_attempts) VALUES (2, 'admin', '$2a$10$AZmT259FCn3sWHCe/Cv83urHsmJIKxeIHk4DC0N0ptEVOHSkpJd.W', 1, 0, 3);