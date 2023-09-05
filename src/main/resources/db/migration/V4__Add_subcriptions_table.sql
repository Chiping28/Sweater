create table user_subscriptions(
    channel_id int references person(id) on delete set null,
    subscriber_id int references person(id) on delete set null,
    primary key(channel_id, subscriber_id)
);