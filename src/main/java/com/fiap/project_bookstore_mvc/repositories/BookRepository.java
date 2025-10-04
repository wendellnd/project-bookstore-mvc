package com.fiap.project_bookstore_mvc.repositories;

import com.fiap.project_bookstore_mvc.entities.Author;
import com.fiap.project_bookstore_mvc.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
