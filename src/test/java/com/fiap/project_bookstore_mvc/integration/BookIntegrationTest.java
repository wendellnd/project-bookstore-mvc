package com.fiap.project_bookstore_mvc.integration;

import com.fiap.project_bookstore_mvc.entities.Author;
import com.fiap.project_bookstore_mvc.entities.Book;
import com.fiap.project_bookstore_mvc.repositories.AuthorRepository;
import com.fiap.project_bookstore_mvc.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void createBook_shouldPersistAndReturnBook() throws Exception {
        // Given - create an author first
        Author author = new Author(null, "Test Author", "test@example.com");
        Author savedAuthor = authorRepository.save(author);

        // When & Then - create book via REST API
        this.mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Integration Test Book",
                                    "isbn": "978-0123456789",
                                    "authorId": %d
                                }
                                """.formatted(savedAuthor.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title", is("Integration Test Book")))
                .andExpect(jsonPath("$.isbn", is("978-0123456789")))
                .andExpect(jsonPath("$.author.id", is(savedAuthor.getId().intValue())))
                .andExpect(jsonPath("$.author.name", is("Test Author")));

        // Verify data was actually persisted in database
        var books = bookRepository.findAll();
        assert books.size() == 1;
        assert books.getFirst().getTitle().equals("Integration Test Book");
        assert books.getFirst().getAuthor().getId().equals(savedAuthor.getId());
    }

    @Test
    @WithMockUser
    void getBookById_shouldReturnPersistedBook() throws Exception {
        // Given - persist author and book directly to database
        Author author = new Author(null, "Book Author", "author@example.com");
        Author savedAuthor = authorRepository.save(author);

        Book book = new Book(null, "Persisted Book", "978-9876543210", savedAuthor);
        Book savedBook = bookRepository.save(book);

        // When & Then - retrieve via REST API
        this.mockMvc.perform(get("/books/" + savedBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedBook.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Persisted Book")))
                .andExpect(jsonPath("$.isbn", is("978-9876543210")))
                .andExpect(jsonPath("$.author.id", is(savedAuthor.getId().intValue())))
                .andExpect(jsonPath("$.author.name", is("Book Author")));
    }

    @Test
    @WithMockUser
    void updateBook_shouldModifyPersistedData() throws Exception {
        // Given - persist author and book
        Author author = new Author(null, "Original Author", "original@example.com");
        Author savedAuthor = authorRepository.save(author);

        Book book = new Book(null, "Original Title", "978-1111111111", savedAuthor);
        Book savedBook = bookRepository.save(book);

        // When - update via REST API
        this.mockMvc.perform(put("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": %d,
                                    "title": "Updated Title",
                                    "isbn": "978-2222222222",
                                    "authorId": %d
                                }
                                """.formatted(savedBook.getId(), savedAuthor.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.isbn", is("978-2222222222")));

        // Then - verify changes persisted in database
        Book updatedBook = bookRepository.findById(savedBook.getId()).orElseThrow();
        assert updatedBook.getTitle().equals("Updated Title");
        assert updatedBook.getIsbn().equals("978-2222222222");
    }

    @Test
    @WithMockUser
    void deleteBook_shouldRemoveFromDatabase() throws Exception {
        // Given - persist author and book
        Author author = new Author(null, "Author", "author@example.com");
        Author savedAuthor = authorRepository.save(author);

        Book book = new Book(null, "To Be Deleted", "978-3333333333", savedAuthor);
        Book savedBook = bookRepository.save(book);

        // When - delete via REST API
        this.mockMvc.perform(delete("/books/" + savedBook.getId()))
                .andExpect(status().isNoContent());

        // Then - verify removed from database
        assert bookRepository.findById(savedBook.getId()).isEmpty();
    }

    @Test
    @WithMockUser
    void findAllBooks_shouldReturnPagedResults() throws Exception {
        // Given - persist author and multiple books
        Author author = new Author(null, "Prolific Author", "prolific@example.com");
        Author savedAuthor = authorRepository.save(author);

        Book book1 = new Book(null, "Book 1", "978-1111111111", savedAuthor);
        Book book2 = new Book(null, "Book 2", "978-2222222222", savedAuthor);
        Book book3 = new Book(null, "Book 3", "978-3333333333", savedAuthor);

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        // When & Then - retrieve via REST API with pagination
        this.mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(2)));
    }

    @Test
    @WithMockUser
    void getBookById_shouldReturn404WhenNotFound() throws Exception {
        this.mockMvc.perform(get("/books/999"))
                .andExpect(status().isNotFound());
    }
}
