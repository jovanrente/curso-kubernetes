#!/bin/bash
set -e

mysql -u root -p"$MYSQL_ROOT_PASSWORD" <<-EOSQL
    CREATE DATABASE IF NOT EXISTS msvc_usuario;
    CREATE DATABASE IF NOT EXISTS msvc_cursos;
EOSQL 