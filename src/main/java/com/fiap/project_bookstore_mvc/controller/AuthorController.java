package com.fiap.project_bookstore_mvc.controller;

import com.fiap.project_bookstore_mvc.dto.In.AuthorInDTO;
import com.fiap.project_bookstore_mvc.dto.Out.AuthorOutDTO;
import com.fiap.project_bookstore_mvc.dto.Out.BookOutDTO;
import com.fiap.project_bookstore_mvc.entities.Author;
import com.fiap.project_bookstore_mvc.entities.Book;
import com.fiap.project_bookstore_mvc.mappers.AuthorMapper;
import com.fiap.project_bookstore_mvc.mappers.BookMapper;
import com.fiap.project_bookstore_mvc.services.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<Page<AuthorOutDTO>> findAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Page<Author> authors = authorService.findAll(page, size);
        Page<AuthorOutDTO> outDTO = authors.map(AuthorMapper::toOutDTO);

        return ResponseEntity.ok(outDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorOutDTO> findById(@PathVariable Long id) {
        Author author = authorService.findById(id);

        AuthorOutDTO outDTO = AuthorMapper.toOutDTO(author);
        return ResponseEntity.ok(outDTO);
    }

    @PostMapping
    public ResponseEntity<AuthorOutDTO> create(@RequestBody AuthorInDTO author) {
        Author entity = AuthorMapper.toEntity(author);
        Author savedAuthor = authorService.saveOrUpdate(entity);
        AuthorOutDTO outDTO = AuthorMapper.toOutDTO(savedAuthor);
        return ResponseEntity.status(HttpStatus.CREATED).body(outDTO);
    }

    @PutMapping
    public ResponseEntity<AuthorOutDTO> update(@RequestBody AuthorInDTO author) {
        Author entity = AuthorMapper.toEntity(author);
        Author updatedAuthor = authorService.saveOrUpdate(entity);
        AuthorOutDTO outDTO = AuthorMapper.toOutDTO(updatedAuthor);
        return ResponseEntity.ok(outDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<List<BookOutDTO>> findBooksByAuthorId(@PathVariable Long id) {
        List<Book> books = authorService.findBooksByAuthorId(id);

        List<BookOutDTO> outDTO = books.stream().map(BookMapper::toOutDTO).toList();

        return ResponseEntity.ok(outDTO);
    }
}
