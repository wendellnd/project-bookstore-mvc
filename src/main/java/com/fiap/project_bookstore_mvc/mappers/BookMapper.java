package com.fiap.project_bookstore_mvc.mappers;

import com.fiap.project_bookstore_mvc.dto.In.BookInDTO;
import com.fiap.project_bookstore_mvc.dto.Out.AuthorOutDTO;
import com.fiap.project_bookstore_mvc.dto.Out.BookOutDTO;
import com.fiap.project_bookstore_mvc.entities.Author;
import com.fiap.project_bookstore_mvc.entities.Book;

public class BookMapper {

    public static BookOutDTO toOutDTO(Book book) {
        AuthorOutDTO author = AuthorMapper.toOutDTO(book.getAuthor());

        return new BookOutDTO(
            book.getId(),
            book.getTitle(),
            book.getIsbn(),
                author
        );
    }

    public static Book toEntity(BookInDTO bookInDTO) {
        Author author = new Author(bookInDTO.authorId());

        return new Book(
                bookInDTO.id(),
                bookInDTO.title(),
                bookInDTO.isbn(),
                author
        );
    }
}
