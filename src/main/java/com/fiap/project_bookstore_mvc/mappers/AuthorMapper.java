package com.fiap.project_bookstore_mvc.mappers;

import com.fiap.project_bookstore_mvc.dto.In.AuthorInDTO;
import com.fiap.project_bookstore_mvc.dto.Out.AuthorOutDTO;
import com.fiap.project_bookstore_mvc.entities.Author;

public final class AuthorMapper {

    public static AuthorOutDTO toOutDTO(Author author) {
        return new AuthorOutDTO(
            author.getId(),
            author.getName(),
            author.getEmail()
        );
    }

    public static Author toEntity(AuthorInDTO authorInDTO) {
        return new Author(
            authorInDTO.id(),
            authorInDTO.name(),
            authorInDTO.email()
        );
    }
}
