alter table t_sleep_log
    add constraint one_sleep_log_per_user_and_date unique (user_id, sleep_date);
