drop table if exists beer_orders;
drop table if exists beer_order_lines;

create table beer_orders (
    id char(36) primary key,
    customer_id char(36) not null,
    customer_ref varchar(255) not null,
    version integer not null,
    created_date datetime(6) not null,
    update_date datetime(6) not null,
    constraint foreign key(customer_id) references customers(id));

create table beer_order_lines (
    id char(36) primary key,
    beer_id char(36) not null,
    beer_order_id char(36) not null,
    quantity integer not null,
    quantity_allocated integer not null,
    version integer not null,
    update_date datetime(6) not null,
    created_date datetime(6) not null,
    constraint foreign key(beer_id) references beers(id),
    constraint foreign key(beer_order_id) references beer_orders(id));