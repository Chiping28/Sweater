create table person(
    id int generated by default as identity primary key,
    username varchar NOT NULL,
    password varchar not null,
    role varchar,
    email varchar,
    activation_code varchar
);

create table message(
    id int generated by default as identity primary key,
    text varchar,
    tag varchar,
    file varchar,
    person_id int references person(id) on delete set null
);