FROM mariadb

MAINTAINER me

ENV MYSQL_DATABASE=hyperspy \
    MYSQL_ROOT_PASSWORD=root

ADD DB/tables.sql /docker-entrypoint-initdb.d

EXPOSE 3306