package com.fiap.project_bookstore_mvc.repositories;

import com.fiap.project_bookstore_mvc.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

}
