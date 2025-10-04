package com.fiap.project_bookstore_mvc.services;

import com.fiap.project_bookstore_mvc.entities.Book;
import org.springframework.data.domain.Page;

public interface BookService {
    Page<Book> findAll(int page, int size);

    Book findById(Long id);

    Book saveOrUpdate(Book book);

    void delete(Long id);
}
