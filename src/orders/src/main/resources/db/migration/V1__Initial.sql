create table IF NOT EXISTS orders (
    id varchar(255) not null,
    created_date TIMESTAMP,
    primary key (id)
);

create table IF NOT EXISTS order_items (
    product_id varchar(255) not null,
    quantity int,
    unit_cost int,
    total_cost int,
    order_id varchar(255) references orders(id)
);

create table IF NOT EXISTS shipping_addresses (
    order_id varchar(255) primary key references orders(id),
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    email varchar(255) not null,
    address1 varchar(255) not null,
    address2 varchar(255),
    city varchar(255) not null,
    zip_code varchar(255) not null,
    state_id varchar(255) not null
);