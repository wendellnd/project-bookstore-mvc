package com.fiap.project_bookstore_mvc.repositories;

import com.fiap.project_bookstore_mvc.entities.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AuthorRepositoryTest {

    @Autowired
    AuthorRepository authorRepository;

    @Test
    void save_shouldPersistAuthorWhenValidAndReturnWhenFindById() {
        Author author = new Author(null, "Test Author", "test@example.com");

        Author saved = this.authorRepository.save(author);
        assertThat(saved.getId()).isNotNull();

        Author found = this.authorRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(author.getName());
        assertThat(found.getEmail()).isEqualTo(author.getEmail());
    }
}
