delete from message;

INSERT INTO message (id, text, tag, person_id)
VALUES
	(1, 'first', 'new-tag', 2),
	(2, 'second', 'some-tag', 2),
	(3, 'third', 'some-tag', 2),
	(4, 'fourth', 'old-tag', 2);