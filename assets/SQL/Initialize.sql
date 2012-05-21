DROP TABLE IF EXISTS config;
DROP TABLE IF EXISTS advanced;
DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS user_data;
DROP TABLE IF EXISTS global_data;
DROP TABLE IF EXISTS nicks;

CREATE TABLE config (
    config_key      STRING, 
    config_value    STRING
);

CREATE TABLE advanced (
    advanced_key    STRING, 
    advanced_value  BOOLEAN
);

CREATE TABLE game (
    game_key        STRING,
    game_value      INTEGER
);

CREATE TABLE user_data (
    data_id         INTEGER PRIMARY KEY,
    global_id       INTEGER,
    date            INTEGER,
    speed           INTEGER,
    density         INTEGER,
    radius          INTEGER,
    score           FLOAT
);

CREATE TABLE global_data (
    data_id         INTEGER PRIMARY KEY,
    global_id       INTEGER,
    date            INTEGER
    user_id         INTEGER,
    speed           INTEGER,
    density         INTEGER,
    radius          INTEGER,
    score           FLOAT
);

CREATE TABLE nicks(
    user_id         INTEGER PRIMARY KEY,
    nick            STRING
);
