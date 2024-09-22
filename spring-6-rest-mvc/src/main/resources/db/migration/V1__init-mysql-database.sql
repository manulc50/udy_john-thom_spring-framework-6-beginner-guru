drop table if exists beers;
drop table if exists customers;

create table beers (
    id char(36) primary key,
    name varchar(50) not null,
    price decimal(38,2) not null,
    quantity_on_hand integer,
    style smallint not null,
    upc varchar(30) not null,
    version integer not null,
    created_date datetime(6) not null,
    update_date datetime(6) not null);

create table customers (
    id char(36) primary key,
    name varchar(255) not null,
    version integer not null,
    created_date datetime(6) not null,
    update_date datetime(6) not null);
