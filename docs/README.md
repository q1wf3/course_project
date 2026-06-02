# Документация Movie Collection

## Назначение

Папка `docs` содержит комплект проектной документации по курсовому проекту Movie Collection. Материалы сгруппированы по этапам жизненного цикла: от бизнес-анализа и требований до реализации, тестирования, развертывания и итоговой проверки соответствия методическим указаниям.

## Структура документации

| Раздел | Содержание | Статус |
|---|---|---|
| [00-project-charter](00-project-charter/README.md) | Инициация, бизнес-анализ, глоссарий, стейкхолдеры, SWOT и ROI | Выполнено |
| [01-requirements](01-requirements/README.md) | Требования, варианты использования, модель предметной области и трассировка | Выполнено |
| [02-architecture](02-architecture/README.md) | Архитектура, PCMEF, зависимости, интерфейсы и ADR | Выполнено |
| [03-database](03-database/README.md) | PostgreSQL, ER-диаграмма, DDL и ORM | Выполнено |
| [04-detailed-design](04-detailed-design/README.md) | Проектные классы, последовательности и спецификации методов | Выполнено |
| [05-implementation](05-implementation/README.md) | Реализация, структура кода, слои и паттерны | Выполнено |
| [06-testing](06-testing/README.md) | План тестирования, сценарии проверок и JaCoCo | Выполнено |
| [07-refactoring](07-refactoring/README.md) | Рефакторинг, code smells, Data Mapper и Identity Map | Выполнено |
| [08-ui](08-ui/README.md) | Пользовательский интерфейс, навигация и экраны Android-приложения | Выполнено |
| [09-api](09-api/README.md) | REST API, OpenAPI, Postman и ручная проверка endpoints | Выполнено |
| [10-deployment](10-deployment/README.md) | Docker Compose, PostgreSQL, backend и pgAdmin | Выполнено |
| [11-guides](11-guides/README.md) | Руководства пользователя, администратора и техническая спецификация | Выполнено |
| [11-user-guide](11-user-guide/README.md) | Дополнительное краткое пользовательское руководство | Выполнено |
| [12-project-management](12-project-management/README.md) | WBS, диаграмма Ганта и COCOMO | Выполнено |
| [12-final-report](12-final-report/README.md) | Материалы для пояснительной записки | Выполнено |
| [13-compliance](13-compliance/README.md) | Чек-лист соответствия требованиям методички | Выполнено |
| [images](images/README.md) | Общие изображения диаграмм для пояснительной записки | Выполнено |

## Основные изображения

| Изображение | Назначение |
|---|---|
| [report-use-case-diagram.png](images/report-use-case-diagram.png) | Диаграмма вариантов использования |
| [report-er-diagram.png](images/report-er-diagram.png) | ER-диаграмма базы данных |
| [report-pcmef-architecture.png](images/report-pcmef-architecture.png) | Архитектура PCMEF |
| [report-create-movie-sequence.png](images/report-create-movie-sequence.png) | Создание фильма |
| [report-offline-cache-sequence.png](images/report-offline-cache-sequence.png) | Оффлайн-режим |

## Как пользоваться разделом

README-файл в каждой папке выполняет роль оглавления этапа: показывает цель, состав документов, ссылки на диаграммы и контроль выполнения требований. Для пояснительной записки можно брать основные формулировки из тематических файлов, а изображения - из локальных папок `images`.
