package bg.home.books.web;

import bg.home.books.model.entity.dto.BookDTO;
import bg.home.books.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

// Разрешаваме Cross-Origin заявки от всички източници (*), което позволява достъп до API-то от различни домейни
@CrossOrigin(origins = "*")
// Маркираме този клас като REST контролер, така че Spring да го разпознае като API контролер
@RestController
// Определяме базовия URL за всички заявки към този контролер
@RequestMapping("api/books")
public class BooksRestController {

    private final BookService bookService;

    // Конструктор с инжектиране на BookService, който обработва логиката за книги
    public BooksRestController(BookService bookService) {
        this.bookService = bookService;
    }

    // Зареждане на всички книги
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks()); // Връщаме списък с всички книги със статус 200 OK
    }

    // Търсене на книга по ID
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> findBookById(@PathVariable("id") Long id) {
        Optional<BookDTO> bookDTOOptional = bookService.findBookById(id);

        return bookDTOOptional
                .map(ResponseEntity::ok) // Ако книгата съществува, връщаме я със статус 200 OK
                .orElse(ResponseEntity.notFound().build()); // Ако не съществува, връщаме 404 Not Found
    }

    // Изтриване на книга по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable("id") Long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build(); // Връщаме 204 No Content след успешното изтриване
    }

    // Обновяване на книга по ID
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        Optional<BookDTO> updatedBook = bookService.updateBook(id, bookDTO);

        return updatedBook
                .map(ResponseEntity::ok)  // Ако книгата е обновена, връщаме 200 OK с обновените данни
                .orElseGet(() -> ResponseEntity.notFound().build());  // Ако книгата не съществува, връщаме 404 Not Found
    }

    // Създаване на нова книга
    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        BookDTO createdBook = bookService.createBook(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook); // Връщаме 201 Created с новата книга
    }
}
