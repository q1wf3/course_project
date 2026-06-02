# JaCoCo-отчет

HTML-отчет формируется командой:

```bash
cd backend
mvn test jacoco:report
```

Путь к отчету:

```text
backend/target/site/jacoco/index.html
```

Текущие показатели после расширения тестов:

| Метрика | Покрытие |
| --- | --- |
| Instructions | 53.56% |
| Branches | 50.00% |
| Lines | 60.78% |

Порог методических требований выше 40% выполнен.

