package com.fiap.project_bookstore_mvc.dto.In;

public record BookInDTO(
        Long id,
        String title,
        String isbn,
        Long authorId
) {
}
