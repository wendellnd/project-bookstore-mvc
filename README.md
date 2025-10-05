# Project Bookstore MVC

## Checklist

- [x] Projeto compila (`mvn clean package`)
- [x] Endpoints REST testados manualmente (Postman/curl)
- [x] H2 console acessível (ex.: `/h2-console`)
- [x] `ResourceNotFoundException` implementada e tratada por `@ControllerAdvice`
- [x] DTOs implementados como `record`
- [x] Mapper implementado
- [x] Services expostos por interfaces
- [x] Cobertura Jacoco >= 50% (incluir relatório)
- [x] Pelo menos 1 teste de integração que valida um endpoint

## Como executar

```bash
mvn spring-boot:run
```

## Endpoints

### Authors

- `GET /authors` - Lista todos os autores
- `GET /authors/{id}` - Busca autor por ID
- `POST /authors` - Cria novo autor
- `PUT /authors` - Atualiza autor
- `DELETE /authors/{id}` - Remove autor
- `GET /authors/{id}/books` - Lista livros do autor

### Books

- `GET /books` - Lista todos os livros
- `GET /books/{id}` - Busca livro por ID
- `POST /books` - Cria novo livro
- `PUT /books` - Atualiza livro
- `DELETE /books/{id}` - Remove livro

## H2 Console

Acessível em: http://localhost:8080/h2-console

_JDBC URL:_ `jdbc:h2:mem:testdb`
_USER:_ `sa`
_PASSWORD:_ `(deixe em branco)`
