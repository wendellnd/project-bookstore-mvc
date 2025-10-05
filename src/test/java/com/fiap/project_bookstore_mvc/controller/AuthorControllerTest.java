package com.fiap.project_bookstore_mvc.controller;

import com.fiap.project_bookstore_mvc.configs.GlobalExceptionHandler;
import com.fiap.project_bookstore_mvc.entities.Author;
import com.fiap.project_bookstore_mvc.entities.Book;
import com.fiap.project_bookstore_mvc.exceptions.EntityNotFound;
import com.fiap.project_bookstore_mvc.services.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AuthorService authorService;

    @Test
    void getById_returnAuthorWhenFound() throws Exception {
        Author author = new Author(1L, "Test Author", "test@example.com");

        when(this.authorService.findById(1L)).thenReturn(author);

        this.mockMvc.perform(get("/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Author")))
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    void findAll_returnPagedAuthors() throws Exception {
        Author author1 = new Author(1L, "Author 1", "author1@example.com");
        Author author2 = new Author(2L, "Author 2", "author2@example.com");
        List<Author> authors = Arrays.asList(author1, author2);
        Page<Author> authorPage = new PageImpl<>(authors);

        when(this.authorService.findAll(0, 10)).thenReturn(authorPage);

        this.mockMvc.perform(get("/authors")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Author 1")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].name", is("Author 2")));
    }

    @Test
    void create_returnCreatedAuthor() throws Exception {
        Author savedAuthor = new Author(1L, "New Author", "new@example.com");

        when(this.authorService.saveOrUpdate(any(Author.class))).thenReturn(savedAuthor);

        this.mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "New Author",
                                    "email": "new@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New Author")))
                .andExpect(jsonPath("$.email", is("new@example.com")));
    }

    @Test
    void update_returnUpdatedAuthor() throws Exception {
        Author updatedAuthor = new Author(1L, "Updated Author", "updated@example.com");

        when(this.authorService.saveOrUpdate(any(Author.class))).thenReturn(updatedAuthor);

        this.mockMvc.perform(put("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "name": "Updated Author",
                                    "email": "updated@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Author")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));
    }

    @Test
    void delete_returnNoContent() throws Exception {
        this.mockMvc.perform(delete("/authors/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findBooksByAuthorId_returnBooksList() throws Exception {
        Author author = new Author(1L, "Test Author", "test@example.com");
        Book book1 = new Book(1L, "Book 1", "978-0123456789", author);
        Book book2 = new Book(2L, "Book 2", "978-0987654321", author);
        List<Book> books = Arrays.asList(book1, book2);

        when(this.authorService.findBooksByAuthorId(1L)).thenReturn(books);

        this.mockMvc.perform(get("/authors/1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Book 1")))
                .andExpect(jsonPath("$[0].isbn", is("978-0123456789")))
                .andExpect(jsonPath("$[0].author.id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Book 2")));
    }

    @Test
    void getById_returnNotFoundWhenAuthorNotExists() throws Exception {
        when(this.authorService.findById(999L)).thenThrow(new EntityNotFound("Author not found with id: 999"));

        this.mockMvc.perform(get("/authors/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findBooksByAuthorId_returnNotFoundWhenAuthorNotExists() throws Exception {
        when(this.authorService.findBooksByAuthorId(999L)).thenThrow(new EntityNotFound("Author not found with id: 999"));

        this.mockMvc.perform(get("/authors/999/books"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returnNotFoundWhenAuthorNotExists() throws Exception {
        doThrow(new EntityNotFound("Author not found with id: 999")).when(this.authorService).delete(999L);

        this.mockMvc.perform(delete("/authors/999"))
                .andExpect(status().isNotFound());
    }
}
