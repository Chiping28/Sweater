create table message_likes (
	message_id int references message(id),
	person_id int references person(id),
	primary key(message_id, person_id)
);