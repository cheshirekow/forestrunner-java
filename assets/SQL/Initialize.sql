DROP TABLE IF EXISTS strings;
DROP TABLE IF EXISTS booleans;
DROP TABLE IF EXISTS integers;
DROP TABLE IF EXISTS unsent_score;
DROP TABLE IF EXISTS user_data;
DROP TABLE IF EXISTS global_data;

CREATE TABLE strings (
    string_key      TEXT PRIMARY KEY, 
    string_value    TEXT
);

CREATE TABLE booleans (
    bool_key    TEXT PRIMARY KEY, 
    bool_value  BOOLEAN
);

CREATE TABLE integers (
    int_key        TEXT PRIMARY KEY,
    int_value      INTEGER
);

CREATE TABLE unsent_score(
    score_id        INTEGER PRIMARY KEY AUTOINCREMENT,
    date            INTEGER,
    velocity        INTEGER,
    density         INTEGER,
    radius          INTEGER,
    score           FLOAT
);

CREATE TABLE user_data (
    data_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    date            INTEGER,
    velocity        INTEGER,
    density         INTEGER,
    radius          INTEGER,
    score           FLOAT
);

CREATE TABLE global_data (
    data_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    nick            TEXT,
    date            INTEGER,
    velocity        INTEGER,
    density         INTEGER,
    radius          INTEGER,
    score           FLOAT,
    global_id       INTEGER
);

CREATE UNIQUE INDEX global_id_index ON global_data (global_id);

INSERT INTO strings
        SELECT 'hash'    , ''
 UNION  SELECT 'nick'    , 'Anon';

INSERT INTO booleans  
        SELECT 'postProcessor'   , 0
 UNION  SELECT 'fogFilter'       , 0 
 UNION  SELECT 'cartoon'         , 1  
 UNION  SELECT 'lighting'        , 0 
 UNION  SELECT 'debugGrids'      , 0 
 UNION  SELECT 'mainGrid'        , 1  
 UNION  SELECT 'gradientFloor'   , 0 
 UNION  SELECT 'verbose'         , 0 
 UNION  SELECT 'worldRotate'     , 1  ;

INSERT INTO integers
        SELECT 'density',    1
 UNION  SELECT 'radius' ,    1 
 UNION  SELECT 'velocity'  , 1
 UNION  SELECT 'version',    3;