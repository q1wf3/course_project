# Структура кода

```text
course_project-main/
├── backend/
│   ├── src/main/java/ru/skfu/moviecollection/
│   │   ├── config/
│   │   ├── control/
│   │   ├── entity/
│   │   ├── foundation/
│   │   └── mediator/
│   └── src/test/java/ru/skfu/moviecollection/
├── mobile/
│   └── app/src/main/java/ru/skfu/moviecollection/
│       ├── api_client/
│       ├── control/
│       ├── local_cache/
│       ├── model/
│       ├── presentation/
│       └── ui/theme/
└── docs/
```

Backend разделен по PCMEF. Android-клиент разделен на API-клиенты, локальный кэш, модель, UI-состояние и Compose-экраны.

