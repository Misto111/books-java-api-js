package bg.home.books.web;

import bg.home.books.model.entity.dto.BookDTO;
import bg.home.books.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final BookService bookService;

    public HomeController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String home(Model model) {
        List<BookDTO> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "index";
    }
}
