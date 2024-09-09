CREATE SCHEMA s409178;
CREATE TABLE s409178.User (
user_id SERIAL PRIMARY KEY,
username VARCHAR (50) UNIQUE NOT NULL,
password VARCHAR (50) NOT NULL,
salt VARCHAR (50) NOT NULL
);

CREATE TABLE s409178.Coordinates (
coordinates_id SERIAL PRIMARY KEY,
coordinates_x INTEGER NOT NULL CHECK (coordinates_x > -465),
coordinates_y BIGINT NOT NULL CHECK (coordinates_y > -493)
);

CREATE TABLE s409178.Location (
location_id SERIAL PRIMARY KEY,
location_x BIGINT,
location_y BIGINT,
location_z BIGINT
);

CREATE TABLE s409178.Address (
address_id SERIAL PRIMARY KEY,
zip_code VARCHAR(100) NOT NULL CHECK (LENGTH(zip_code) >= 8),
town INTEGER NOT NULL,
FOREIGN KEY (town) REFERENCES s409178.Location(location_id) ON DELETE CASCADE
);

CREATE TYPE s409178.OrganisationType AS ENUM (
    'PUBLIC',
    'GOVERNMENT',
    'TRUST'
);

CREATE TABLE s409178.Organisation (
organisation_id SERIAL PRIMARY KEY,
address_id INTEGER NOT NULL,
user_id INTEGER,
coordinates_id INTEGER NOT NULL,
organisation_type s409178.OrganisationType NOT NULL,
name VARCHAR(255) NOT NULL,
creation_date DATE NOT NULL DEFAULT CURRENT_DATE,
annual_turnover BIGINT CHECK (annual_turnover > 0),
full_name VARCHAR(1322) NOT NULL UNIQUE CHECK (LENGTH(full_name) < 1322),
FOREIGN KEY (address_id) REFERENCES s409178.Address(address_id) ON DELETE CASCADE,
FOREIGN KEY (user_id) REFERENCES s409178.User(user_id) ON DELETE CASCADE,
FOREIGN KEY (coordinates_id) REFERENCES s409178.Coordinates(coordinates_id)
);
-- psql -h localhost -U myuser -d mydatabase -f initdb.sql
--export DBPASS="mysecretpassword"
--export DBURL="jdbc:postgresql://localhost:5432/mydatabase"
--export DBUSER="myuser"


INSERT INTO s409178.User (username, password, salt) VALUES
                                                     ('user1', 'hashed_password1', 'salt1'),
                                                     ('user2', 'hashed_password2', 'salt2'),
                                                     ('user3', 'hashed_password3', 'salt3'),
                                                     ('user4', 'hashed_password4', 'salt4'),
                                                     ('user5', 'hashed_password5', 'salt5');


INSERT INTO s409178.Coordinates (coordinates_x, coordinates_y) VALUES
                                        (-450, -480),
                                        (-440, -470),
                                        (-430, -460),
                                        (-420, -450),
                                        (-410, -440);

INSERT INTO s409178.Location (location_x, location_y, location_z) VALUES
                                        (10, 20, 30),
                                        (11, 21, 31),
                                        (12, 22, 32),
                                        (13, 23, 33),
                                        (14, 24, 34);


INSERT INTO s409178.Address (zip_code, town) VALUES
                                              ('12345678', 1),
                                              ('23456789', 2),
                                              ('34567890', 3),
                                              ('45678901', 4),
                                              ('56789012', 5);

INSERT INTO s409178.Organisation (address_id, user_id, coordinates_id, organisation_type, name, annual_turnover, full_name) VALUES
                                                                                                                             (1, 1, 1, 'PUBLIC', 'Organisation1', 100000, 'Full Name Org1'),
                                                                                                                             (2, 2, 2, 'GOVERNMENT', 'Organisation2', 200000, 'Full Name Org2'),
                                                                                                                             (3, 3, 3, 'TRUST', 'Organisation3', 300000, 'Full Name Org3'),
                                                                                                                             (4, 4, 4, 'PUBLIC', 'Organisation4', 400000, 'Full Name Org4'),
                                                                                                                             (5, 5, 5, 'GOVERNMENT', 'Organisation5', 500000, 'Full Name Org5');