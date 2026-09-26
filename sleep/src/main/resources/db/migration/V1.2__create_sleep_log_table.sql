create table t_sleep_log (
     id serial primary key,
     user_id int not null,
     started_at timestamp not null,
     ended_at timestamp null,
     feeling smallint null,
     constraint sleep_user_fk foreign key (user_id) references t_user(id)
)
