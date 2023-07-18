--INSERT INTO rating (rating_name)
--    VALUES
--        ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');
--
--INSERT INTO genre (genre_name)
--    VALUES
--        ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

INSERT INTO USERS (user_name, EMAIL, LOGIN, BIRTHDAY)
    values
        ('User Name1', 'emailUser1@mail.com', 'login1', '2000-10-11'),
        ('User Name2', 'emailUser2@mail.com', 'login2', '2000-10-12'),
        ('User Name3', 'emailUser3@mail.com', 'login3', '2000-10-13');

INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, rating_mpa, count_likes)
    VALUES
        ('Film Name1', 'Description1', '2000-10-11', '120', '1', '0'),
        ('Film Name2', 'Description2', '2000-10-12', '140', '2', '0'),
        ('Film Name3', 'Description3', '2000-10-13', '180', '3', '0');

INSERT INTO film_likes (film_id, user_id)
    VALUES
        (1, 1),
        (1, 2),
        (2, 1),
        (3, 1),
        (3, 2),
        (3, 3);