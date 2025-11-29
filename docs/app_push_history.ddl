create table app_push_history
(
    id         bigserial
        constraint app_push_history_pk
            primary key,
    device_id  bigint                              not null,
    user_id    bigint                              not null,
    agent_id   varchar(255)                        not null,
    title      varchar(255)                        not null,
    content    text                                not null,
    deep_link  varchar(100),
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);