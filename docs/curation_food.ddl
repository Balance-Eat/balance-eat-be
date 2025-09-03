create table curation_food
(
    id         bigint    default nextval('curation_product_id_seq'::regclass) not null
        constraint curation_product_pk
            primary key,
    food_id    bigint,
    weight     smallint,
    created_at timestamp default CURRENT_TIMESTAMP                            not null,
    updated_at timestamp default CURRENT_TIMESTAMP                            not null
);