drop table if exists beer_order_shipments;

create table beer_order_shipments (
    id char(36) primary key,
    beer_order_id char(36) not null unique,
    tracking_number varchar(50) not null,
    version integer not null,
    created_date datetime(6) not null,
    update_date datetime(6) not null,
    constraint foreign key(beer_order_id) references beer_orders(id));