package com.fiap.project_bookstore_mvc.controller;

import com.fiap.project_bookstore_mvc.configs.GlobalExceptionHandler;
import com.fiap.project_bookstore_mvc.entities.Author;
import com.fiap.project_bookstore_mvc.entities.Book;
import com.fiap.project_bookstore_mvc.exceptions.EntityNotFound;
import com.fiap.project_bookstore_mvc.services.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BookService bookService;

    @Test
    @WithMockUser
    void getById_returnBookWhenFound() throws Exception {
        Author author = new Author(1L, "Test Author", "test@example.com");
        Book book = new Book(1L, "Test Book", "978-0123456789", author);

        when(this.bookService.findById(1L)).thenReturn(book);

        this.mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Book")))
                .andExpect(jsonPath("$.isbn", is("978-0123456789")))
                .andExpect(jsonPath("$.author.id", is(1)))
                .andExpect(jsonPath("$.author.name", is("Test Author")))
                .andExpect(jsonPath("$.author.email", is("test@example.com")));
    }

    @Test
    @WithMockUser
    void findAll_returnPagedBooks() throws Exception {
        Author author = new Author(1L, "Test Author", "test@example.com");
        Book book1 = new Book(1L, "Book 1", "978-0123456789", author);
        Book book2 = new Book(2L, "Book 2", "978-0987654321", author);
        List<Book> books = Arrays.asList(book1, book2);
        Page<Book> bookPage = new PageImpl<>(books);

        when(this.bookService.findAll(0, 10)).thenReturn(bookPage);

        this.mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Book 1")))
                .andExpect(jsonPath("$.content[0].isbn", is("978-0123456789")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].title", is("Book 2")))
                .andExpect(jsonPath("$.content[1].isbn", is("978-0987654321")));
    }

    @Test
    @WithMockUser
    void create_returnCreatedBook() throws Exception {
        Author author = new Author(1L, "Test Author", "test@example.com");
        Book savedBook = new Book(1L, "New Book", "978-0111111111", author);

        when(this.bookService.saveOrUpdate(any(Book.class))).thenReturn(savedBook);

        this.mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "New Book",
                                    "isbn": "978-0111111111",
                                    "authorId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("New Book")))
                .andExpect(jsonPath("$.isbn", is("978-0111111111")))
                .andExpect(jsonPath("$.author.id", is(1)))
                .andExpect(jsonPath("$.author.name", is("Test Author")));
    }

    @Test
    @WithMockUser
    void update_returnUpdatedBook() throws Exception {
        Author author = new Author(1L, "Test Author", "test@example.com");
        Book updatedBook = new Book(1L, "Updated Book", "978-0222222222", author);

        when(this.bookService.saveOrUpdate(any(Book.class))).thenReturn(updatedBook);

        this.mockMvc.perform(put("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "title": "Updated Book",
                                    "isbn": "978-0222222222",
                                    "authorId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Book")))
                .andExpect(jsonPath("$.isbn", is("978-0222222222")))
                .andExpect(jsonPath("$.author.id", is(1)))
                .andExpect(jsonPath("$.author.name", is("Test Author")));
    }

    @Test
    @WithMockUser
    void delete_returnNoContent() throws Exception {
        this.mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void getById_returnNotFoundWhenBookNotExists() throws Exception {
        when(this.bookService.findById(999L)).thenThrow(new EntityNotFound("Book not found with id: 999"));

        this.mockMvc.perform(get("/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void delete_returnNotFoundWhenBookNotExists() throws Exception {
        doThrow(new EntityNotFound("Book not found with id: 999")).when(this.bookService).delete(999L);

        this.mockMvc.perform(delete("/books/999"))
                .andExpect(status().isNotFound());
    }
}
