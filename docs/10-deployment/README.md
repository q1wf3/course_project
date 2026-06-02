# Этап 10. Развертывание

## Цель этапа

Описать запуск Movie Collection в учебном окружении: Docker Compose для backend и PostgreSQL, параметры окружения, адреса сервисов и порядок проверки работоспособности.

## Артефакты этапа

| Артефакт | Содержание | Статус |
|---|---|---|
| [Развертывание](deployment.md) | Docker Compose, переменные окружения, запуск backend, PostgreSQL и pgAdmin | Выполнено |

## Команды запуска

| Назначение | Команда |
|---|---|
| Запуск backend и PostgreSQL | `docker compose up -d postgres backend` |
| Запуск pgAdmin | `docker compose --profile tools up -d pgadmin` |
| Остановка контейнеров | `docker compose down` |

## Адреса сервисов

| Сервис | Адрес |
|---|---|
| Backend | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| pgAdmin | `http://localhost:5050` |

## Контроль соответствия методическим указаниям

| Требование | Статус |
|---|---|
| Описание запуска проекта | Выполнено |
| Использование Docker | Выполнено |
| Использование PostgreSQL | Выполнено |
| Описание адресов сервисов | Выполнено |
