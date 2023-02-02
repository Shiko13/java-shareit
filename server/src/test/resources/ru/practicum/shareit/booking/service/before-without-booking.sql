INSERT INTO users (id, name, email)
VALUES (1, 'Oleg', 'oleg@yandex.ru');

INSERT INTO users (id, name, email)
VALUES (2, 'Irina', 'irina@yandex.ru');

INSERT INTO requests (id, description, requestor_id, created)
VALUES (4, 'I want to dry my hair', 2, '2023-01-01 12:00:00');

INSERT INTO items (id, name, description, is_available, owner_id, request_id)
VALUES (3, 'Dryer', 'For curly hair', true, 1, 4);

INSERT INTO items (id, name, description, is_available, owner_id, request_id)
VALUES (5, 'Hammer', 'With gold handle', true, 2, null);