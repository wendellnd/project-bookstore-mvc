package com.fiap.project_bookstore_mvc.services;

import com.fiap.project_bookstore_mvc.entities.Book;
import com.fiap.project_bookstore_mvc.exceptions.EntityNotFound;
import com.fiap.project_bookstore_mvc.repositories.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    public BookServiceImpl(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    @Override
    public Page<Book> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Book not found"));
    }

    @Override
    public Book saveOrUpdate(Book book) {
        if (book.getAuthor() != null && book.getAuthor().getId() != null) {
            var author = authorService.findById(book.getAuthor().getId());
            book.setAuthor(author);
        }
        return bookRepository.save(book);
    }

    @Override
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}
