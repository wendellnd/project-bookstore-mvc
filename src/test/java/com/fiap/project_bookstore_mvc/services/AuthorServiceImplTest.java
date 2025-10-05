package com.fiap.project_bookstore_mvc.services;

import com.fiap.project_bookstore_mvc.entities.Author;
import com.fiap.project_bookstore_mvc.entities.Book;
import com.fiap.project_bookstore_mvc.exceptions.EntityNotFound;
import com.fiap.project_bookstore_mvc.repositories.AuthorRepository;
import com.fiap.project_bookstore_mvc.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthorServiceImplTest {

    private AuthorService authorService;
    private AuthorRepository authorRepository;
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        this.authorRepository = mock(AuthorRepository.class);
        this.bookRepository = mock(BookRepository.class);
        this.authorService = new AuthorServiceImpl(authorRepository, bookRepository);
    }

    @Test
    void findById_returnsThrowWhenNotFound() {
        when(this.authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.authorService.findById(1L)).isInstanceOf(EntityNotFound.class);
    }

    @Test
    void findById_returnsAuthorWhenFound() {
        Author author = new Author(1L, "Test Author", "test@example.com");

        when(this.authorRepository.findById(1L)).thenReturn(Optional.of(author));

        final Author found = this.authorService.findById(1L);
        verify(this.authorRepository, times(1)).findById(any());
        assertThat(found).isSameAs(author);
    }

    @Test
    void saveOrUpdate_savesAuthorSuccessfully() {
        Author author = new Author(null, "New Author", "new@example.com");
        Author savedAuthor = new Author(2L, "New Author", "new@example.com");

        when(this.authorRepository.save(author)).thenReturn(savedAuthor);

        final Author result = this.authorService.saveOrUpdate(author);
        verify(this.authorRepository, times(1)).save(author);
        assertThat(result).isSameAs(savedAuthor);
    }

    @Test
    void delete_deletesAuthorSuccessfully() {
        Author author = new Author(1L, "Test Author", "test@example.com");

        when(this.authorRepository.findById(1L)).thenReturn(Optional.of(author));
        this.authorService.delete(1L);

        verify(this.authorRepository, times(1)).findById(1L);
        verify(this.authorRepository, times(1)).delete(author);
    }

    @Test
    void delete_throwsExceptionWhenAuthorNotFound() {
        when(this.authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.authorService.delete(1L)).isInstanceOf(EntityNotFound.class);
    }

    @Test
    void findBooksByAuthorId_returnsBooksList() {
        Author author = new Author(1L, "Test Author", "test@example.com");
        Book book1 = new Book(1L, "Book 1", "978-0123456789", author);
        Book book2 = new Book(2L, "Book 2", "978-0987654321", author);
        List<Book> books = Arrays.asList(book1, book2);
        author.setBooks(books);

        when(this.authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(this.bookRepository.findByAuthor(author)).thenReturn(books);

        final List<Book> foundBooks = this.authorService.findBooksByAuthorId(1L);
        verify(this.authorRepository, times(1)).findById(1L);
        assertThat(foundBooks).hasSize(2);
        assertThat(foundBooks).containsExactly(book1, book2);
    }

    @Test
    void findBooksByAuthorId_throwsExceptionWhenAuthorNotFound() {
        when(this.authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.authorService.findBooksByAuthorId(1L)).isInstanceOf(EntityNotFound.class);
    }

    @Test
    void findAll_returnsPagedAuthors() {
        Author author1 = new Author(1L, "Author 1", "author1@example.com");
        Author author2 = new Author(2L, "Author 2", "author2@example.com");
        List<Author> authors = Arrays.asList(author1, author2);
        Page<Author> authorPage = new PageImpl<>(authors);
        Pageable pageable = PageRequest.of(0, 10);

        when(this.authorRepository.findAll(pageable)).thenReturn(authorPage);

        final Page<Author> result = this.authorService.findAll(0, 10);
        verify(this.authorRepository, times(1)).findAll(any(Pageable.class));
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(author1, author2);
    }
}
