INSERT
IGNORE INTO users (username, name, email, password, enabled) VALUES
    ('user', 'Willy De Keyser', 'user@fake.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', '1'),
    ('admin', 'Tom Black', 'admin@fake.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', '1');
    
INSERT
IGNORE INTO authorities (username, authority) VALUES
    ('user', 'ROLE_USER'),
    ('admin', 'ROLE_USER'),
    ('admin', 'ROLE_ADMIN');