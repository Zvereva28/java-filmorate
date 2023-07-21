drop table if exists films, users, film_likes, friends, genre, film_genre, reviews, feed cascade;


create table if not exists films (
id integer generated by default as identity not null primary key,
name varchar (150) not null,
description varchar (200),
release_date date,
duration integer,
rating_mpa varchar(20),
count_likes integer,
created timestamp default now()
);

create table if not exists users(
id integer generated by default as identity not null primary key,
user_name varchar (150) not null,
email varchar (200) not null unique,
login varchar (150) not null,
birthday date,
created timestamp default now()
);

create table if not exists genre(
id integer generated by default as identity not null primary key,
genre_name varchar (150) not null
);

create table if not exists film_genre(
genre_id integer not null references genre(id) on delete cascade,
film_id  integer not null references films(id) on delete cascade,
UNIQUE (genre_id, film_id)
);

create table if not exists friends(
user_id integer not null references users(id) on delete cascade,
friend_id  integer not null references users(id) on delete cascade,
UNIQUE (user_id, friend_id)
);

create table if not exists film_likes(
user_id integer not null references users(id) on delete cascade,
film_id  integer not null references films(id) on delete cascade,
UNIQUE (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS reviews (
reviewId INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
content VARCHAR (200),
isPositive boolean,
userId INTEGER NOT NULL REFERENCES USERS(ID) ON DELETE CASCADE,
filmId INTEGER NOT NULL REFERENCES FILMS(ID) ON DELETE CASCADE,
useful INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS feed (
event_id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
timestamp timestamp,
user_id INTEGER NOT NULL REFERENCES USERS(ID) ON DELETE CASCADE,
event_type VARCHAR(6),
operation VARCHAR(6),
entity_id INTEGER NOT NULL
);