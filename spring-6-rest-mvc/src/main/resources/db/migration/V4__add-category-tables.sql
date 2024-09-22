drop table if exists categories;
drop table if exists beers_categories;

create table categories (
    id char(36) primary key,
    description varchar(50) not null,
    version integer not null,
    created_date datetime(6) not null,
    update_date datetime(6) not null);

create table beers_categories (
    beer_id char(36) not null,
    category_id char(36) not null,
    primary key(beer_id,category_id),
    constraint foreign key(beer_id) references beers(id),
    constraint foreign key(category_id) references categories(id));