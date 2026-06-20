package ru.skfu.moviecollection.config;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.moviecollection.entity.CollectionItem;
import ru.skfu.moviecollection.entity.Movie;
import ru.skfu.moviecollection.entity.User;
import ru.skfu.moviecollection.entity.WatchStatus;
import ru.skfu.moviecollection.foundation.CollectionItemRepository;
import ru.skfu.moviecollection.foundation.MovieRepository;
import ru.skfu.moviecollection.foundation.UserRepository;

@Component
public class TestUserMovieBootstrap implements CommandLineRunner {
    private static final String TEST_EMAIL = "test@yandex.ru";
    private static final String TEST_PASSWORD = "123456";

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CollectionItemRepository collectionItemRepository;
    private final PasswordEncoder passwordEncoder;

    public TestUserMovieBootstrap(
            UserRepository userRepository,
            MovieRepository movieRepository,
            CollectionItemRepository collectionItemRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.collectionItemRepository = collectionItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        var user = userRepository.findByEmail(TEST_EMAIL)
                .orElseGet(() -> userRepository.save(new User(
                        TEST_EMAIL,
                        passwordEncoder.encode(TEST_PASSWORD)
                )));

        demoMovies().forEach(demoMovie -> attachMovie(user, demoMovie));
    }

    private void attachMovie(User user, DemoMovie demoMovie) {
        var movies = movieRepository.findAllByTitleIgnoreCaseAndReleaseYear(
                demoMovie.title(),
                demoMovie.releaseYear()
        );
        var alreadyAttached = movies.stream()
                .anyMatch(movie -> collectionItemRepository.existsByOwnerIdAndMovieId(user.getId(), movie.getId()));
        if (alreadyAttached) {
            return;
        }

        var movie = movies.stream()
                .findFirst()
                .orElseGet(() -> movieRepository.save(new Movie(
                        demoMovie.title(),
                        demoMovie.releaseYear(),
                        demoMovie.director(),
                        demoMovie.durationMinutes(),
                        demoMovie.description(),
                        demoMovie.coverUrl(),
                        demoMovie.category()
                )));

        var item = new CollectionItem(user, movie, demoMovie.status());
        if (demoMovie.rating() != null) {
            item.rate(demoMovie.rating());
        }
        if (demoMovie.note() != null) {
            item.updateNote(demoMovie.note());
        }
        if (demoMovie.favorite()) {
            item.toggleFavorite();
        }
        collectionItemRepository.save(item);
    }

    private List<DemoMovie> demoMovies() {
        return List.of(
                new DemoMovie("Побег из Шоушенка", 1994, "Фрэнк Дарабонт", 142, "Драма", WatchStatus.WATCHED, 10, true, "Эталонная драма о надежде."),
                new DemoMovie("Зеленая миля", 1999, "Фрэнк Дарабонт", 189, "Драма", WatchStatus.WATCHED, 9, true, "Очень сильная история."),
                new DemoMovie("Интерстеллар", 2014, "Кристофер Нолан", 169, "Фантастика", WatchStatus.WATCHED, 10, true, "Космос, семья и музыка Циммера."),
                new DemoMovie("Начало", 2010, "Кристофер Нолан", 148, "Фантастика", WatchStatus.WATCHED, 9, true, "Сны внутри снов."),
                new DemoMovie("Опенгеймер", 2023, "Кристофер Нолан", 180, "Биография", WatchStatus.PLANNED, null, false, "Посмотреть в ближайшие выходные."),
                new DemoMovie("Дюна", 2021, "Дени Вильнев", 155, "Фантастика", WatchStatus.WATCHED, 8, false, "Красиво и масштабно."),
                new DemoMovie("Дюна: Часть вторая", 2024, "Дени Вильнев", 166, "Фантастика", WatchStatus.PLANNED, null, false, "Добавлено в планы."),
                new DemoMovie("Бегущий по лезвию 2049", 2017, "Дени Вильнев", 164, "Фантастика", WatchStatus.WATCHED, 9, true, "Неоновая медитация."),
                new DemoMovie("Ла-Ла Ленд", 2016, "Дэмьен Шазелл", 128, "Мюзикл", WatchStatus.WATCHED, 8, false, "Музыка и настроение."),
                new DemoMovie("Одержимость", 2014, "Дэмьен Шазелл", 106, "Драма", WatchStatus.WATCHED, 9, true, "Напряженный фильм про амбиции."),
                new DemoMovie("Матрица", 1999, "Лана и Лилли Вачовски", 136, "Фантастика", WatchStatus.WATCHED, 10, true, "Классика киберпанка."),
                new DemoMovie("Форрест Гамп", 1994, "Роберт Земекис", 142, "Драма", WatchStatus.WATCHED, 9, false, "Доброе большое кино."),
                new DemoMovie("Криминальное чтиво", 1994, "Квентин Тарантино", 154, "Криминал", WatchStatus.WATCHING, 9, false, "Пересматриваю сцены."),
                new DemoMovie("Унесенные призраками", 2001, "Хаяо Миядзаки", 125, "Анимация", WatchStatus.PLANNED, null, false, "Хочу пересмотреть."),
                new DemoMovie("Грань будущего", 2014, "Даг Лайман", 113, "Боевик", WatchStatus.WATCHED, 8, false, "Отличная петля времени."),
                new DemoMovie("Паразиты", 2019, "Пон Джун-хо", 132, "Триллер", WatchStatus.WATCHED, 9, true, "Остро и точно."),
                new DemoMovie("Социальная сеть", 2010, "Дэвид Финчер", 120, "Биография", WatchStatus.WATCHED, 8, false, "Ритм, диалоги, монтаж."),
                new DemoMovie("Темный рыцарь", 2008, "Кристофер Нолан", 152, "Боевик", WatchStatus.WATCHED, 10, true, "Один из любимых блокбастеров."),
                new DemoMovie("Джентльмены", 2019, "Гай Ричи", 113, "Криминал", WatchStatus.WATCHED, 8, false, "Легкий стильный криминал."),
                new DemoMovie("Достать ножи", 2019, "Райан Джонсон", 130, "Детектив", WatchStatus.PLANNED, null, false, "Для уютного вечера.")
        );
    }

    private record DemoMovie(
            String title,
            int releaseYear,
            String director,
            int durationMinutes,
            String category,
            WatchStatus status,
            Integer rating,
            boolean favorite,
            String note
    ) {
        private String description() {
            return note;
        }

        private String coverUrl() {
            return null;
        }
    }
}
