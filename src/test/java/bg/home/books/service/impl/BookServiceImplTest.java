package bg.home.books.service.impl;

import bg.home.books.model.entity.AuthorEntity;
import bg.home.books.model.entity.BookEntity;
import bg.home.books.model.entity.dto.AuthorDTO;
import bg.home.books.model.entity.dto.BookDTO;
import bg.home.books.repository.AuthorRepository;
import bg.home.books.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository; // Мокваме репозиторията за книги, за да не правим реални заявки към базата данни.

    @Mock
    private AuthorRepository authorRepository; // Мокваме репозиторията за автори, за да не правим реални заявки към базата данни.

    @InjectMocks
    private BookServiceImpl bookService; // Инжектираме мокнатите зависимости в сервиза за книги.

    private BookEntity bookEntity; // Тестова променлива за BookEntity (реално съществуваща книга).
    private BookDTO bookDTO; // Тестова променлива за BookDTO (обект от данни, който ще се използва за трансфер).
    private AuthorEntity authorEntity; // Тестова променлива за AuthorEntity (реален автор).
    private AuthorDTO authorDTO; // Тестова променлива за AuthorDTO (обект от данни на автора).

    @BeforeEach
    void setUp() {
        // Инициализираме тестовите обекти.
        authorEntity = new AuthorEntity().setName("John Doe"); // Създаваме нов автор с име "John Doe".
        authorDTO = new AuthorDTO().setName("John Doe"); // Създаваме нов DTO за автора.

        bookEntity = new BookEntity()
                .setId(1L)
                .setTitle("Test Book")
                .setIsbn("123456789")
                .setAuthor(authorEntity); // Създаваме нова книга с ID, заглавие, ISBN и автор.

        bookDTO = mapToDTO(bookEntity); // Преобразуваме BookEntity в BookDTO.
    }

    // Метод за преобразуване от BookEntity към BookDTO.
    private BookDTO mapToDTO(BookEntity book) {
        if (book == null) return null; // Ако книгата е null, връщаме null.
        BookDTO dto = new BookDTO();
        dto.setId(book.getId()); // Задаваме ID на книгата.
        dto.setTitle(book.getTitle()); // Задаваме заглавие.
        dto.setIsbn(book.getIsbn()); // Задаваме ISBN на книгата.
        if (book.getAuthor() != null) {
            dto.setAuthor(new AuthorDTO().setName(book.getAuthor().getName())); // Преобразуваме автора в AuthorDTO.
        }
        return dto;
    }

    // Метод за преобразуване от BookDTO към BookEntity.
    private BookEntity mapToEntity(BookDTO book) {
        if (book == null) return null; // Ако DTO е null, връщаме null.
        BookEntity entity = new BookEntity();
        entity.setId(book.getId()); // Задаваме ID на книгата.
        entity.setTitle(book.getTitle()); // Задаваме заглавие.
        entity.setIsbn(book.getIsbn()); // Задаваме ISBN на книгата.
        if (book.getAuthor() != null) {
            entity.setAuthor(new AuthorEntity().setName(book.getAuthor().getName())); // Преобразуваме автора в AuthorEntity.
        }
        return entity;
    }

    @Test
    void testMapToEntity() {
        // Създаваме тестови данни за DTO.
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(1L); // Задаваме ID на DTO.
        bookDTO.setTitle("Test Book"); // Задаваме заглавие.
        bookDTO.setIsbn("123456789"); // Задаваме ISBN.

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("John Doe"); // Задаваме име на автора в DTO.
        bookDTO.setAuthor(authorDTO); // Добавяме автора към DTO.

        // Мапираме към Entity.
        BookEntity bookEntity = mapToEntity(bookDTO);

        // Проверяваме дали стойностите са правилно мапирани.
        assertNotNull(bookEntity); // Проверяваме дали книгата не е null.
        assertEquals(1L, bookEntity.getId()); // Проверяваме ID.
        assertEquals("Test Book", bookEntity.getTitle()); // Проверяваме заглавието.
        assertEquals("123456789", bookEntity.getIsbn()); // Проверяваме ISBN.
        assertNotNull(bookEntity.getAuthor()); // Проверяваме дали авторът е зададен.
        assertEquals("John Doe", bookEntity.getAuthor().getName()); // Проверяваме името на автора.
    }

    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(bookEntity)); // Мокваме метода findAll да върне списък с bookEntity.

        List<BookDTO> result = bookService.getAllBooks(); // Извикваме метода getAllBooks.

        assertEquals(1, result.size()); // Проверяваме дали списъкът съдържа точно една книга.
        assertEquals("Test Book", result.get(0).getTitle()); // Проверяваме дали книгата има правилно заглавие.
        verify(bookRepository, times(1)).findAll(); // Проверяваме дали методът findAll е извикан точно веднъж.
    }

    @Test
    void testFindBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookEntity)); // Мокваме метода findById да върне bookEntity.

        Optional<BookDTO> result = bookService.findBookById(1L); // Извикваме метода findBookById.

        assertTrue(result.isPresent()); // Проверяваме дали резултатът не е празен.
        assertEquals("Test Book", result.get().getTitle()); // Проверяваме заглавието на книгата.
        verify(bookRepository, times(1)).findById(1L); // Проверяваме дали методът findById е извикан точно веднъж.
    }

    @Test
    void testFindBookById_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty()); // Мокваме метода findById да върне Optional.empty() (не намира книга).

        Optional<BookDTO> result = bookService.findBookById(1L); // Извикваме метода findBookById.

        assertFalse(result.isPresent()); // Проверяваме дали резултатът е празен (няма намерена книга).
        verify(bookRepository, times(1)).findById(1L); // Проверяваме дали методът findById е извикан точно веднъж.
    }

    @Test
    void testCreateBook_NewAuthor() {
        when(authorRepository.findByName("John Doe")).thenReturn(Optional.empty()); // Мокваме, че авторът не съществува в базата.
        when(authorRepository.save(any())).thenReturn(authorEntity); // Мокваме записа на нов автор.
        when(bookRepository.save(any())).thenReturn(bookEntity); // Мокваме записа на нова книга.

        BookDTO result = bookService.createBook(mapToDTO(bookEntity)); // Извикваме метода за създаване на книга.

        assertNotNull(result); // Проверяваме дали резултатът не е null.
        assertEquals("Test Book", result.getTitle()); // Проверяваме заглавието на книгата.
        verify(authorRepository, times(1)).findByName("John Doe"); // Проверяваме дали е извикана проверка за автора.
        verify(authorRepository, times(1)).save(any()); // Проверяваме дали е извикан методът за запис на автора.
        verify(bookRepository, times(1)).save(any()); // Проверяваме дали е извикан методът за запис на книгата.
    }

    @Test
    void testCreateBook_ExistingAuthor() {
        when(authorRepository.findByName("John Doe")).thenReturn(Optional.of(authorEntity)); // Мокваме, че авторът вече съществува.
        when(bookRepository.save(any())).thenReturn(bookEntity); // Мокваме записа на книга.

        BookDTO result = bookService.createBook(mapToDTO(bookEntity)); // Извикваме метода за създаване на книга.

        assertNotNull(result); // Проверяваме дали резултатът не е null.
        assertEquals("Test Book", result.getTitle()); // Проверяваме заглавието на книгата.
        verify(authorRepository, times(1)).findByName("John Doe"); // Проверяваме дали е извикана проверка за автора.
        verify(bookRepository, times(1)).save(any()); // Проверяваме дали е извикан методът за запис на книгата.
    }

    @Test
    void testUpdateBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookEntity)); // Мокваме метода findById да върне книга.
        when(authorRepository.findByName("John Doe")).thenReturn(Optional.of(authorEntity)); // Мокваме метода findByName да върне автор.
        when(bookRepository.save(any())).thenReturn(bookEntity); // Мокваме записа на книга.

        Optional<BookDTO> result = bookService.updateBook(1L, mapToDTO(bookEntity)); // Извикваме метода за актуализиране на книга.

        assertTrue(result.isPresent()); // Проверяваме дали резултатът не е празен.
        assertEquals("Test Book", result.get().getTitle()); // Проверяваме заглавието на книгата.
        verify(bookRepository, times(1)).findById(1L); // Проверяваме дали е извикан методът findById.
        verify(bookRepository, times(1)).save(any()); // Проверяваме дали е извикан методът за записа на книгата.
    }

    @Test
    void testUpdateBook_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty()); // Мокваме метода findById да не намери книга.

        Optional<BookDTO> result = bookService.updateBook(1L, mapToDTO(bookEntity)); // Извикваме метода за актуализиране на книга.

        assertFalse(result.isPresent()); // Проверяваме дали резултатът е празен.
        verify(bookRepository, times(1)).findById(1L); // Проверяваме дали е извикан методът findById.
        verify(bookRepository, never()).save(any()); // Проверяваме, че методът save не е извикан.
    }

    @Test
    void testDeleteBook() {
        doNothing().when(bookRepository).deleteById(1L); // Мокваме метода deleteById да не прави нищо.

        bookService.deleteBookById(1L); // Извикваме метода за изтриване на книга.

        verify(bookRepository, times(1)).deleteById(1L); // Проверяваме дали методът deleteById е извикан точно веднъж.
    }

    @Test
    void testDeleteBook_NotFound() {
        // Симулираме, че книгата с id 2 не е намерена
        doNothing().when(bookRepository).deleteById(2L); // Мокваме метода deleteById да не прави нищо.

        // Извикваме метода deleteBookById
        bookService.deleteBookById(2L);

        // Потвърждаваме, че методът deleteById е извикан веднъж (дори ако не е намерена книга)
        verify(bookRepository, times(1)).deleteById(2L); // Проверяваме дали методът е извикан веднъж.
    }

}
