create table IF NOT EXISTS order_entity (
    id varchar(255) not null,
    first_name varchar(255) not null,
    last_name varchar(255),
    email varchar(255),
    primary key (id)
);

create table IF NOT EXISTS order_item_entity (
    product_id varchar(255) not null,
    name varchar(255) not null,
    quantity int,
    unit_cost int,
    total_cost int,
    order_entity varchar(255) not null
);