DROP TABLE IF EXISTS config;
DROP TABLE IF EXISTS advanced;
DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS user_data;
DROP TABLE IF EXISTS global_data;
DROP TABLE IF EXISTS nicks;

CREATE TABLE config (
    config_key      TEXT, 
    config_value    TEXT
);

CREATE TABLE advanced (
    advanced_key    TEXT, 
    advanced_value  BOOLEAN
);

CREATE TABLE game (
    game_key        TEXT,
    game_value      INTEGER
);

CREATE TABLE unsent_score(
    date            INTEGER,
    speed           INTEGER,
    density         INTEGER,
    radius          INTEGER,
    score           FLOAT
);

CREATE TABLE user_data (
    data_id         INTEGER PRIMARY KEY,
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
    user            TEXT,
    speed           INTEGER,
    density         INTEGER,
    radius          INTEGER,
    score           FLOAT
);

INSERT INTO config
        SELECT 'hash'    , ''
 UNION  SELECT 'nick'    , 'Anon'
 UNION  SELECT 'version' , '1';

INSERT INTO advanced  
        SELECT 'postProcessor'   , 0
 UNION  SELECT 'fogFilter'       , 0 
 UNION  SELECT 'cartoon'         , 1  
 UNION  SELECT 'lighting'        , 0 
 UNION  SELECT 'debugGrids'      , 0 
 UNION  SELECT 'mainGrid'        , 1  
 UNION  SELECT 'gradientFloor'   , 0 
 UNION  SELECT 'verbose'         , 0 
 UNION  SELECT 'worldRotate'     , 1  ;

INSERT INTO game 
        SELECT 'density',    1
 UNION  SELECT 'radius' ,    1 
 UNION  SELECT 'speed'  ,    1 ;