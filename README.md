# Тестовое задание. Пишем микробанкинг.

не забудем создать базу данных

docker run --name postgres \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=postgres \
-e POSTGRES_DB=banc \
-p 5432:5432 \
-d postgres:latest

При старте приложения будет создан дефолтный пользователь 
admin с паролем password

также базу зальет hibernate

свагер будет на дефолтном адрессе http://localhost:8080/swagger-ui/index.html

в свагере все эндпоинты описаны тут писать смысла не вижу



