-- This is the schema file that the database is initialized with. It is specific to the H2 SQL dialect.
-- Author: Rasmus Ros, rasmus.ros@cs.lth.se


-- User roles describe what each user can do on a generic level.
CREATE TABLE user_role(role_id TINYINT,
                       role VARCHAR(10) NOT NULL UNIQUE,
                       PRIMARY KEY (role_id));

CREATE TABLE user(user_id INT AUTO_INCREMENT NOT NULL,
                  role_id TINYINT NOT NULL,
                  username VARCHAR_IGNORECASE NOT NULL UNIQUE, -- username should be unique
                  salt BIGINT NOT NULL,
                  password_hash VARCHAR NOT NULL,
                  PRIMARY KEY (user_id),
                  FOREIGN KEY (role_id) REFERENCES user_role (role_id),
                  CHECK (LENGTH(username) >= 4)); -- ensures that username have 4 or more characters

-- Sessions are indexed by large random numbers instead of a sequence of integers, because they could otherwise
-- be guessed by a malicious user.
CREATE TABLE session(session_uuid UUID DEFAULT RANDOM_UUID(),
                     user_id INT NOT NULL,
                     last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
                     PRIMARY KEY(session_uuid),
                     FOREIGN KEY(user_id) REFERENCES user(user_id) ON DELETE CASCADE);

INSERT INTO user_role VALUES (1, 'ADMIN'), (2, 'USER');
INSERT INTO user (role_id, username, salt, password_hash)
    VALUES (1, 'Admin', -3084944687275696311, 'dUAYAH1GMtGGoLSNdNeAgzwikcCgDKXhTB7ZWjrncsc='),
           (2, 'Test', 6240894663902665109, 'zn8DL+k4yWjhhS9S2NcbUfBNfSEBPhpm7s87YBi+w4g=');

-- Example table containing some data per user, you are expected to remove this table in your project.
CREATE TABLE simple(simple_id INT AUTO_INCREMENT NOT NULL,
                    payload VARCHAR NOT NULL,
                    user_id INT NOT NULL,
                    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
                    PRIMARY KEY(simple_id),
                    FOREIGN KEY(user_id) REFERENCES user(user_id) ON DELETE CASCADE);
