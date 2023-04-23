# java-filmorate
Template repository for Filmorate project.
Ссылка на структуру базы данных [dbdiagram.io](https://dbdiagram.io/d/644378a96b31947051028cb1)
<img alt=”Ссылка на схему” src=”https://i.imgur.com/o9Jvele.png” width =”14” height=”14”>
### Примеры SQL запросов к БД

- Пример запроса для получения всех пользователей
~~~ 
     SELECT u.user_id,
            u.user_login,
            u.user_email,
            u.user_name,
            u.user_birthday,
     FROM users u
~~~