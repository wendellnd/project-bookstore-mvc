package com.fiap.project_bookstore_mvc.services;

import com.fiap.project_bookstore_mvc.entities.Author;
import com.fiap.project_bookstore_mvc.entities.Book;
import com.fiap.project_bookstore_mvc.exceptions.EntityNotFound;
import com.fiap.project_bookstore_mvc.repositories.AuthorRepository;
import com.fiap.project_bookstore_mvc.repositories.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public Page<Author> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return authorRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Author findById(Long id) {
        return authorRepository.findById(id).orElseThrow(() ->  new EntityNotFound("Author not found"));
    }

    @Override
    public Author saveOrUpdate(Author author) {
        return authorRepository.save(author);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Author not found"));

        authorRepository.delete(author);
    }

    @Override
    public List<Book> findBooksByAuthorId(Long authorId) {
        Author author = authorRepository.findById(authorId).orElseThrow(() -> new EntityNotFound("Author not found"));

        return author.getBooks();
    }
}
