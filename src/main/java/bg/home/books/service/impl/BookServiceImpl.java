package bg.home.books.service.impl;

import bg.home.books.model.entity.BookEntity;
import bg.home.books.model.entity.AuthorEntity;
import bg.home.books.model.entity.dto.BookDTO;
import bg.home.books.model.entity.dto.AuthorDTO;
import bg.home.books.repository.BookRepository;
import bg.home.books.repository.AuthorRepository;
import bg.home.books.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Маркираме класа като @Service, за да може Spring да го разпознае като компонент
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;  // Репозитори за работа с книги (BookEntity)
    private final AuthorRepository authorRepository; // Репозитори за работа с автори (AuthorEntity)

    // Конструктор, който инжектира зависимостите
    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    // Извличане на всички книги като BookDTO
    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()  // Взимаме всички книги от базата
                .map(this::mapBookToDTO) // Преобразуваме всяка BookEntity в BookDTO
                .toList(); // Конвертираме потока в списък и го връщаме
    }

    // Търсене на книга по ID
    @Override
    public Optional<BookDTO> findBookById(Long id) {
        return bookRepository.findById(id) // Търсим книгата по ID
                .map(this::mapBookToDTO); // Ако я намерим, я преобразуваме в DTO
    }

    // Създаване на нова книга
    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        // Проверяваме дали авторът съществува, ако не – го създаваме
        AuthorEntity author = authorRepository.findByName(bookDTO.getAuthor().getName())
                .orElseGet(() -> createAuthor(bookDTO.getAuthor().getName()));

        // Създаваме нов BookEntity със стойностите от DTO-то
        BookEntity newBook = new BookEntity()
                .setAuthor(author)
                .setIsbn(bookDTO.getIsbn())
                .setTitle(bookDTO.getTitle());

        // Запазваме книгата в базата и връщаме DTO версията ѝ
        return mapBookToDTO(bookRepository.save(newBook));
    }

    // Обновяване на съществуваща книга по ID
    @Override
    public Optional<BookDTO> updateBook(Long id, BookDTO bookDTO) {
        return bookRepository.findById(id) // Търсим книгата по ID
                .map(book -> {  // Ако я намерим, обновяваме данните ѝ
                    book.setTitle(bookDTO.getTitle());
                    book.setIsbn(bookDTO.getIsbn());

                    // Проверяваме дали авторът съществува, ако не – го създаваме
                    book.setAuthor(authorRepository.findByName(bookDTO.getAuthor().getName())
                            .orElseGet(() -> createAuthor(bookDTO.getAuthor().getName())));

                    // Запазваме променената книга и връщаме нейното DTO
                    return mapBookToDTO(bookRepository.save(book));
                });
    }

    // Изтриване на книга по ID
    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    // Създаване на нов автор, ако не съществува
    private AuthorEntity createAuthor(String authorName) {
        return authorRepository.save(new AuthorEntity().setName(authorName));
    }

    // Преобразуване на BookEntity към BookDTO
    private BookDTO mapBookToDTO(BookEntity bookEntity) {
        AuthorDTO authorDTO = new AuthorDTO().setName(bookEntity.getAuthor().getName());
        return new BookDTO()
                .setId(bookEntity.getId())
                .setAuthor(authorDTO)
                .setTitle(bookEntity.getTitle())
                .setIsbn(bookEntity.getIsbn());
    }
}
