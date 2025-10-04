package com.fiap.project_bookstore_mvc.services;

import com.fiap.project_bookstore_mvc.entities.Author;
import com.fiap.project_bookstore_mvc.entities.Book;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AuthorService {
    Page<Author> findAll(int page, int size);

    Author findById(Long id);

    Author saveOrUpdate(Author author);

    List<Book> findBooksByAuthorId(Long authorId);

    void delete(Long id);
}
