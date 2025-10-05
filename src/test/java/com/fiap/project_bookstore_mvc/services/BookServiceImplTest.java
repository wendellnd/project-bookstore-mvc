package com.fiap.project_bookstore_mvc.services;

import com.fiap.project_bookstore_mvc.entities.Author;
import com.fiap.project_bookstore_mvc.entities.Book;
import com.fiap.project_bookstore_mvc.exceptions.EntityNotFound;
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

class BookServiceImplTest {

    private BookService bookService;
    private BookRepository bookRepository;
    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        this.bookRepository = mock(BookRepository.class);
        this.authorService = mock(AuthorService.class);
        this.bookService = new BookServiceImpl(bookRepository, authorService);
    }

    @Test
    void findById_returnsThrowWhenNotFound() {
        when(this.bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.bookService.findById(1L)).isInstanceOf(EntityNotFound.class);
    }

    @Test
    void findById_returnsBookWhenFound() {
        Author author = new Author(1L, "Test Author", "test@example.com");
        Book book = new Book(1L, "Test Book", "978-0123456789", author);

        when(this.bookRepository.findById(1L)).thenReturn(Optional.of(book));

        final Book found = this.bookService.findById(1L);
        verify(this.bookRepository, times(1)).findById(any());
        assertThat(found).isSameAs(book);
    }

    @Test
    void saveOrUpdate_savesBookSuccessfully() {
        Author author = new Author(1L, "Test Author", "test@example.com");
        Book book = new Book(null, "New Book", "978-0987654321", author);
        Book savedBook = new Book(2L, "New Book", "978-0987654321", author);

        when(this.authorService.findById(1L)).thenReturn(author);
        when(this.bookRepository.save(book)).thenReturn(savedBook);

        final Book result = this.bookService.saveOrUpdate(book);
        verify(this.authorService, times(1)).findById(1L);
        verify(this.bookRepository, times(1)).save(book);
        assertThat(result).isSameAs(savedBook);
    }

    @Test
    void delete_deletesBookSuccessfully() {
        this.bookService.delete(1L);

        verify(this.bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void findAll_returnsPagedBooks() {
        Author author = new Author(1L, "Test Author", "test@example.com");
        Book book1 = new Book(1L, "Book 1", "978-0123456789", author);
        Book book2 = new Book(2L, "Book 2", "978-0987654321", author);
        List<Book> books = Arrays.asList(book1, book2);
        Page<Book> bookPage = new PageImpl<>(books);
        Pageable pageable = PageRequest.of(0, 10);

        when(this.bookRepository.findAll(pageable)).thenReturn(bookPage);

        final Page<Book> result = this.bookService.findAll(0, 10);
        verify(this.bookRepository, times(1)).findAll(any(Pageable.class));
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(book1, book2);
    }
}
