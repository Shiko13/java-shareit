INSERT INTO users (id, name, email)
VALUES (1, 'Oleg', 'oleg@yandex.ru');

INSERT INTO users (id, name, email)
VALUES (2, 'Irina', 'irina@yandex.ru');

INSERT INTO requests (id, description, requestor_id, created)
VALUES (4, 'I want to dry my hair', 2, '2023-01-01 12:00:00');

INSERT INTO items (id, name, description, is_available, owner_id, request_id)
VALUES (3, 'Dryer', 'For curly hair', true, 1, 4);

INSERT INTO bookings (id, start_date, end_date, item_id, booker_id, status)
VALUES (7, '2023-01-2 12:00:00', '2023-02-15 12:00:00', 3, 1, 'UNSUPPORTED');