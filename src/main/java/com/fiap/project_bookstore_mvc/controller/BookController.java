package com.fiap.project_bookstore_mvc.controller;

import com.fiap.project_bookstore_mvc.dto.In.BookInDTO;
import com.fiap.project_bookstore_mvc.dto.Out.BookOutDTO;
import com.fiap.project_bookstore_mvc.entities.Book;
import com.fiap.project_bookstore_mvc.mappers.BookMapper;
import com.fiap.project_bookstore_mvc.services.BookService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Page<BookOutDTO>> findAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Page<Book> books = bookService.findAll(page, size);
        Page<BookOutDTO> outDTO = books.map(BookMapper::toOutDTO);

        return ResponseEntity.ok(outDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookOutDTO> findById(@PathVariable Long id) {
        Book book = bookService.findById(id);

        BookOutDTO outDTO = BookMapper.toOutDTO(book);
        return ResponseEntity.ok(outDTO);
    }

    @PostMapping
    public ResponseEntity<BookOutDTO> create(@RequestBody BookInDTO bookInDTO) {
        Book book = BookMapper.toEntity(bookInDTO);
        Book savedBook = bookService.saveOrUpdate(book);
        BookOutDTO outDTO = BookMapper.toOutDTO(savedBook);
        return ResponseEntity.status(HttpStatus.CREATED).body(outDTO);
    }

    @PutMapping
    public ResponseEntity<BookOutDTO> update(@RequestBody BookInDTO bookInDTO) {
        Book book = BookMapper.toEntity(bookInDTO);

        Book updatedBook = bookService.saveOrUpdate(book);
        BookOutDTO outDTO = BookMapper.toOutDTO(updatedBook);
        return ResponseEntity.ok(outDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
