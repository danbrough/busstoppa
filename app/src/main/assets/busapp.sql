
CREATE TABLE t_stop (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    stop_code TEXT NOT NULL UNIQUE,
    stop_name TEXT NOT NULL,
    stop_lat REAL NOT NULL,
    stop_lon REAL NOT NULL,
    zone_id TEXT NOT NULL DEFAULT '',
    location_type INTEGER NOT NULL DEFAULT 0
);


CREATE TABLE t_stop_attributes (
    stop_code TEXT NOT NULL PRIMARY KEY,
    access_count INTEGER NOT NULL
);