package bg.home.books.init;

import bg.home.books.model.entity.AuthorEntity;
import bg.home.books.model.entity.BookEntity;
import bg.home.books.repository.AuthorRepository;
import bg.home.books.repository.BookRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component // Маркира класа като Spring компонент, който ще се изпълни автоматично
public class DBInit implements CommandLineRunner {

    private final AuthorRepository authorRepository; // Репозитори за работа с авторите
    private final BookRepository bookRepository; // Репозитори за работа с книгите

    // Конструктор за инжектиране на зависимостите (авторите и книгите)
    DBInit(
            AuthorRepository authorRepository,
            BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Проверяваме дали базата е празна (няма книги и автори)
        if (bookRepository.count() == 0 && authorRepository.count() == 0) {
            // Ако е празна, добавяме тестови данни (известни български автори)
            initJovkov();
            initNikolaiHaitov();
            initDimitarTalev();
            initElinPelin();
            initVazov();
        }
    }

    // Метод за добавяне на книги на Николай Хайтов
    private void initNikolaiHaitov() {
        initAuthor("Николай Хайтов",
                "Диви Разкази"
        );
    }

    // Метод за добавяне на книги на Димитър Димов
    private void initDimitarTalev() {
        initAuthor("Димитър Димов",
                "Тютюн"
        );
    }

    // Метод за добавяне на книги на Елин Пелин
    private void initElinPelin() {
        initAuthor("Елин Пелин",
                "Пижо и Пендо",
                "Ян Бибиян на луната",
                "Под манастирската лоза"
        );
    }

    // Метод за добавяне на книги на Иван Вазов
    private void initVazov() {
        initAuthor("Иван Вазов",
                "Пряпорец и Гусла",
                "Под Игото",
                "Тъгите на България"
        );
    }

    // Метод за добавяне на книги на Йордан Йовков
    private void initJovkov() {
        initAuthor("Йордан Йовков",
                "Старопланински легенди",
                "Чифликът край границата");
    }

    // Общ метод за добавяне на автор и неговите книги
    private void initAuthor(String authorName, String... books) {
        // Създаваме нов автор и му задаваме име
        AuthorEntity author = new AuthorEntity();
        author.setName(authorName);

        // Запазваме автора в базата
        author = authorRepository.save(author);

        // Създаваме списък за всички книги на този автор
        List<BookEntity> allBooks = new ArrayList<>();

        // Обхождаме подадените книги и създаваме BookEntity за всяка
        for (String book : books) {
            BookEntity aBook = new BookEntity();
            aBook.setAuthor(author); // Задаваме автора на книгата
            aBook.setTitle(book); // Задаваме заглавието на книгата
            aBook.setIsbn(UUID.randomUUID().toString()); // Генерираме уникален ISBN (за тестови цели)
            allBooks.add(aBook); // Добавяме книгата в списъка
        }

        // Свързваме книгите с автора
        author.setBooks(allBooks);

        // Обновяваме автора с пълния списък от книги
        authorRepository.save(author);

        // Записваме всички книги в базата
        bookRepository.saveAll(allBooks);
    }
}
