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
class AuthorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void createAuthor_shouldPersistAndReturnAuthor() throws Exception {
        this.mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Integration Test Author",
                                    "email": "integration@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Integration Test Author")))
                .andExpect(jsonPath("$.email", is("integration@example.com")));

        // Verify data was actually persisted in database
        var authors = authorRepository.findAll();
        assert authors.size() == 1;
        assert authors.get(0).getName().equals("Integration Test Author");
    }

    @Test
    @WithMockUser
    void getAuthorById_shouldReturnPersistedAuthor() throws Exception {
        // Given - persist an author directly to database
        Author author = new Author(null, "Persisted Author", "persisted@example.com");
        Author savedAuthor = authorRepository.save(author);

        // When & Then - retrieve via REST API
        this.mockMvc.perform(get("/authors/" + savedAuthor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedAuthor.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Persisted Author")))
                .andExpect(jsonPath("$.email", is("persisted@example.com")));
    }

    @Test
    @WithMockUser
    void updateAuthor_shouldModifyPersistedData() throws Exception {
        // Given - persist an author
        Author author = new Author(null, "Original Name", "original@example.com");
        Author savedAuthor = authorRepository.save(author);

        // When - update via REST API
        this.mockMvc.perform(put("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": %d,
                                    "name": "Updated Name",
                                    "email": "updated@example.com"
                                }
                                """.formatted(savedAuthor.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        // Then - verify changes persisted in database
        Author updatedAuthor = authorRepository.findById(savedAuthor.getId()).orElseThrow();
        assert updatedAuthor.getName().equals("Updated Name");
        assert updatedAuthor.getEmail().equals("updated@example.com");
    }

    @Test
    @WithMockUser
    void deleteAuthor_shouldRemoveFromDatabase() throws Exception {
        // Given - persist an author
        Author author = new Author(null, "To Be Deleted", "delete@example.com");
        Author savedAuthor = authorRepository.save(author);

        // When - delete via REST API
        this.mockMvc.perform(delete("/authors/" + savedAuthor.getId()))
                .andExpect(status().isNoContent());

        // Then - verify removed from database
        assert authorRepository.findById(savedAuthor.getId()).isEmpty();
    }

    @Test
    @WithMockUser
    void findAllAuthors_shouldReturnPagedResults() throws Exception {
        // Given - persist multiple authors
        Author author1 = new Author(null, "Author 1", "author1@example.com");
        Author author2 = new Author(null, "Author 2", "author2@example.com");
        Author author3 = new Author(null, "Author 3", "author3@example.com");

        authorRepository.save(author1);
        authorRepository.save(author2);
        authorRepository.save(author3);

        // When & Then - retrieve via REST API with pagination
        this.mockMvc.perform(get("/authors")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(2)));
    }

    @Test
    @WithMockUser
    void findBooksByAuthorId_shouldReturnAuthorBooks() throws Exception {
        // Given - persist author and books
        Author author = new Author(null, "Book Author", "bookauthor@example.com");
        Author savedAuthor = authorRepository.save(author);

        Book book1 = new Book(null, "Book 1", "978-1111111111", savedAuthor);
        Book book2 = new Book(null, "Book 2", "978-2222222222", savedAuthor);

        bookRepository.save(book1);
        bookRepository.save(book2);

        // When & Then - retrieve author's books via REST API
        this.mockMvc.perform(get("/authors/" + savedAuthor.getId() + "/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Book 1")))
                .andExpect(jsonPath("$[1].title", is("Book 2")));
    }

    @Test
    @WithMockUser
    void getAuthorById_shouldReturn404WhenNotFound() throws Exception {
        this.mockMvc.perform(get("/authors/999"))
                .andExpect(status().isNotFound());
    }
}
