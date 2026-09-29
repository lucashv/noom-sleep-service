create table t_sleep_log
(
    id         uuid primary key,
    user_id    uuid         not null,
    sleep_date date         not null,
    started_at timestamp    not null,
    ended_at   timestamp    not null,
    feeling    varchar(255) not null,
    constraint sleep_user_fk foreign key (user_id) references t_user (id),
    constraint feeling_values check (feeling in ('BAD', 'OK', 'GOOD'))
)
