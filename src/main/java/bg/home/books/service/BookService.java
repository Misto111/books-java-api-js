package bg.home.books.service;

import bg.home.books.model.entity.dto.BookDTO;
import java.util.List;
import java.util.Optional;

public interface BookService {

    List<BookDTO> getAllBooks();

    Optional<BookDTO> findBookById(Long id);

    BookDTO createBook(BookDTO bookDTO);

    Optional<BookDTO> updateBook(Long id, BookDTO bookDTO);

    void deleteBookById(Long id);
}
