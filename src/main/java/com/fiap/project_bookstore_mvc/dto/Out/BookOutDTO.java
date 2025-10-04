package com.fiap.project_bookstore_mvc.dto.Out;

public record BookOutDTO(
    Long id,
    String title,
    String isbn,
    AuthorOutDTO author
) {
}
