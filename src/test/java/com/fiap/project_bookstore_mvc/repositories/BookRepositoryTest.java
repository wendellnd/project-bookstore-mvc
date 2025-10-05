package com.fiap.project_bookstore_mvc.repositories;

import com.fiap.project_bookstore_mvc.entities.Author;
import com.fiap.project_bookstore_mvc.entities.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Test
    void save_shouldPersistBookWhenValidAndReturnWhenFindById() {
        // First create and save an author
        Author author = new Author(null, "Test Author", "test@example.com");
        Author savedAuthor = this.authorRepository.save(author);

        // Create a book with the saved author
        Book book = new Book(null, "Test Book", "978-0123456789", savedAuthor);

        Book saved = this.bookRepository.save(book);
        assertThat(saved.getId()).isNotNull();

        Book found = this.bookRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo(book.getTitle());
        assertThat(found.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(found.getAuthor().getName()).isEqualTo(savedAuthor.getName());
    }
}
