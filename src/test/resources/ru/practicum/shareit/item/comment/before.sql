INSERT INTO users (id, name, email)
VALUES (1, 'Oleg', 'oleg@yandex.ru');

INSERT INTO users (id, name, email)
VALUES (2, 'Irina', 'irina@yandex.ru');

INSERT INTO items (id, name, description, is_available, owner_id, request_id)
VALUES (3, 'Dryer', 'For curly hair', true, 1, null);

INSERT INTO comments (id, text, item_id, author_id, created)
VALUES (4, 'Hot!', 3, 2, '2023-01-20 12:00:00');